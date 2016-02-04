/*
 * Copyright (c) 2016 Deloitte MCS Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.deloittedigital.dropwizard.hikari;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import uk.co.deloittedigital.dropwizard.hikari.config.HikariConfiguration;
import uk.co.deloittedigital.dropwizard.hikari.config.HikariConfigurationProvider;
import uk.co.deloittedigital.dropwizard.testsupport.ConfigOverrides;
import uk.co.deloittedigital.dropwizard.testsupport.Targets;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.GenericType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static org.rnorth.visibleassertions.VisibleAssertions.*;
import static uk.co.deloittedigital.dropwizard.testsupport.Targets.localTarget;

public class HikariBundleIntegrationTest {

    @ClassRule
    public static final DropwizardAppRule<TestConfiguration> APP = new DropwizardAppRule<>(TestApp.class,
            ResourceHelpers.resourceFilePath("integration-test-config.yml"),
            ConfigOverrides.randomLocalPort(),
            ConfigOverrides.randomAdminPort());
    private static Client client;

    @BeforeClass
    public static void initializeClient() {
        client = new JerseyClientBuilder(APP.getEnvironment()).build("test client");
    }

    @Test
    public void applicationIsInitializedWithAConnectionPool() throws Exception {
        String response = localTarget(client, APP, "/").get(String.class);
        assertEquals("The resource returns the expected value to indicate that it has a DB connection", "OK", response);
    }

    @Test
    public void applicationAdminInterfaceExposesHikariMetrics() throws Exception {
        Map<String, Object> metrics = Targets.adminTarget(client, APP, "/metrics").get(new GenericType<Map<String, Object>>() {});
        Map<String, Object> gauges = (Map<String, Object>) metrics.get("gauges");
        assertTrue("The Dropwizard exposed metrics includes values reported by Hikari", gauges.containsKey("database.pool.IdleConnections"));
    }

    @Test
    public void applicationAdminInterfaceExposesHikariInHealthcheck() throws Exception {
        Map<String, Object> healthcheck = Targets.adminTarget(client, APP, "/healthcheck").get(new GenericType<Map<String, Object>>() {});
        Map<String, Object> connectivityCheck = (Map<String, Object>) healthcheck.get("database.pool.ConnectivityCheck");
        assertEquals("The Dropwizard exposed healthchecks includes values reported by Hikari", true, connectivityCheck.get("healthy"));
    }


    /**
     * This app is the system under test for these integation tests.
     */
    @Path("/")
    public static class TestApp extends Application<TestConfiguration> {

        private ManagedDataSource dataSource;

        @Override
        public void initialize(Bootstrap<TestConfiguration> bootstrap) {
            bootstrap.addBundle(new HikariBundle());
        }

        @Override
        public void run(TestConfiguration configuration, Environment environment) throws Exception {
            environment.jersey().register(this);
            dataSource = configuration.database.getManagedDataSource();
        }

        @GET
        @Path("/")
        public String testResource() throws SQLException {
            info("Resource at / hit");
            ResultSet resultSet = dataSource.getConnection().prepareStatement("SELECT 'OK';").executeQuery();
            resultSet.next();
            String result = resultSet.getString(1); // This should be the String 'OK'
            info("Resource at / returning result: " + result);


            return result; // Let our caller validate
        }
    }


    /**
     * This serves as the configuration respresentation for testing.
     */
    public static class TestConfiguration extends Configuration implements HikariConfigurationProvider {

        public HikariConfiguration database = new HikariConfiguration();

        @Override
        public HikariConfiguration getHikariConfiguration() {
            return database;
        }
    }
}

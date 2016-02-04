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

package uk.co.deloittedigital.dropwizard.hikari.config;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.zaxxer.hikari.HikariConfig;
import io.dropwizard.db.ManagedDataSource;
import lombok.Data;
import uk.co.deloittedigital.dropwizard.hikari.ManagedHikariPoolWrapper;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Configuration for HikariCP data source. See https://github.com/brettwooldridge/HikariCP/wiki/Configuration
 * for available settings.
 */
@Data
public class HikariConfiguration {

    private ManagedDataSource managedDataSource;

    public long connectionTimeoutMillis = TimeUnit.SECONDS.toMillis(30);
    public long validationTimeoutMillis = TimeUnit.SECONDS.toMillis(5);
    public long idleTimeoutMillis = TimeUnit.MINUTES.toMillis(10);
    public long leakDetectionThresholdMillis = 0;
    public long maxLifetimeMillis = TimeUnit.MINUTES.toMillis(30);

    public int maxPoolSize = 10;
    public int minIdle = -1;

    public String catalog;
    public String connectionInitSql;
    public String connectionTestQuery;
    public String dataSourceClassName;
    public String dataSourceJndiName;
    public String driverClassName;
    public String jdbcUrl;
    public String password;
    public String transactionIsolationName;
    public String username;
    public boolean autoCommit = true;
    public boolean readOnly = false;
    public boolean initializationFailFast = true;
    public boolean isolateInternalQueries = false;
    public boolean registerMbeans = false;
    public boolean allowPoolSuspension = false;

    public long healthCheckConnectionTimeoutMillis = TimeUnit.SECONDS.toMillis(1);

    public void buildDataSource(MetricRegistry metricRegistry, HealthCheckRegistry healthCheckRegistry, String name) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setConnectionTimeout(connectionTimeoutMillis);
        hikariConfig.setValidationTimeout(validationTimeoutMillis);
        hikariConfig.setIdleTimeout(idleTimeoutMillis);
        hikariConfig.setLeakDetectionThreshold(leakDetectionThresholdMillis);
        hikariConfig.setMaxLifetime(maxLifetimeMillis);
        hikariConfig.setMaximumPoolSize(maxPoolSize);

        if (minIdle > 0) {
            hikariConfig.setMinimumIdle(minIdle);
        }

        hikariConfig.setCatalog(catalog);
        hikariConfig.setConnectionInitSql(connectionInitSql);
        hikariConfig.setConnectionTestQuery(connectionTestQuery);
        hikariConfig.setDataSourceClassName(dataSourceClassName);
        hikariConfig.setDataSourceJNDI(dataSourceJndiName);
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setPassword(password);
        hikariConfig.setPoolName(name);
        hikariConfig.setTransactionIsolation(transactionIsolationName);
        hikariConfig.setUsername(username);
        hikariConfig.setAutoCommit(autoCommit);
        hikariConfig.setReadOnly(readOnly);
        hikariConfig.setInitializationFailFast(initializationFailFast);
        hikariConfig.setIsolateInternalQueries(isolateInternalQueries);
        hikariConfig.setRegisterMbeans(registerMbeans);
        hikariConfig.setAllowPoolSuspension(allowPoolSuspension);

        hikariConfig.setMetricRegistry(metricRegistry);
        hikariConfig.setHealthCheckRegistry(healthCheckRegistry);

        Properties healthCheckProperties = new Properties();
        healthCheckProperties.setProperty("connectivityCheckTimeoutMs", String.valueOf(healthCheckConnectionTimeoutMillis));
        hikariConfig.setHealthCheckProperties(healthCheckProperties);

        this.managedDataSource = new ManagedHikariPoolWrapper(hikariConfig);
    }

    public ManagedDataSource getManagedDataSource() {

        if (managedDataSource == null) {
            throw new IllegalStateException("DataSource has not been built yet - have you added the HikariBundle in your application initialize method?");
        }

        return managedDataSource;
    }

}

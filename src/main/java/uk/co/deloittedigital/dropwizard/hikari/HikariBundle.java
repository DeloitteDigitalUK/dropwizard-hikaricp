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

import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import uk.co.deloittedigital.dropwizard.hikari.config.HikariConfigurationProvider;

/**
 * Dropwizard bundle to add Hikari Connection Pool support to Jersey resources.
 *
 * Note that this bundle should be registered before any other bundles that depend on it.
 */
public class HikariBundle implements ConfiguredBundle<HikariConfigurationProvider> {
    @Override
    public void run(HikariConfigurationProvider configuration, Environment environment) throws Exception {
        // The Data Source is created by this call, and is retained as a field on the configuration object itself.
        // This is why this bundle has to be registered before any other bundles that might call
        configuration.getHikariConfiguration().buildDataSource(environment.metrics(), environment.healthChecks(), "database");
        environment.lifecycle().manage(configuration.getHikariConfiguration().getManagedDataSource());
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {

    }
}

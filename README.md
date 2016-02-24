# Dropwizard HikariCP bundle

[![Circle CI](https://circleci.com/gh/DeloitteDigitalUK/dropwizard-hikaricp.svg?style=svg)](https://circleci.com/gh/DeloitteDigitalUK/dropwizard-hikaricp)

## Overview

This Dropwizard bundle adds support for the excellent [HikariCP](https://github.com/brettwooldridge/HikariCP) database
connection pool library. This includes metrics and healthchecks support.

## Obtaining the library

### Maven

    <dependency>
        <groupId>uk.co.deloittedigital.dropwizard</groupId>
        <artifactId>dropwizard-hikaricp</artifactId>
        <version>1.0.0</version>
    </dependency>

### Gradle

    compile 'uk.co.deloittedigital.dropwizard:dropwizard-hikaricp:1.0.0'

## Usage

In your service's `initialize` method, add the bundle:

    bootstrap.addBundle(new HikariBundle());

This bundle should be registered **before** other bundles that use it - e.g. before the [Flyway](https://github.com/DeloitteDigitalUK/dropwizard-flyway) bundle.

Your service's configuration class should implement
`uk.co.deloittedigital.dropwizard.hikari.config.HikariConfigurationProvider`. See the
[Hikari documentation](https://github.com/brettwooldridge/HikariCP/wiki/Configuration) for the available configuration
settings and their behaviour.

## License

See [LICENSE](LICENSE.txt).

## Copyright

Copyright (c) 2015, 2016 Deloitte MCS Ltd.

See [AUTHORS](AUTHORS.txt) for contributors.
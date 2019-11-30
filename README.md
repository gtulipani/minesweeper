[![Build Status](https://travis-ci.com/gtulipani/minesweeper.svg?branch=master)](https://travis-ci.com/gtulipani/minesweeper)
# minesweeper
## Description
Microservice that handles the Server required to play the classic game [Minesweeper](https://en.wikipedia.org/wiki/Minesweeper_(video_game)).

## Building and running
### List Tasks
1. Navigate to the base folder
1. Execute `./gradlew tasks`

### Build
1. Navigate to the base folder
1. Execute `./gradlew build`

## Run
### Run Locally
1. Execute `./gradlew bootRun`

## Flyway
### About
Flyway is a database migration tool similar to Liquibase. It is recommended by Spring Boot.
See the documentation [here](http://flywaydb.org/). The migrations scripts are in SQL directly. Make sure that your SQL
scripts run both on MySQL and H2. H2's syntax is pretty flexible and will handle most MySQL specific instructions.
The project is configured to run the migration scripts on start.

### Configuration
Flyway migration can be disabled completely by disabling `flyway.enabled`. On certain occasions, a SQL script might be
changed after being run. Flyway validates the checksum of each migration and will report error on startup. Enable `flyway.repair` to correct this situation.

## CI
[Travis CI](https://travis-ci.org/) has been chosen as CI Software. It's already configured and runs all the UT for
every PR and Build (including `master`). The status can be found at the top of this file.

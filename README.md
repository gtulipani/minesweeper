[![Build Status](https://travis-ci.org/gtulipani/minesweeper.svg?branch=master)](https://travis-ci.org/gtulipani/minesweeper)
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

### Run in Heroku
1. The application includes a **Procfile** and a **system.properties** file which can be used to deploy the application in
[Heroku](https://www.heroku.com/). The only detail to take into account, is that a MySQL DB needs to be deployed for the
application to work.

## Technologies
### Spring
The Application is a standard [Spring Boot](https://spring.io/) Application and it's conformed by the following layers:
- **Controller**: handles all the incoming requests from the outside world. There is one of them: *GameController*.
- **Service**: handles all the logic from the incoming requests from the controllers. There are two of them: *GameService* and *GameCellService*.
- **DAO**: handles all the logic with the Repository. This layer abstracts the Transactionality needed to manage entities with Spring.
There are two of them: *GameDAO* and *GameCellDAO*.
- **Repository**: [CrudRespository](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html)
that handles the last communication layer between the DAOs and the Database. There are two of them: *GameRepository* and
*GameCellRepository*.

### MySQL
The chosen Database Engine is [MySQL](https://www.mysql.com/), a free relational DB model. By the moment it contains
only two table called `games` and `game_cells`. The first one contains the following columns:
```
id|created_on|last_modified|rows_quantity|columns_quantity|mines_quantity|status
```

The second one contains the following columns:
```
id|created_on|last_modified|game_id|row_number|col_number|cell_content|mines_around|cell_operation
```

### Flyway
#### About
Flyway is a database migration tool similar to Liquibase. It is recommended by Spring Boot.
See the documentation [here](http://flywaydb.org/). The migrations scripts are in SQL directly. Make sure that your SQL
scripts run both on MySQL and H2. H2's syntax is pretty flexible and will handle most MySQL specific instructions.
The project is configured to run the migration scripts on start.

#### Configuration
Flyway migration can be disabled completely by disabling `flyway.enabled`. On certain occasions, a SQL script might be
changed after being run. Flyway validates the checksum of each migration and will report error on startup. Enable `flyway.repair` to correct this situation.

### Orika Mapping
[Orika](https://orika-mapper.github.io/orika-docs/) is a framework used to convert different entities on a extensible, clear
and practical way. The mappers reduce the work necessary to convert between the necessary entities. E.g. to convert between a entity of type
*Game* to an entity of type *GameBean*, we just need to:
```
gameMapper.mapToBean(game);
``` 

### ObjectMother Pattern
The [ObjectMother](https://martinfowler.com/bliki/ObjectMother.html) is a Factory class used only for Unit Tests, and it's
really useful for avoiding duplicating code and to gain consistency across different classes under test. The main advantage
of it is that we can avoid duplicate code and the final result is a cleaner code block. Example from ObjectMother implementations
```java
@Test
public void testCreate() {
    GameBean requestGame = GameBeanMother.empty().build();
    Set<GameCellBean> gameCellBeans = Sets.newHashSet(GameCellBeanMother.mine().build());
    GameBean expectedResponse = GameBeanMother.basic().build();
    when(gameCellService.populateCells(requestGame)).thenReturn(gameCellBeans);
    when(gameDAO.create(requestGame, gameCellBeans)).thenReturn(expectedResponse);

    assertThat(gameService.create(requestGame)).isEqualTo(expectedResponse);
    verify(gameDAO, times(1)).create(requestGame, gameCellBeans);
    verifyNoMoreInteractions(gameDAO);
}
```

## Postman Collection
A [Postman Collection](https://www.getpostman.com/) has been included in the repository, containing all the existing endpoints
among with examples:
### POST /api/v1/games
API to create a new game. The id from the game among with the configuration is returned as part of the response. Expected input payload:
```
{
    "rows": 10,
    "columns": 10,
    "mines": 40
}
```

Example response payload:
```
{
    "id": 1,
    "rows": 10,
    "columns": 10,
    "mines": 40
}
```

### POST /api/v1/games/{gameId}/pause
API to pause an existing game. The id from an existing an active game must be included as part of the path. No body is required.

If the game is on status *FINISHED_WON* or *FINISHED_WON*, Error Code **404** is returned.

### POST /api/v1/games/{gameId}/resume
API to resume an existing game. The id from an existing paused game must be included as part of the path. No body is required.

If the game is on status *FINISHED_WON* or *FINISHED_WON*, Error Code **404** is returned.

### POST /api/v1/games/{gameId}/operation?cellOperation={cellOperation}&row={row}&column={column}
API to perform an operation on an existing active game. The id from it must be included as part of the path.
The row and column from the cell must be included as part of the request among with a valid *cellOperation*.

Three different type of *cellOperation* can be performed: *REVEALED*, *FLAGGED* or *QUESTION_MARKED*.

Example response payload with all the new cells to be revealed:
```
{
    "gameCellOperationStatus": "SUCCESS",
    "gameCellBeans": {
        ...
    }
}
```

## Swagger
The project includes the [Swagger](https://swagger.io/) plugin, that is useful to navigate the API requests and expected
responses. The [Swagger UI](https://swagger.io/tools/swagger-ui/) is available on `/swagger-ui.html` path, and can be used
to hit the Project APIs.

## Game Logic
The same way as the classic *minesweeper*, the game starts with its creation, when the mines are generated randomly on many places.
The user can perform any of the following three operations:
- *REVEALED*: when the player wants to reveal the content of cell.
- *FLAGGED*: when the player thinks there is mine on a given cell and wants to place a flag on it.
- *QUESTION_MARKED*: when the player is uncertain about the content of a cell and wants to place a question mark on it.
#### Game End
The Game ends in any of the following scenarios
- *GAME_LOST*: when the player reveals the position of a mine, then *GAME_LOST* status is returned after the operation, 
among with the positions of all the mines.
- *GAME_WON*: when the player successfully flagged all the mines and revealed all the rest of the cells.

## CI
[Travis CI](https://travis-ci.org/) has been chosen as CI Software. It's already configured and runs all the UT for
every PR and Build (including `master`). The status can be found at the top of this file.

## More Stats
The project includes a total of **169** Unit Tests.

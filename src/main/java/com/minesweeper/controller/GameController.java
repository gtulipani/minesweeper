package com.minesweeper.controller;

import javax.ws.rs.core.MediaType;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.minesweeper.bean.GameBean;
import com.minesweeper.bean.GameCellOperationResponse;
import com.minesweeper.enums.CellOperation;
import com.minesweeper.service.GameService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@AllArgsConstructor
@RestController
@Slf4j
@RequestMapping(value = "/api/v1/games", produces = MediaType.APPLICATION_JSON)
public class GameController {
	private final GameService gameService;

	@PostMapping(consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ApiOperation(
			value = "Creates a Game with the configuration passed as parameter",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON)
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Game successfully created", response = GameBean.class),
			@ApiResponse(code = 500, message = "Unexpected error")})
	public ResponseEntity<GameBean> create(@RequestBody GameBean gameBean) {
		log.info("Received request to create a new game with gameBean={}", gameBean);

		return ResponseEntity.status(HttpStatus.CREATED).body(gameService.create(gameBean));
	}

	@PostMapping(path = "/{gameId}/operation", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ApiOperation(
			value = "Performs an operation on a given cell from the Game",
			httpMethod = "POST",
			produces = MediaType.APPLICATION_JSON)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Operation successfully performed", response = GameCellOperationResponse.class),
			@ApiResponse(code = 400, message = "Invalid operation"),
			@ApiResponse(code = 400, message = "Invalid row and column"),
			@ApiResponse(code = 404, message = "No active Game found for that id"),
			@ApiResponse(code = 500, message = "Unexpected error")})
	public ResponseEntity<GameCellOperationResponse> performOperation(
			@PathVariable Long gameId, 
			@ApiParam(value = "Operation that is being performed", required = true) @RequestParam CellOperation cellOperation,
			@ApiParam(value = "Row from the cell", required = true) @RequestParam Long row,
			@ApiParam(value = "Column from the cell", required = true) @RequestParam Long column) {
		log.info("Received request to perform cellOperation={} on gameId={} row={} and column={}", gameId, cellOperation, row, column);

		return ResponseEntity.ok(gameService.performOperation(gameId, cellOperation, row, column));
	}

	@PostMapping(path = "/{gameId}/pause", produces = MediaType.APPLICATION_JSON)
	@ApiOperation(
			value = "Pauses an active Game",
			httpMethod = "POST")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Game successfully paused"),
			@ApiResponse(code = 404, message = "No active Game found for that id"),
			@ApiResponse(code = 500, message = "Unexpected error")})
	public ResponseEntity pause(@PathVariable Long gameId) {
		log.info("Received request to pause game with gameId={}", gameId);

		gameService.pause(gameId);
		return ResponseEntity.ok().build();
	}

	@PostMapping(path = "/{gameId}/resume", produces = MediaType.APPLICATION_JSON)
	@ApiOperation(
			value = "Resumes a paused Game",
			httpMethod = "POST")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Game successfully resumed"),
			@ApiResponse(code = 404, message = "No paused Game found for that id"),
			@ApiResponse(code = 500, message = "Unexpected error")})
	public ResponseEntity resume(@PathVariable Long gameId) {
		log.info("Received request to resume game with gameId={}", gameId);

		gameService.resume(gameId);
		return ResponseEntity.ok().build();
	}
}

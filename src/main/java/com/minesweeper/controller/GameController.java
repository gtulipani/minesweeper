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
import com.minesweeper.enums.CellOperation;
import com.minesweeper.service.GameService;

@AllArgsConstructor
@RestController
@Slf4j
@RequestMapping(value = "/games", produces = MediaType.APPLICATION_JSON)
public class GameController {
	private final GameService gameService;

	@PostMapping(consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public ResponseEntity<GameBean> create(@RequestBody GameBean gameBean) {
		log.info("Received request to create a new game with gameBean={}", gameBean);

		return ResponseEntity.status(HttpStatus.CREATED).body(gameService.create(gameBean));
	}

	@PostMapping(path = "/{gameId}/operation", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public ResponseEntity<GameBean> performOperation(@PathVariable Integer gameId,
										  @RequestParam CellOperation cellOperation,
										  @RequestParam Integer row,
										  @RequestParam Integer column) {
		log.info("Received request to perform cellOperation={} on gameId={} row={} and column={}", gameId, cellOperation, row, column);

		return ResponseEntity.ok().build();
	}
}

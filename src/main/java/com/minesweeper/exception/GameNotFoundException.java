package com.minesweeper.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class GameNotFoundException extends RuntimeException {
	private static final String ERROR_MESSAGE = "Game with id=%s not found";

	public GameNotFoundException(Long gameId) {
		super(String.format(ERROR_MESSAGE, gameId));
	}
}

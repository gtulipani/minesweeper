package com.minesweeper.exception;

public class GameNotFoundException extends RuntimeException {
	private static final String ERROR_MESSAGE = "Game with id=%s not found";

	public GameNotFoundException(Long gameId) {
		super(String.format(ERROR_MESSAGE, gameId));
	}
}

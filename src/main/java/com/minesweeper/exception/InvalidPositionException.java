package com.minesweeper.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidPositionException extends RuntimeException {
	private static final String ERROR_MESSAGE = "Invalid position with row=%s and column=%s";

	public InvalidPositionException(Long row, Long column) {
		super(String.format(ERROR_MESSAGE, row, column));
	}
}

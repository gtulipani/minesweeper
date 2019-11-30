package com.minesweeper.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.minesweeper.enums.CellOperation;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class CellOperationNotSupportedException extends RuntimeException {
	private static final String ERROR_MESSAGE = "Invalid Cell Operation=%s";

	public CellOperationNotSupportedException(CellOperation cellOperation) {
		super(String.format(ERROR_MESSAGE, cellOperation));
	}
}

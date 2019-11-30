package com.minesweeper.exception;

import lombok.Getter;

import com.minesweeper.bean.GameBean;

@Getter
public class MineExplodedException extends RuntimeException {
	private static final String ERROR_MESSAGE = "Mine exploded in row=%s, column=%s on game with id=%s";

	private final GameBean gameBean;

	public MineExplodedException(Long row, Long column, GameBean gameBean) {
		super(String.format(ERROR_MESSAGE, row, column, gameBean.getId()));
		this.gameBean = gameBean;
	}
}

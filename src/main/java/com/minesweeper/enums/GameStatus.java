package com.minesweeper.enums;

import java.util.EnumSet;

public enum GameStatus {
	PLAYING, PAUSED, FINISHED_WON, FINISHED_LOST;

	public static EnumSet<GameStatus> FINISHED_STATUS = EnumSet.of(FINISHED_WON, FINISHED_LOST);
}

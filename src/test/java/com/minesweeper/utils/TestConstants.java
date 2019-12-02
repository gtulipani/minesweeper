package com.minesweeper.utils;

import java.time.Instant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TestConstants {
	public static final Long ID = 1L;
	public static final Instant CREATED_ON = Instant.now();
	public static final Instant LAST_MODIFIED = Instant.now();
	public static final long ROWS_QUANTITY = 40;
	public static final long COLUMNS_QUANTITY = 30;
	public static final long NUMBER_ROW = 30;
	public static final long NUMBER_COLUMN = 20;
	public static final long MINE_ROW = 20;
	public static final long MINE_COLUMN = 10;
	public static final long MINES_QUANTITY = 5;
	public static final long MINES_AROUND = 4;
}

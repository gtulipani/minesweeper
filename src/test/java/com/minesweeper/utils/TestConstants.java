package com.minesweeper.utils;

import java.time.Instant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TestConstants {
	public static final Long ID = 1L;
	public static final Instant CREATED_ON = Instant.now();
	public static final Instant LAST_MODIFIED = Instant.now();
	public static final int ROWS = 20;
	public static final int COLUMNS = 10;
	public static final int MINES = 40; 
}

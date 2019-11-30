package com.minesweeper.mother;

import static com.minesweeper.utils.TestConstants.COLUMNS;
import static com.minesweeper.utils.TestConstants.CREATED_ON;
import static com.minesweeper.utils.TestConstants.ID;
import static com.minesweeper.utils.TestConstants.LAST_MODIFIED;
import static com.minesweeper.utils.TestConstants.MINES;
import static com.minesweeper.utils.TestConstants.ROWS;

import com.minesweeper.entity.GameConfiguration;

public class GameConfigurationMother {
	public static GameConfiguration.GameConfigurationBuilder basic() {
		return GameConfiguration.builder()
				.id(ID)
				.createdOn(CREATED_ON)
				.lastModified(LAST_MODIFIED)
				.rows((long) ROWS)
				.columns((long) COLUMNS)
				.mines((long) MINES);
	}
}

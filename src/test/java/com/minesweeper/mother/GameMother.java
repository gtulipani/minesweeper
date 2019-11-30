package com.minesweeper.mother;

import static com.minesweeper.utils.TestConstants.COLUMNS;
import static com.minesweeper.utils.TestConstants.CREATED_ON;
import static com.minesweeper.utils.TestConstants.ID;
import static com.minesweeper.utils.TestConstants.LAST_MODIFIED;
import static com.minesweeper.utils.TestConstants.MINES;
import static com.minesweeper.utils.TestConstants.ROWS;

import com.google.common.collect.Sets;
import com.minesweeper.entity.Game;

public class GameMother {
	public static Game.GameBuilder basic() {
		return Game.builder()
				.id(ID)
				.createdOn(CREATED_ON)
				.lastModified(LAST_MODIFIED)
				.rows(ROWS)
				.columns(COLUMNS)
				.mines(MINES)
				.gameCells(Sets.newHashSet(GameCellMother.mine().build()));
	}

	public static Game.GameBuilder withoutId() {
		return basic().id(null);
	}
}

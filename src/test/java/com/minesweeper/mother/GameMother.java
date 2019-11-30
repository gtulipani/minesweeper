package com.minesweeper.mother;

import static com.minesweeper.utils.TestConstants.CREATED_ON;
import static com.minesweeper.utils.TestConstants.ID;
import static com.minesweeper.utils.TestConstants.LAST_MODIFIED;

import com.minesweeper.entity.Game;

public class GameMother {
	public static Game.GameBuilder basic() {
		return Game.builder()
				.id(ID)
				.createdOn(CREATED_ON)
				.lastModified(LAST_MODIFIED)
				.gameConfiguration(GameConfigurationMother.basic().build());
	}

	public static Game.GameBuilder withoutId() {
		return Game.builder()
				.id(null)
				.createdOn(null)
				.lastModified(null)
				.gameConfiguration(GameConfigurationMother.basic().build());
	}
}

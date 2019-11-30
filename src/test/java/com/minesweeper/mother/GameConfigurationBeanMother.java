package com.minesweeper.mother;

import static com.minesweeper.utils.TestConstants.COLUMNS;
import static com.minesweeper.utils.TestConstants.ID;
import static com.minesweeper.utils.TestConstants.MINES;
import static com.minesweeper.utils.TestConstants.ROWS;

import com.minesweeper.bean.GameConfigurationBean;

public class GameConfigurationBeanMother {
	public static GameConfigurationBean.GameConfigurationBeanBuilder basic() {
		return GameConfigurationBean.builder()
				.id(ID)
				.rows(ROWS)
				.columns(COLUMNS)
				.mines(MINES);
	}
}

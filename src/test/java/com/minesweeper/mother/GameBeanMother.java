package com.minesweeper.mother;

import static com.minesweeper.utils.TestConstants.COLUMNS;
import static com.minesweeper.utils.TestConstants.ID;
import static com.minesweeper.utils.TestConstants.MINES;
import static com.minesweeper.utils.TestConstants.ROWS;

import com.google.common.collect.Sets;
import com.minesweeper.bean.GameBean;

public class GameBeanMother {
	public static GameBean.GameBeanBuilder basic() {
		return GameBean.builder()
				.id(ID)
				.rows(ROWS)
				.columns(COLUMNS)
				.mines(MINES)
				.gameCells(Sets.newHashSet(GameCellBeanMother.mine().build()));
	}

	public static GameBean.GameBeanBuilder empty() {
		return basic()
				.id(null)
				.gameCells(null);
	}
}

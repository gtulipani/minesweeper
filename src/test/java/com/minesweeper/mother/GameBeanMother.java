package com.minesweeper.mother;

import static com.minesweeper.utils.TestConstants.COLUMNS_QUANTITY;
import static com.minesweeper.utils.TestConstants.ID;
import static com.minesweeper.utils.TestConstants.MINES_QUANTITY;
import static com.minesweeper.utils.TestConstants.ROWS_QUANTITY;

import com.google.common.collect.Sets;
import com.minesweeper.bean.GameBean;

public class GameBeanMother {
	public static GameBean.GameBeanBuilder basic() {
		return GameBean.builder()
				.id(ID)
				.rows(ROWS_QUANTITY)
				.columns(COLUMNS_QUANTITY)
				.mines(MINES_QUANTITY)
				.gameCells(Sets.newHashSet(GameCellBeanMother.mine().build()));
	}

	public static GameBean.GameBeanBuilder empty() {
		return basic()
				.id(null)
				.gameCells(null);
	}
}

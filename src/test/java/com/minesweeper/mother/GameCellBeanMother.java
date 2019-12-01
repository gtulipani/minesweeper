package com.minesweeper.mother;

import static com.minesweeper.utils.TestConstants.MINE_COLUMNS;
import static com.minesweeper.utils.TestConstants.MINES_AROUND;
import static com.minesweeper.utils.TestConstants.MINE_ROW;
import static com.minesweeper.utils.TestConstants.NUMBER_COLUMN;
import static com.minesweeper.utils.TestConstants.NUMBER_ROW;

import com.minesweeper.bean.GameCellBean;
import com.minesweeper.enums.CellContent;
import com.minesweeper.enums.CellOperation;

public class GameCellBeanMother {
	public static GameCellBean.GameCellBeanBuilder mine() {
		return GameCellBean.builder()
				.row(MINE_ROW)
				.column(MINE_COLUMNS)
				.cellContent(CellContent.MINE)
				.cellOperation(CellOperation.NONE);
	}

	public static GameCellBean.GameCellBeanBuilder number() {
		return GameCellBean.builder()
				.row(NUMBER_ROW)
				.column(NUMBER_COLUMN)
				.cellContent(CellContent.NUMBER)
				.minesAround(MINES_AROUND)
				.cellOperation(CellOperation.NONE);
	}
}

package com.minesweeper.mother;

import static com.minesweeper.utils.TestConstants.MINE_COLUMN;
import static com.minesweeper.utils.TestConstants.MINE_ROW;
import static com.minesweeper.utils.TestConstants.NUMBER_COLUMN;
import static com.minesweeper.utils.TestConstants.NUMBER_ROW;

import com.minesweeper.entity.GameCell;
import com.minesweeper.enums.CellContent;
import com.minesweeper.enums.CellOperation;

public class GameCellMother {
	public static GameCell.GameCellBuilder mine() {
		return GameCell.builder()
				.row(MINE_ROW)
				.column(MINE_COLUMN)
				.cellContent(CellContent.MINE)
				.cellOperation(CellOperation.NONE);
	}

	public static GameCell.GameCellBuilder number() {
		return GameCell.builder()
				.row(NUMBER_ROW)
				.column(NUMBER_COLUMN)
				.cellContent(CellContent.NUMBER)
				.cellOperation(CellOperation.NONE);
	}
}

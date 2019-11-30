package com.minesweeper.mother;

import static com.minesweeper.utils.TestConstants.MINE_COLUMNS;
import static com.minesweeper.utils.TestConstants.MINE_ROW;

import com.minesweeper.entity.GameCell;
import com.minesweeper.enums.CellContent;
import com.minesweeper.enums.CellOperation;

public class GameCellMother {
	public static GameCell.GameCellBuilder mine() {
		return GameCell.builder()
				.row(MINE_ROW)
				.column(MINE_COLUMNS)
				.cellContent(CellContent.MINE)
				.cellOperation(CellOperation.NONE);
	}
}

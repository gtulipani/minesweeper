package com.minesweeper.mother;

import static com.minesweeper.utils.TestConstants.COLUMNS;
import static com.minesweeper.utils.TestConstants.ROWS;

import com.minesweeper.entity.GameCell;
import com.minesweeper.enums.CellContent;
import com.minesweeper.enums.CellOperation;

public class GameCellMother {
	public static GameCell.GameCellBuilder mine() {
		return GameCell.builder()
				.row(ROWS)
				.column(COLUMNS)
				.cellContent(CellContent.MINE)
				.cellOperation(CellOperation.NONE);
	}
}

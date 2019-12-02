package com.minesweeper.dao;

import com.minesweeper.enums.CellOperation;

public interface GameCellDAO {
	void updateCellOperationById(CellOperation cellOperation, Long id);

	void updateCellOperationAndMinesAroundById(CellOperation cellOperation, Long minesAround, Long id);
}

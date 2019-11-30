package com.minesweeper.service;

import com.minesweeper.bean.GameBean;
import com.minesweeper.bean.GameCellOperationResponse;
import com.minesweeper.enums.CellOperation;

public interface GameService {
	GameBean create(GameBean gameBean);

	GameCellOperationResponse performOperation(Long gameId, CellOperation cellOperation, Long row, Long column);
}

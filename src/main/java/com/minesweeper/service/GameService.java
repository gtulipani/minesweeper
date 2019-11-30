package com.minesweeper.service;

import java.util.List;

import com.minesweeper.bean.GameBean;
import com.minesweeper.bean.GameCellBean;
import com.minesweeper.enums.CellOperation;

public interface GameService {
	GameBean create(GameBean gameBean);

	List<GameCellBean> performOperation(Long gameId, CellOperation cellOperation, Long row, Long column);
}

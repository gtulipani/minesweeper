package com.minesweeper.service;

import java.util.List;
import java.util.Set;

import com.minesweeper.bean.GameBean;
import com.minesweeper.bean.GameCellBean;
import com.minesweeper.enums.CellOperation;

public interface GameCellService {
	Set<GameCellBean> generateRandomMines(GameBean gameBean);

	List<GameCellBean> performOperation(GameBean gameBean, CellOperation cellOperation, Long row, Long column);
}

package com.minesweeper.service;

import java.util.Set;

import com.minesweeper.bean.GameBean;
import com.minesweeper.bean.GameCellBean;
import com.minesweeper.bean.GameCellOperation;

public interface GameCellService {
	Set<GameCellBean> populateCells(GameBean gameBean);

	Set<GameCellBean> performOperation(GameCellOperation gameCellOperation);
}

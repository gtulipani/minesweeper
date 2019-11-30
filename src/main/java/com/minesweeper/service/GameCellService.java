package com.minesweeper.service;

import java.util.Set;

import com.minesweeper.bean.GameBean;
import com.minesweeper.bean.GameCellBean;

public interface GameCellService {
	Set<GameCellBean> generateRandomMines(GameBean gameBean);
}

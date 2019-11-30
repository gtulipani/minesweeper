package com.minesweeper.component;

import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;
import com.minesweeper.bean.GameCellBean;
import com.minesweeper.enums.CellContent;

@Component
public class GameCellHelper {
	/**
	 * Private method that checks if the {@link GameCellBean} passed as parameter is a {@link CellContent#MINE}
	 */
	@VisibleForTesting
	public boolean isMine(GameCellBean gameCellBean) {
		return CellContent.MINE.equals(gameCellBean.getCellContent());
	}
}

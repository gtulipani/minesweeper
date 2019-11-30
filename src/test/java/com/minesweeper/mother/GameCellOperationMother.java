package com.minesweeper.mother;

import com.minesweeper.bean.GameCellOperation;
import com.minesweeper.enums.CellOperation;

public class GameCellOperationMother {
	public static GameCellOperation.GameCellOperationBuilder revealed() {
		return GameCellOperation.builder()
				.cellOperation(CellOperation.REVEALED)
				.gameBean(GameBeanMother.basic().build());
	}
}

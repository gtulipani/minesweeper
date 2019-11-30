package com.minesweeper.mother;

import com.google.common.collect.Sets;
import com.minesweeper.bean.GameCellOperationResponse;
import com.minesweeper.enums.GameCellOperationStatus;

public class GameCellOperationResponseMother {
	public static GameCellOperationResponse.GameCellOperationResponseBuilder success() {
		return GameCellOperationResponse.builder()
				.gameCellOperationStatus(GameCellOperationStatus.SUCCESS)
				.gameCellBeans(Sets.newHashSet(GameCellBeanMother.number().build()));
	}

	public static GameCellOperationResponse.GameCellOperationResponseBuilder gameLost() {
		return GameCellOperationResponse.builder()
				.gameCellOperationStatus(GameCellOperationStatus.GAME_LOST)
				.gameCellBeans(Sets.newHashSet(GameCellBeanMother.mine().build()));
	}
}

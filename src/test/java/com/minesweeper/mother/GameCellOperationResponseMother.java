package com.minesweeper.mother;

import java.util.Collections;

import com.minesweeper.bean.GameCellOperationResponse;
import com.minesweeper.enums.GameCellOperationStatus;

public class GameCellOperationResponseMother {
	public static GameCellOperationResponse.GameCellOperationResponseBuilder success() {
		return GameCellOperationResponse.builder()
				.gameCellOperationStatus(GameCellOperationStatus.SUCCESS)
				.gameCellBeans(Collections.singletonList(GameCellBeanMother.number().build()));
	}

	public static GameCellOperationResponse.GameCellOperationResponseBuilder gameLost() {
		return GameCellOperationResponse.builder()
				.gameCellOperationStatus(GameCellOperationStatus.GAME_LOST)
				.gameCellBeans(Collections.singletonList(GameCellBeanMother.mine().build()));
	}
}

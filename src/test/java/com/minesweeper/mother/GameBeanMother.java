package com.minesweeper.mother;

import static com.minesweeper.utils.TestConstants.ID;

import com.minesweeper.bean.GameBean;

public class GameBeanMother {
	public static GameBean.GameBeanBuilder basic() {
		return GameBean.builder()
				.id(ID)
				.gameConfiguration(GameConfigurationBeanMother.basic().build());
	}

	public static GameBean.GameBeanBuilder empty() {
		return GameBean.builder()
				.id(null)
				.gameConfiguration(null);
	}
}
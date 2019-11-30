package com.minesweeper.mother;

import static com.minesweeper.utils.TestConstants.ID;

import com.minesweeper.bean.GameBean;

public class GameBeanMother {
	public static GameBean.GameBeanBuilder basic() {
		return GameBean.builder()
				.id(ID);
	}
}

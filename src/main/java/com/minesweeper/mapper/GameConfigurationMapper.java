package com.minesweeper.mapper;

import com.minesweeper.bean.GameConfigurationBean;
import com.minesweeper.entity.GameConfiguration;

public interface GameConfigurationMapper {
	GameConfigurationBean mapToBean(GameConfiguration gameConfiguration);

	GameConfiguration mapToEntity(GameConfigurationBean gameConfigurationBean);
}

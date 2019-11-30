package com.minesweeper.mapper;

import com.minesweeper.bean.GameBean;
import com.minesweeper.entity.Game;

public interface GameMapper {
	GameBean mapToBean(Game game);

	Game mapToEntity(GameBean gameBean);
}

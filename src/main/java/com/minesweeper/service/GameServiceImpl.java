package com.minesweeper.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minesweeper.bean.GameBean;
import com.minesweeper.entity.Game;
import com.minesweeper.entity.GameConfiguration;
import com.minesweeper.mapper.GameConfigurationMapper;
import com.minesweeper.mapper.GameMapper;
import com.minesweeper.repository.GameConfigurationRepository;
import com.minesweeper.repository.GameRepository;

@AllArgsConstructor
@Service
@Slf4j
public class GameServiceImpl implements GameService {
	private final GameMapper gameMapper;
	private final GameConfigurationMapper gameConfigurationMapper;
	private final GameRepository gameRepository;
	private final GameConfigurationRepository gameConfigurationRepository;
	
	@Transactional
	@Override
	public GameBean create(GameBean gameBean) {
		GameConfiguration gameConfiguration = gameConfigurationMapper.mapToEntity(gameBean.getGameConfiguration());
		gameConfiguration = gameConfigurationRepository.save(gameConfiguration);

		Game game = gameMapper.mapToEntity(gameBean);
		game.setGameConfiguration(gameConfiguration);

		game = gameRepository.save(game);

		return gameMapper.mapToBean(game);
	}
}

package com.minesweeper.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minesweeper.bean.GameBean;
import com.minesweeper.entity.Game;
import com.minesweeper.mapper.GameMapper;
import com.minesweeper.repository.GameRepository;

@Service
@Slf4j
public class GameServiceImpl implements GameService {
	private final GameMapper gameMapper;
	private final GameRepository gameRepository;

	@Autowired
	public GameServiceImpl(GameMapper gameMapper, GameRepository gameRepository) {
		this.gameMapper = gameMapper;
		this.gameRepository = gameRepository;
	}
	
	@Transactional
	@Override
	public GameBean create(GameBean gameBean) {
		Game game = gameMapper.mapToEntity(gameBean);

		Game result = gameRepository.save(game);

		return gameMapper.mapToBean(result);
	}
}

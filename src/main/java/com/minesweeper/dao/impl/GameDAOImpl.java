package com.minesweeper.dao.impl;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.minesweeper.bean.GameBean;
import com.minesweeper.bean.GameCellBean;
import com.minesweeper.dao.GameDAO;
import com.minesweeper.entity.Game;
import com.minesweeper.entity.GameCell;
import com.minesweeper.enums.GameStatus;
import com.minesweeper.mapper.GameCellMapper;
import com.minesweeper.mapper.GameMapper;
import com.minesweeper.repository.GameCellRepository;
import com.minesweeper.repository.GameRepository;

@Slf4j
@AllArgsConstructor
@Component
public class GameDAOImpl implements GameDAO {
	private final GameRepository gameRepository;
	private final GameCellRepository gameCellRepository;
	private final GameMapper gameMapper;
	private final GameCellMapper gameCellMapper;

	@Override
	@Transactional
	public GameBean create(GameBean gameBean, Set<GameCellBean> gameCellBeans) {
		// Save GameCells
		Set<GameCell> gameCells = gameCellMapper.mapToEntity(gameCellBeans);
		gameCells = new HashSet<>(gameCellRepository.saveAll(gameCells));

		// Save Game
		Game game = gameMapper.mapToEntity(gameBean);
		game.setGameStatus(GameStatus.PLAYING);
		game.setGameCells(gameCells);
		game = gameRepository.save(game);

		return gameMapper.mapToBean(game);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<GameBean> findById(Long gameId) {
		return gameRepository.findById(gameId)
				.map(gameMapper::mapToBean);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<GameBean> findByIdAndGameStatusNotIn(Long gameId, Set<GameStatus> gameStatus) {
		return gameRepository.findByIdAndGameStatusNotIn(gameId, gameStatus)
				.map(gameMapper::mapToBean);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<GameBean> findByIdAndGameStatusIs(Long gameId, GameStatus gameStatus) {
		return gameRepository.findByIdAndGameStatusIs(gameId, gameStatus)
				.map(gameMapper::mapToBean);
	}

	@Override
	@Transactional
	public void updateGameStatusById(GameStatus gameStatus, Long gameId) {
		log.info("Updated game status to {} for game with id={}", gameStatus, gameId);
		gameRepository.updateGameStatusById(gameStatus, gameId);
	}
}

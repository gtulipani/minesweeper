package com.minesweeper.service.impl;

import static com.minesweeper.enums.GameStatus.FINISHED_STATUS;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minesweeper.bean.GameBean;
import com.minesweeper.bean.GameCellBean;
import com.minesweeper.bean.GameCellOperation;
import com.minesweeper.bean.GameCellOperationResponse;
import com.minesweeper.component.GameCellHelper;
import com.minesweeper.entity.Game;
import com.minesweeper.entity.GameCell;
import com.minesweeper.enums.CellOperation;
import com.minesweeper.enums.GameCellOperationStatus;
import com.minesweeper.enums.GameStatus;
import com.minesweeper.exception.GameNotFoundException;
import com.minesweeper.exception.MineExplodedException;
import com.minesweeper.mapper.GameCellMapper;
import com.minesweeper.mapper.GameMapper;
import com.minesweeper.repository.GameCellRepository;
import com.minesweeper.repository.GameRepository;
import com.minesweeper.service.GameCellService;
import com.minesweeper.service.GameService;

@AllArgsConstructor
@Service
@Slf4j
public class GameServiceImpl implements GameService {
	private final GameMapper gameMapper;
	private final GameCellMapper gameCellMapper;
	private final GameRepository gameRepository;
	private final GameCellRepository gameCellRepository;
	private final GameCellService gameCellService;
	private final GameCellHelper gameCellHelper;

	@Override
	public GameBean create(GameBean gameBean) {
		Set<GameCellBean> gameCells = gameCellService.populateCells(gameBean);
		return createInternal(gameBean, gameCells);
	}

	/**
	 * Method is public in order to have Transactional scope. Logic in different method because
	 * {@link GameCellService#populateCells(GameBean)} could be really memory-intensive and we don't want to leave the
	 * transactional scope open during that time.
	 */
	@Transactional
	public GameBean createInternal(GameBean gameBean, Set<GameCellBean> gameCellBeans) {
		// Create Game
		Game game = gameMapper.mapToEntity(gameBean);
		game.setGameStatus(GameStatus.PLAYING);
		game = gameRepository.save(game);

		// Create GameCells
		Set<GameCell> gameCells = gameCellMapper.mapToEntity(gameCellBeans);
		gameCells = new HashSet<>(gameCellRepository.saveAll(gameCells));

		// Update game with nested entity
		game.setGameCells(gameCells);
		game = gameRepository.save(game);

		return gameMapper.mapToBean(game);
	}

	@Override
	@Transactional
	public GameCellOperationResponse performOperation(Long gameId, CellOperation cellOperation, Long row, Long column) {
		GameBean gameBean = gameRepository.findByIdAndGameStatusIs(gameId, GameStatus.PLAYING)
				.map(gameMapper::mapToBean)
				.orElseThrow(() -> new GameNotFoundException(gameId));
		GameCellOperation gameCellOperation = GameCellOperation.builder()
				.cellOperation(cellOperation)
				.gameBean(gameBean)
				.row(row)
				.column(column)
				.build();

		try {
			return GameCellOperationResponse.builder()
					.gameCellOperationStatus(GameCellOperationStatus.SUCCESS)
					.gameCellBeans(gameCellService.performOperation(gameCellOperation))
					.build();
		} catch (MineExplodedException e) {
			log.info("A mine has exploded in position with row={} and column={} in gameId={}",
					row,
					column,
					gameId);
			gameRepository.updateGameStatusById(GameStatus.FINISHED_LOST, gameId);
			log.info("Updated game status to %s for status with id=%s", GameStatus.FINISHED_LOST, gameId);
			return GameCellOperationResponse.builder()
					.gameCellOperationStatus(GameCellOperationStatus.GAME_LOST)
					.gameCellBeans(gameBean.getGameCells().stream()
							.filter(gameCellHelper::isMine)
							.collect(Collectors.toSet()))
					.build();
		}
	}

	@Override
	@Transactional
	public void pause(Long gameId) {
		gameRepository.findByIdAndGameStatusNotIn(gameId, FINISHED_STATUS)
				.orElseThrow(() -> new GameNotFoundException(gameId));
		
		gameRepository.updateGameStatusById(GameStatus.PAUSED, gameId);
	}

	@Override
	@Transactional
	public void resume(Long gameId) {
		gameRepository.findByIdAndGameStatusNotIn(gameId, FINISHED_STATUS)
				.orElseThrow(() -> new GameNotFoundException(gameId));

		gameRepository.updateGameStatusById(GameStatus.PLAYING, gameId);
	}
}

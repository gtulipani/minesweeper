package com.minesweeper.service.impl;

import static com.minesweeper.enums.GameStatus.FINISHED_STATUS;

import java.util.Set;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.google.common.annotations.VisibleForTesting;
import com.minesweeper.bean.GameBean;
import com.minesweeper.bean.GameCellBean;
import com.minesweeper.bean.GameCellOperation;
import com.minesweeper.bean.GameCellOperationResponse;
import com.minesweeper.component.GameCellHelper;
import com.minesweeper.dao.GameDAO;
import com.minesweeper.enums.CellContent;
import com.minesweeper.enums.CellOperation;
import com.minesweeper.enums.GameCellOperationStatus;
import com.minesweeper.enums.GameStatus;
import com.minesweeper.exception.GameNotFoundException;
import com.minesweeper.exception.MineExplodedException;
import com.minesweeper.service.GameCellService;
import com.minesweeper.service.GameService;

@AllArgsConstructor
@Service
@Slf4j
public class GameServiceImpl implements GameService {
	private final GameDAO gameDAO;
	private final GameCellService gameCellService;
	private final GameCellHelper gameCellHelper;

	@Override
	public GameBean create(GameBean gameBean) {
		Set<GameCellBean> gameCells = gameCellService.populateCells(gameBean);
		return gameDAO.create(gameBean, gameCells);
	}

	@Override
	public GameCellOperationResponse performOperation(Long gameId, CellOperation cellOperation, Long row, Long column) {
		GameBean gameBean = gameDAO.findByIdAndGameStatusIs(gameId, GameStatus.PLAYING)
				.orElseThrow(() -> new GameNotFoundException(gameId));
		GameCellOperation gameCellOperation = GameCellOperation.builder()
				.cellOperation(cellOperation)
				.gameBean(gameBean)
				.row(row)
				.column(column)
				.build();

		try {
			Set<GameCellBean> newCells = gameCellService.performOperation(gameCellOperation);

			if (gameIsFinished(gameBean.getId())) {
				log.info("All mines have been flagged and all numbers have been revealed. Game with id=%s finished.", gameBean.getId());
				gameDAO.updateGameStatusById(GameStatus.FINISHED_WON, gameId);
				return GameCellOperationResponse.builder()
						.gameCellOperationStatus(GameCellOperationStatus.GAME_WON)
						.build();
			}

			return GameCellOperationResponse.builder()
					.gameCellOperationStatus(GameCellOperationStatus.SUCCESS)
					.gameCellBeans(newCells)
					.build();
		} catch (MineExplodedException e) {
			log.info("A mine has exploded in position with row={} and column={} in gameId={}",
					row,
					column,
					gameId);
			gameDAO.updateGameStatusById(GameStatus.FINISHED_LOST, gameId);
			return GameCellOperationResponse.builder()
					.gameCellOperationStatus(GameCellOperationStatus.GAME_LOST)
					.gameCellBeans(gameBean.getGameCells().stream()
							.filter(gameCellHelper::isMine)
							.collect(Collectors.toSet()))
					.build();
		}
	}

	@Override
	public void pause(Long gameId) {
		gameDAO.findByIdAndGameStatusNotIn(gameId, FINISHED_STATUS)
				.orElseThrow(() -> new GameNotFoundException(gameId));

		gameDAO.updateGameStatusById(GameStatus.PAUSED, gameId);
	}

	@Override
	public void resume(Long gameId) {
		gameDAO.findByIdAndGameStatusNotIn(gameId, FINISHED_STATUS)
				.orElseThrow(() -> new GameNotFoundException(gameId));

		gameDAO.updateGameStatusById(GameStatus.PLAYING, gameId);
	}

	@VisibleForTesting
	boolean gameIsFinished(Long gameId) {
		return gameDAO.findById(gameId)
				.map(GameBean::getGameCells)
				.orElseThrow(() -> new GameNotFoundException(gameId))
				.stream()
				.noneMatch(gameCell -> isMineAndNotFlagged(gameCell) || isNumberAndNotRevealed(gameCell));
	}

	/**
	 * Private method that checks if the {@link GameCellBean} passed as parameter is a MINE and it's not FLAGGED
	 */
	@VisibleForTesting
	boolean isMineAndNotFlagged(GameCellBean gameCellBean) {
		return CellContent.MINE.equals(gameCellBean.getCellContent()) && !CellOperation.FLAGGED.equals(gameCellBean.getCellOperation());
	}

	/**
	 * Private method that checks if the {@link GameCellBean} passed as parameter is a MINE and it's not FLAGGED
	 */
	@VisibleForTesting
	boolean isNumberAndNotRevealed(GameCellBean gameCellBean) {
		return CellContent.NUMBER.equals(gameCellBean.getCellContent()) && !CellOperation.REVEALED.equals(gameCellBean.getCellOperation());
	}
}

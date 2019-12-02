package com.minesweeper.service.impl;

import static com.minesweeper.enums.GameStatus.FINISHED_STATUS;
import static com.minesweeper.utils.TestConstants.ID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;
import com.minesweeper.bean.GameBean;
import com.minesweeper.bean.GameCellBean;
import com.minesweeper.bean.GameCellOperation;
import com.minesweeper.bean.GameCellOperationResponse;
import com.minesweeper.component.GameCellHelper;
import com.minesweeper.dao.GameDAO;
import com.minesweeper.enums.CellOperation;
import com.minesweeper.enums.GameStatus;
import com.minesweeper.exception.GameNotFoundException;
import com.minesweeper.exception.MineExplodedException;
import com.minesweeper.mother.GameBeanMother;
import com.minesweeper.mother.GameCellBeanMother;
import com.minesweeper.mother.GameCellOperationMother;
import com.minesweeper.mother.GameCellOperationResponseMother;
import com.minesweeper.service.GameCellService;

public class GameServiceImplTest {
	@Mock
	private GameDAO gameDAO;
	@Mock
	private GameCellService gameCellService;
	@Mock
	private GameCellHelper gameCellHelper;

	private GameServiceImpl gameService;

	@BeforeMethod
	public void setup() {
		MockitoAnnotations.initMocks(this);
		gameService = new GameServiceImpl(gameDAO, gameCellService, gameCellHelper);
	}

	@Test
	public void testCreate() {
		GameBean requestGame = GameBeanMother.empty().build();
		Set<GameCellBean> gameCellBeans = Sets.newHashSet(GameCellBeanMother.mine().build());
		GameBean expectedResponse = GameBeanMother.basic().build();
		when(gameCellService.populateCells(requestGame)).thenReturn(gameCellBeans);
		when(gameDAO.create(requestGame, gameCellBeans)).thenReturn(expectedResponse);

		assertThat(gameService.create(requestGame)).isEqualTo(expectedResponse);
		verify(gameDAO, times(1)).create(requestGame, gameCellBeans);
		verifyNoMoreInteractions(gameDAO);
	}

	@Test
	public void testPerformOperation() {
		GameBean gameBean = GameBeanMother.basic().build();
		Long gameId = gameBean.getId();
		CellOperation cellOperation = CellOperation.REVEALED;
		Long row = 1L;
		Long column = 1L;
		Set<GameCellBean> cellBeans = Sets.newHashSet(GameCellBeanMother.number().build());
		GameCellOperation gameCellOperation = GameCellOperationMother.revealed()
				.row(row)
				.column(column)
				.build();
		GameCellOperationResponse expectedResponse = GameCellOperationResponseMother.success().build();
		when(gameDAO.findByIdAndGameStatusIs(gameId, GameStatus.PLAYING)).thenReturn(Optional.of(gameBean));
		when(gameDAO.findById(gameId)).thenReturn(Optional.of(gameBean));
		when(gameCellService.performOperation(gameCellOperation)).thenReturn(cellBeans);

		assertThat(gameService.performOperation(gameId, cellOperation, row, column)).isEqualTo(expectedResponse);
		verify(gameDAO, times(1)).findByIdAndGameStatusIs(gameId, GameStatus.PLAYING);
		verify(gameDAO, times(1)).findById(gameId);
		verifyNoMoreInteractions(gameDAO);
	}

	@Test
	public void testPerformOperation_gameIsFinished() {
		GameBean gameBeanBeforeOperation = GameBeanMother.basic().build();
		Long gameId = gameBeanBeforeOperation.getId();
		CellOperation cellOperation = CellOperation.REVEALED;
		Long row = 1L;
		Long column = 1L;
		Set<GameCellBean> cellBeans = Sets.newHashSet(GameCellBeanMother.number().build());
		GameCellOperation gameCellOperation = GameCellOperationMother.revealed()
				.row(row)
				.column(column)
				.build();
		GameCellBean revealedNumber = GameCellBeanMother.number().cellOperation(CellOperation.REVEALED).build();
		GameCellBean flaggedMine = GameCellBeanMother.mine().cellOperation(CellOperation.FLAGGED).build();
		GameBean gameBeanAfterOperation = GameBeanMother.basic()
				.gameCells(Sets.newHashSet(revealedNumber, flaggedMine))
				.build();
		GameCellOperationResponse expectedResponse = GameCellOperationResponseMother.gameWon().build();
		when(gameDAO.findByIdAndGameStatusIs(gameId, GameStatus.PLAYING)).thenReturn(Optional.of(gameBeanBeforeOperation));
		when(gameCellService.performOperation(gameCellOperation)).thenReturn(cellBeans);
		when(gameDAO.findById(gameId)).thenReturn(Optional.of(gameBeanAfterOperation));

		assertThat(gameService.performOperation(gameId, cellOperation, row, column)).isEqualTo(expectedResponse);
		verify(gameDAO, times(1)).findByIdAndGameStatusIs(gameId, GameStatus.PLAYING);
		verify(gameDAO, times(1)).findById(gameId);
		verify(gameDAO, times(1)).updateGameStatusById(GameStatus.FINISHED_WON, gameBeanAfterOperation.getId());
		verifyNoMoreInteractions(gameDAO);
	}

	@Test
	public void testPerformOperation_whenMineExplodedExceptionIsThrown_returnGameLostWithListOfMines() {
		GameBean gameBean = GameBeanMother.basic().build();
		GameCellBean number = GameCellBeanMother.number().build();
		GameCellBean mine = GameCellBeanMother.mine().build();
		Long gameId = gameBean.getId();
		CellOperation cellOperation = CellOperation.REVEALED;
		Long row = 1L;
		Long column = 1L;
		GameCellOperationResponse expectedResponse = GameCellOperationResponseMother.gameLost()
				.gameCellBeans(Sets.newHashSet(mine))
				.build();
		when(gameDAO.findByIdAndGameStatusIs(gameId, GameStatus.PLAYING)).thenReturn(Optional.of(gameBean));
		when(gameCellService.performOperation(any(GameCellOperation.class))).thenThrow(new MineExplodedException(row, column, gameBean));
		when(gameCellHelper.isMine(number)).thenReturn(false);
		when(gameCellHelper.isMine(mine)).thenReturn(true);

		assertThat(gameService.performOperation(gameId, cellOperation, row, column)).isEqualTo(expectedResponse);
		verify(gameDAO, times(1)).findByIdAndGameStatusIs(gameId, GameStatus.PLAYING);
		verify(gameDAO, times(1)).updateGameStatusById(GameStatus.FINISHED_LOST, gameId);
		verifyNoMoreInteractions(gameDAO);
	}

	@Test
	public void testPause() {
		GameBean gameBean = GameBeanMother.basic().build();
		Long gameId = gameBean.getId();
		when(gameDAO.findByIdAndGameStatusNotIn(gameId, FINISHED_STATUS)).thenReturn(Optional.of(gameBean));

		assertThatCode(() -> gameService.pause(gameId)).doesNotThrowAnyException();
		verify(gameDAO, times(1)).findByIdAndGameStatusNotIn(gameId, FINISHED_STATUS);
		verify(gameDAO, times(1)).updateGameStatusById(GameStatus.PAUSED, gameId);
		verifyNoMoreInteractions(gameDAO);
	}

	@Test
	public void testPause_whenGameIsFinishedOrNotFound_throwsGameNotFoundException() {
		GameBean gameBean = GameBeanMother.basic().build();
		Long gameId = gameBean.getId();
		when(gameDAO.findByIdAndGameStatusNotIn(gameId, FINISHED_STATUS)).thenReturn(Optional.empty());

		assertThatExceptionOfType(GameNotFoundException.class)
				.isThrownBy(() -> gameService.pause(gameId))
				.withMessage("Game with id=%s not found", gameId);
		verify(gameDAO, times(1)).findByIdAndGameStatusNotIn(gameId, FINISHED_STATUS);
		verifyNoMoreInteractions(gameDAO);
	}

	@Test
	public void testResume() {
		GameBean gameBean = GameBeanMother.basic().build();
		Long gameId = gameBean.getId();
		when(gameDAO.findByIdAndGameStatusNotIn(gameId, FINISHED_STATUS)).thenReturn(Optional.of(gameBean));

		assertThatCode(() -> gameService.resume(gameId)).doesNotThrowAnyException();
		verify(gameDAO, times(1)).findByIdAndGameStatusNotIn(gameId, FINISHED_STATUS);
		verify(gameDAO, times(1)).updateGameStatusById(GameStatus.PLAYING, gameId);
		verifyNoMoreInteractions(gameDAO);
	}

	@Test
	public void testResume_whenGameIsFinishedOrNotFound_throwsGameNotFoundException() {
		GameBean gameBean = GameBeanMother.basic().build();
		Long gameId = gameBean.getId();
		when(gameDAO.findByIdAndGameStatusNotIn(gameId, FINISHED_STATUS)).thenReturn(Optional.empty());

		assertThatExceptionOfType(GameNotFoundException.class)
				.isThrownBy(() -> gameService.resume(gameId))
				.withMessage("Game with id=%s not found", gameId);
		verify(gameDAO, times(1)).findByIdAndGameStatusNotIn(gameId, FINISHED_STATUS);
		verifyNoMoreInteractions(gameDAO);
	}

	@Test
	public void testGameIsFinished_returnTrueWhenAllMinesAreFlaggedAndAllNumbersAreRevealed() {
		GameCellBean number = GameCellBeanMother.number().cellOperation(CellOperation.REVEALED).build();
		GameCellBean mine = GameCellBeanMother.mine().cellOperation(CellOperation.FLAGGED).build();
		GameBean gameBean = GameBeanMother.basic()
				.gameCells(Sets.newHashSet(number, mine))
				.build();
		Long gameId = gameBean.getId();
		when(gameDAO.findById(gameId)).thenReturn(Optional.of(gameBean));

		assertThat(gameService.gameIsFinished(gameId)).isTrue();
		verify(gameDAO, times(1)).findById(gameId);
		verifyNoMoreInteractions(gameDAO);
	}

	@Test
	public void testGameIsFinished_returnFalseWhenAllMinesAreFlaggedButOneNumberIsNotRevealed() {
		GameCellBean number = GameCellBeanMother.number().cellOperation(CellOperation.NONE).build();
		GameCellBean mine = GameCellBeanMother.mine().cellOperation(CellOperation.FLAGGED).build();
		GameBean gameBean = GameBeanMother.basic()
				.gameCells(Sets.newHashSet(number, mine))
				.build();
		Long gameId = gameBean.getId();
		when(gameDAO.findById(gameId)).thenReturn(Optional.of(gameBean));

		assertThat(gameService.gameIsFinished(gameId)).isFalse();
		verify(gameDAO, times(1)).findById(gameId);
		verifyNoMoreInteractions(gameDAO);
	}

	@Test
	public void testGameIsFinished_returnFalseWhenAllNumbersAreRevealedButOneMineIsNotFlagged() {
		GameCellBean number = GameCellBeanMother.number().cellOperation(CellOperation.REVEALED).build();
		GameCellBean mine = GameCellBeanMother.mine().cellOperation(CellOperation.NONE).build();
		GameBean gameBean = GameBeanMother.basic()
				.gameCells(Sets.newHashSet(number, mine))
				.build();
		Long gameId = gameBean.getId();
		when(gameDAO.findById(gameId)).thenReturn(Optional.of(gameBean));

		assertThat(gameService.gameIsFinished(gameId)).isFalse();
		verify(gameDAO, times(1)).findById(gameId);
		verifyNoMoreInteractions(gameDAO);
	}

	@Test
	public void testGameIsFinished_whenNoGameIsFound_throwsGameNotFoundException() {
		Long gameId = ID;
		when(gameDAO.findById(gameId)).thenReturn(Optional.empty());

		assertThatExceptionOfType(GameNotFoundException.class)
				.isThrownBy(() -> gameService.gameIsFinished(gameId))
				.withMessage("Game with id=%s not found", gameId);
		verify(gameDAO, times(1)).findById(gameId);
		verifyNoMoreInteractions(gameDAO);
	}

	@Test
	public void testIsMineAndNotFlagged_returnTrueWithOperationNone() {
		GameCellBean gameCellBean = GameCellBeanMother.mine().cellOperation(CellOperation.NONE).build();

		assertThat(gameService.isMineAndNotFlagged(gameCellBean)).isTrue();
	}

	@Test
	public void testIsMineAndNotFlagged_returnTrueWithOperationQuestionMarked() {
		GameCellBean gameCellBean = GameCellBeanMother.mine().cellOperation(CellOperation.QUESTION_MARKED).build();

		assertThat(gameService.isMineAndNotFlagged(gameCellBean)).isTrue();
	}

	@Test
	public void testIsMineAndNotFlagged_returnFalseWithOperationFlagged() {
		GameCellBean gameCellBean = GameCellBeanMother.mine().cellOperation(CellOperation.FLAGGED).build();

		assertThat(gameService.isMineAndNotFlagged(gameCellBean)).isFalse();
	}

	@Test
	public void testIsMineAndNotFlagged_returnFalseWithNumber() {
		GameCellBean gameCellBean = GameCellBeanMother.number().build();

		assertThat(gameService.isMineAndNotFlagged(gameCellBean)).isFalse();
	}

	@Test
	public void testIsNumberAndNotRevealed_returnTrueWithOperationNone() {
		GameCellBean gameCellBean = GameCellBeanMother.number().cellOperation(CellOperation.NONE).build();

		assertThat(gameService.isNumberAndNotRevealed(gameCellBean)).isTrue();
	}

	@Test
	public void testIsNumberAndNotRevealed_returnTrueWithOperationQuestionMarked() {
		GameCellBean gameCellBean = GameCellBeanMother.number().cellOperation(CellOperation.QUESTION_MARKED).build();

		assertThat(gameService.isNumberAndNotRevealed(gameCellBean)).isTrue();
	}

	@Test
	public void testIsNumberAndNotRevealed_returnTrueWithOperationFlagged() {
		GameCellBean gameCellBean = GameCellBeanMother.number().cellOperation(CellOperation.FLAGGED).build();

		assertThat(gameService.isNumberAndNotRevealed(gameCellBean)).isTrue();
	}

	@Test
	public void testIsNumberAndNotRevealed_returnFalseWithOperationRevealed() {
		GameCellBean gameCellBean = GameCellBeanMother.number().cellOperation(CellOperation.REVEALED).build();

		assertThat(gameService.isNumberAndNotRevealed(gameCellBean)).isFalse();
	}

	@Test
	public void testIsNumberAndNotRevealed_returnFalseWithMine() {
		GameCellBean gameCellBean = GameCellBeanMother.mine().build();

		assertThat(gameService.isNumberAndNotRevealed(gameCellBean)).isFalse();
	}
}

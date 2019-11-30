package com.minesweeper.service.impl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;
import com.minesweeper.bean.GameBean;
import com.minesweeper.bean.GameCellBean;
import com.minesweeper.bean.GameCellOperationResponse;
import com.minesweeper.component.GameCellHelper;
import com.minesweeper.entity.Game;
import com.minesweeper.entity.GameCell;
import com.minesweeper.enums.CellOperation;
import com.minesweeper.exception.GameNotFoundException;
import com.minesweeper.exception.MineExplodedException;
import com.minesweeper.mapper.GameCellMapper;
import com.minesweeper.mapper.GameMapper;
import com.minesweeper.mother.GameBeanMother;
import com.minesweeper.mother.GameCellBeanMother;
import com.minesweeper.mother.GameCellMother;
import com.minesweeper.mother.GameCellOperationResponseMother;
import com.minesweeper.mother.GameMother;
import com.minesweeper.repository.GameCellRepository;
import com.minesweeper.repository.GameRepository;
import com.minesweeper.service.GameCellService;

public class GameServiceImplTest {
	@Mock
	private GameMapper gameMapper;
	@Mock
	private GameCellMapper gameCellMapper;
	@Mock
	private GameRepository gameRepository;
	@Mock
	private GameCellRepository gameCellRepository;
	@Mock
	private GameCellService gameCellService;
	@Mock
	private GameCellHelper gameCellHelper;

	private GameServiceImpl gameService;

	@BeforeMethod
	public void setup() {
		MockitoAnnotations.initMocks(this);
		gameService = new GameServiceImpl(gameMapper, gameCellMapper, gameRepository,gameCellRepository, gameCellService, gameCellHelper);
	}

	@Test
	public void testCreate() {
		GameBean requestGame = GameBeanMother.empty().build();
		Game requestGameMapped = GameMother.withoutId().gameCells(null).build();
		Set<GameCellBean> gameCellBeans = Sets.newHashSet(GameCellBeanMother.mine().build());
		Set<GameCell> gameCells = Sets.newHashSet(GameCellMother.mine().build());
		Game requestGameMappedWithCellsAndId = GameMother.basic().build();
		GameBean expectedResponse = GameBeanMother.basic().build();
		when(gameCellService.generateRandomMines(requestGame)).thenReturn(gameCellBeans);
		when(gameCellMapper.mapToEntity(gameCellBeans)).thenReturn(gameCells);
		when(gameMapper.mapToEntity(requestGame)).thenReturn(requestGameMapped);
		when(gameRepository.save(any(Game.class))).thenReturn(requestGameMappedWithCellsAndId);
		when(gameMapper.mapToBean(requestGameMappedWithCellsAndId)).thenReturn(expectedResponse);

		assertThat(gameService.create(requestGame)).isEqualTo(expectedResponse);
	}

	@Test
	public void testCreateInternal() {
		GameBean requestGame = GameBeanMother.empty().build();
		Game requestGameMapped = GameMother.withoutId().gameCells(null).build();
		Set<GameCellBean> gameCellBeans = Sets.newHashSet(GameCellBeanMother.mine().build());
		Set<GameCell> gameCells = Sets.newHashSet(GameCellMother.mine().build());
		Game requestGameMappedWithCellsAndId = GameMother.basic().build();
		GameBean expectedResponse = GameBeanMother.basic().build();
		when(gameCellMapper.mapToEntity(gameCellBeans)).thenReturn(gameCells);
		when(gameMapper.mapToEntity(requestGame)).thenReturn(requestGameMapped);
		when(gameRepository.save(any(Game.class))).thenReturn(requestGameMappedWithCellsAndId);
		when(gameMapper.mapToBean(requestGameMappedWithCellsAndId)).thenReturn(expectedResponse);

		assertThat(gameService.createInternal(requestGame, gameCellBeans)).isEqualTo(expectedResponse);
	}

	@Test
	public void testPerformOperation() {
		Game game = GameMother.basic().build();
		GameBean gameBean = GameBeanMother.basic().build();
		Long gameId = game.getId();
		CellOperation cellOperation = CellOperation.REVEALED;
		Long row = 1L;
		Long column = 1L;
		List<GameCellBean> cellBeans = Collections.singletonList(GameCellBeanMother.number().build());
		GameCellOperationResponse expectedResponse = GameCellOperationResponseMother.success().build();
		when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
		when(gameMapper.mapToBean(game)).thenReturn(gameBean);
		when(gameCellService.performOperation(gameBean, cellOperation, row, column)).thenReturn(cellBeans);

		assertThat(gameService.performOperation(gameId, cellOperation, row, column)).isEqualTo(expectedResponse);
	}

	@Test
	public void testPerformOperation_withInvalidGameId_throwsGameNotFoundException() {
		Game game = GameMother.basic().build();
		Long gameId = game.getId();
		CellOperation cellOperation = CellOperation.REVEALED;
		Long row = 1L;
		Long column = 1L;
		when(gameRepository.findById(gameId)).thenReturn(Optional.empty());

		assertThatExceptionOfType(GameNotFoundException.class)
				.isThrownBy(() -> gameService.performOperation(gameId, cellOperation, row, column))
				.withMessage("Game with id=%s not found", gameId);
		verifyZeroInteractions(gameMapper);
		verifyZeroInteractions(gameCellService);
	}

	@Test
	public void testPerformOperation_whenMineExplodedExceptionIsThrown_returnGameLostWithListOfMines() {
		Game game = GameMother.basic().build();
		GameCellBean number = GameCellBeanMother.number().build();
		GameCellBean mine = GameCellBeanMother.mine().build();
		GameBean gameBean = GameBeanMother.basic()
				.gameCells(Sets.newHashSet(number, mine))
				.build();
		Long gameId = game.getId();
		CellOperation cellOperation = CellOperation.REVEALED;
		Long row = 1L;
		Long column = 1L;
		GameCellOperationResponse expectedResponse = GameCellOperationResponseMother.gameLost()
				.gameCellBeans(Collections.singletonList(mine))
				.build();
		when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
		when(gameMapper.mapToBean(game)).thenReturn(gameBean);
		when(gameCellService.performOperation(gameBean, cellOperation, row, column)).thenThrow(new MineExplodedException(row, column, gameBean));
		when(gameCellHelper.isMine(number)).thenReturn(false);
		when(gameCellHelper.isMine(mine)).thenReturn(true);

		assertThat(gameService.performOperation(gameId, cellOperation, row, column)).isEqualTo(expectedResponse);
	}
}

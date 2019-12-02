package com.minesweeper.dao.impl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
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
import com.minesweeper.dao.GameDAO;
import com.minesweeper.entity.Game;
import com.minesweeper.entity.GameCell;
import com.minesweeper.enums.GameStatus;
import com.minesweeper.mapper.GameCellMapper;
import com.minesweeper.mapper.GameMapper;
import com.minesweeper.mother.GameBeanMother;
import com.minesweeper.mother.GameCellBeanMother;
import com.minesweeper.mother.GameCellMother;
import com.minesweeper.mother.GameMother;
import com.minesweeper.repository.GameCellRepository;
import com.minesweeper.repository.GameRepository;

public class GameDAOImplTest {
	@Mock
	private GameRepository gameRepository;
	@Mock
	private GameCellRepository gameCellRepository;
	@Mock
	private GameMapper gameMapper;
	@Mock
	private GameCellMapper gameCellMapper;

	private GameDAO gameDAO;

	@BeforeMethod
	public void setup() {
		MockitoAnnotations.initMocks(this);
		gameDAO = new GameDAOImpl(gameRepository, gameCellRepository, gameMapper, gameCellMapper);
	}

	@Test
	public void testCreate() {
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

		assertThat(gameDAO.create(requestGame, gameCellBeans)).isEqualTo(expectedResponse);
	}

	@Test
	public void testFindById() {
		Game game = GameMother.basic().build();
		Long gameId = game.getId();
		GameBean gameBean = GameBeanMother.basic().build();
		when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
		when(gameMapper.mapToBean(game)).thenReturn(gameBean);

		assertThat(gameDAO.findById(gameId)).contains(gameBean);
		verify(gameRepository, times(1)).findById(gameId);
		verify(gameMapper, times(1)).mapToBean(game);
		verifyNoMoreInteractions(gameRepository);
		verifyNoMoreInteractions(gameMapper);
	}

	@Test
	public void testFindByIdAndGameStatusIs() {
		GameStatus gameStatus = GameStatus.PLAYING;
		Game game = GameMother.basic().gameStatus(gameStatus).build();
		Long gameId = game.getId();
		GameBean gameBean = GameBeanMother.basic().build();
		when(gameRepository.findByIdAndGameStatusIs(gameId, gameStatus)).thenReturn(Optional.of(game));
		when(gameMapper.mapToBean(game)).thenReturn(gameBean);

		assertThat(gameDAO.findByIdAndGameStatusIs(gameId, gameStatus)).contains(gameBean);
		verify(gameRepository, times(1)).findByIdAndGameStatusIs(gameId, gameStatus);
		verify(gameMapper, times(1)).mapToBean(game);
		verifyNoMoreInteractions(gameRepository);
		verifyNoMoreInteractions(gameMapper);
	}

	@Test
	public void testFindByIdAndGameStatusIs_whenNoGameFound_returnsEmpty() {
		GameStatus gameStatus = GameStatus.PLAYING;
		Game game = GameMother.basic().gameStatus(gameStatus).build();
		Long gameId = game.getId();
		when(gameRepository.findByIdAndGameStatusIs(gameId, gameStatus)).thenReturn(Optional.empty());

		assertThat(gameDAO.findByIdAndGameStatusIs(gameId, gameStatus)).isEmpty();
		verify(gameRepository, times(1)).findByIdAndGameStatusIs(gameId, gameStatus);
		verifyNoMoreInteractions(gameRepository);
		verifyZeroInteractions(gameMapper);
	}

	@Test
	public void testUpdateGameStatusById() {
		GameStatus gameStatus = GameStatus.PLAYING;
		Game game = GameMother.basic().gameStatus(gameStatus).build();
		Long gameId = game.getId();

		assertThatCode(() -> gameDAO.updateGameStatusById(gameStatus, gameId)).doesNotThrowAnyException();
		verify(gameRepository, times(1)).updateGameStatusById(gameStatus, gameId);
		verifyNoMoreInteractions(gameRepository);
	}
}

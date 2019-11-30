package com.minesweeper.service.impl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;
import com.minesweeper.bean.GameBean;
import com.minesweeper.bean.GameCellBean;
import com.minesweeper.entity.Game;
import com.minesweeper.entity.GameCell;
import com.minesweeper.mapper.GameCellMapper;
import com.minesweeper.mapper.GameMapper;
import com.minesweeper.mother.GameBeanMother;
import com.minesweeper.mother.GameCellBeanMother;
import com.minesweeper.mother.GameCellMother;
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

	private GameServiceImpl gameService;

	@BeforeMethod
	public void setup() {
		MockitoAnnotations.initMocks(this);
		gameService = new GameServiceImpl(gameMapper, gameCellMapper, gameRepository,gameCellRepository, gameCellService);
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
}

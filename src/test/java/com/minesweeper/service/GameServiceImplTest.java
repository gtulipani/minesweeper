package com.minesweeper.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.minesweeper.bean.GameBean;
import com.minesweeper.entity.Game;
import com.minesweeper.mapper.GameMapper;
import com.minesweeper.mother.GameBeanMother;
import com.minesweeper.mother.GameMother;
import com.minesweeper.repository.GameRepository;

public class GameServiceImplTest {
	@Mock
	private GameMapper gameMapper;
	@Mock
	private GameRepository gameRepository;

	private GameService gameService;

	@BeforeMethod
	public void setup() {
		MockitoAnnotations.initMocks(this);
		gameService = new GameServiceImpl(gameMapper, gameRepository);
	}

	@Test
	public void testCreate() {
		GameBean requestGame = GameBeanMother.empty().build();
		Game requestGameMapped = GameMother.empty().build();
		Game newObject = GameMother.basic().build();
		GameBean expectedResponse = GameBeanMother.basic().build();
		when(gameMapper.mapToEntity(requestGame)).thenReturn(requestGameMapped);
		when(gameRepository.save(requestGameMapped)).thenReturn(newObject);
		when(gameMapper.mapToBean(newObject)).thenReturn(expectedResponse);

		assertThat(gameService.create(requestGame)).isEqualTo(expectedResponse);
	}
}

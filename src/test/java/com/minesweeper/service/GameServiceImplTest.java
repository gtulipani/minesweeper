package com.minesweeper.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.minesweeper.bean.GameBean;
import com.minesweeper.entity.Game;
import com.minesweeper.entity.GameConfiguration;
import com.minesweeper.mapper.GameConfigurationMapper;
import com.minesweeper.mapper.GameMapper;
import com.minesweeper.mother.GameBeanMother;
import com.minesweeper.mother.GameConfigurationMother;
import com.minesweeper.mother.GameMother;
import com.minesweeper.repository.GameConfigurationRepository;
import com.minesweeper.repository.GameRepository;

public class GameServiceImplTest {
	@Mock
	private GameMapper gameMapper;
	@Mock
	private GameConfigurationMapper gameConfigurationMapper;
	@Mock
	private GameRepository gameRepository;
	@Mock
	GameConfigurationRepository gameConfigurationRepository;

	private GameService gameService;

	@BeforeMethod
	public void setup() {
		MockitoAnnotations.initMocks(this);
		gameService = new GameServiceImpl(gameMapper, gameConfigurationMapper, gameRepository, gameConfigurationRepository);
	}

	@Test
	public void testCreate() {
		GameBean requestGame = GameBeanMother.empty().build();
		GameConfiguration gameConfigurationWithoutId = GameConfigurationMother.basic()
				.id(null)
				.build();
		GameConfiguration gameConfigurationWithId = GameConfigurationMother.basic().build();

		Game requestGameMapped = GameMother.withoutId().build();
		Game requestGameMappedWithId = GameMother.basic().build();
		GameBean expectedResponse = GameBeanMother.basic().build();
		when(gameConfigurationMapper.mapToEntity(requestGame.getGameConfiguration())).thenReturn(gameConfigurationWithoutId);
		when(gameConfigurationRepository.save(gameConfigurationWithoutId)).thenReturn(gameConfigurationWithId);
		when(gameMapper.mapToEntity(requestGame)).thenReturn(requestGameMapped);
		when(gameRepository.save(requestGameMapped)).thenReturn(requestGameMappedWithId);
		when(gameMapper.mapToBean(requestGameMappedWithId)).thenReturn(expectedResponse);

		assertThat(gameService.create(requestGame)).isEqualTo(expectedResponse);
	}
}

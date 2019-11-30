package com.minesweeper.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.minesweeper.bean.GameBean;
import com.minesweeper.entity.Game;
import com.minesweeper.entity.GameConfiguration;
import com.minesweeper.mother.GameBeanMother;
import com.minesweeper.mother.GameConfigurationBeanMother;
import com.minesweeper.mother.GameConfigurationMother;
import com.minesweeper.mother.GameMother;

public class GameMapperImplTest {
	@Mock
	private GameConfigurationMapper gameConfigurationMapper;

	private GameMapperImpl gameMapper;

	@BeforeMethod
	public void setup() {
		MockitoAnnotations.initMocks(this);
		gameMapper = new GameMapperImpl(gameConfigurationMapper);
		gameMapper.init();
	}

	@Test
	public void testMapToBean() {
		Game game = GameMother.basic().build();
		when(gameConfigurationMapper.mapToBean(game.getGameConfiguration())).thenReturn(GameConfigurationBeanMother.basic().build());

		assertThat(gameMapper.mapToBean(game)).isEqualTo(GameBeanMother.basic().build());
	}

	@Test
	public void testMapToEntity() {
		GameBean gameBean = GameBeanMother.basic().build();
		GameConfiguration mappedGameConfiguration = GameConfigurationMother.basic().build();
		when(gameConfigurationMapper.mapToEntity(gameBean.getGameConfiguration())).thenReturn(mappedGameConfiguration);

		Game result = gameMapper.mapToEntity(gameBean);

		assertThat(result)
				.isEqualToIgnoringGivenFields(GameMother.basic().build(), "createdOn", "lastModified", "gameConfiguration");
		assertThat(result.getGameConfiguration())
				.isEqualToIgnoringGivenFields(mappedGameConfiguration, "createdOn", "lastModified");
	}
}

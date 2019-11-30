package com.minesweeper.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.minesweeper.mother.GameConfigurationBeanMother;
import com.minesweeper.mother.GameConfigurationMother;

public class GameConfigurationMapperImplTest {
	private GameConfigurationMapperImpl gameConfigurationMapper;

	@BeforeMethod
	public void setup() {
		MockitoAnnotations.initMocks(this);
		gameConfigurationMapper = new GameConfigurationMapperImpl();
		gameConfigurationMapper.init();
	}

	@Test
	public void testMapToBean() {
		assertThat(gameConfigurationMapper.mapToBean(GameConfigurationMother.basic().build())).isEqualTo(GameConfigurationBeanMother.basic().build());
	}

	@Test
	public void testMapToEntity() {
		assertThat(gameConfigurationMapper.mapToEntity(GameConfigurationBeanMother.basic().build()))
				.isEqualToIgnoringGivenFields(GameConfigurationMother.basic().build(), "createdOn", "lastModified");
	}
}

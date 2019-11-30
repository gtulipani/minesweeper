package com.minesweeper.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.minesweeper.mother.GameBeanMother;
import com.minesweeper.mother.GameMother;

public class GameMapperImplTest {
	private GameMapperImpl gameMapper;

	@BeforeMethod
	public void setup() {
		gameMapper = new GameMapperImpl();
		gameMapper.init();
	}

	@Test
	public void testMapToBean() {
		assertThat(gameMapper.mapToBean(GameMother.basic().build())).isEqualTo(GameBeanMother.basic().build());
	}

	@Test
	public void testMapToEntity() {
		assertThat(gameMapper.mapToEntity(GameBeanMother.basic().build()))
				.isEqualToIgnoringGivenFields(GameMother.basic().build(), "createdOn", "lastModified");
	}
}

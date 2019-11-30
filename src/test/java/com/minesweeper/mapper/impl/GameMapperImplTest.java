package com.minesweeper.mapper.impl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.minesweeper.bean.GameBean;
import com.minesweeper.entity.Game;
import com.minesweeper.entity.GameCell;
import com.minesweeper.mother.GameBeanMother;
import com.minesweeper.mother.GameCellMother;
import com.minesweeper.mother.GameMother;

public class GameMapperImplTest {
	private GameMapperImpl gameMapper;

	@BeforeMethod
	public void setup() {
		MockitoAnnotations.initMocks(this);
		gameMapper = new GameMapperImpl();
		gameMapper.init();
	}

	@Test
	public void testMapToBean() {
		Game game = GameMother.basic().build();

		assertThat(gameMapper.mapToBean(game)).isEqualTo(GameBeanMother.basic().build());
	}

	@Test
	public void testMapToEntity() {
		GameBean gameBean = GameBeanMother.basic().build();
		GameCell mappedGameCell = GameCellMother.mine().build();

		Game result = gameMapper.mapToEntity(gameBean);

		assertThat(result)
				.isEqualToIgnoringGivenFields(GameMother.basic().build(), 
						"createdOn",
						"lastModified",
						"gameCells");
		result.getGameCells().forEach(gameCell -> assertThat(gameCell)
				.isEqualToIgnoringGivenFields(mappedGameCell, "createdOn", "lastModified"));
	}
}

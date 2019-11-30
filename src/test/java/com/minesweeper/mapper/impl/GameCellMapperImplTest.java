package com.minesweeper.mapper.impl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Collections;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;
import com.minesweeper.bean.GameCellBean;
import com.minesweeper.entity.GameCell;
import com.minesweeper.mother.GameCellBeanMother;
import com.minesweeper.mother.GameCellMother;

public class GameCellMapperImplTest {
	private GameCellMapperImpl gameCellMapper;

	@BeforeMethod
	public void setup() {
		gameCellMapper = new GameCellMapperImpl();
		gameCellMapper.init();
	}

	@Test
	public void testMapToBean() {
		Set<GameCell> gameCells = Sets.newHashSet(GameCellMother.mine().build());
		GameCellBean gameCellBean = GameCellBeanMother.mine().build();

		Set<GameCellBean> result = gameCellMapper.mapToBean(gameCells);
		
		assertThat(result.size()).isEqualTo(1);
		result.stream().forEach(cellBean -> assertThat(cellBean).isEqualToIgnoringGivenFields(gameCellBean, "createdOn", "lastModified"));
	}

	@Test
	public void testMapToBean_setNull_returnsEmptySet() {
		Set<GameCell> gameCells = null;
		assertThat(gameCellMapper.mapToBean(gameCells)).isEqualTo(Collections.emptySet());
	}

	@Test
	public void testMapToEntity() {
		Set<GameCellBean> gameCellBeans = Sets.newHashSet(GameCellBeanMother.mine().build());
		GameCell gameCell = GameCellMother.mine().build();

		Set<GameCell> result = gameCellMapper.mapToEntity(gameCellBeans);

		assertThat(result.size()).isEqualTo(1);
		result.stream().forEach(cell -> assertThat(cell).isEqualToIgnoringGivenFields(gameCell, "createdOn", "lastModified"));
	}

	@Test
	public void testMapToEntity_setNull_returnEmptySet() {
		Set<GameCellBean> gameCellBeans = null;
		assertThat(gameCellMapper.mapToEntity(gameCellBeans)).isEqualTo(Collections.emptySet());
	}
}

package com.minesweeper.mapper.impl;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.minesweeper.bean.GameCellBean;
import com.minesweeper.entity.GameCell;
import com.minesweeper.mapper.GameCellMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

@Component
public class GameCellMapperImpl implements GameCellMapper {
	private MapperFacade facade;

	@PostConstruct
	public void init() {
		MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
		mapperFactory.classMap(GameCell.class, GameCellBean.class)
				.byDefault()
				.register();
		facade = mapperFactory.getMapperFacade();
	}

	@Override
	public Set<GameCellBean> mapToBean(Set<GameCell> gameCells) {
		return Optional.ofNullable(gameCells)
				.map(cells -> facade.mapAsSet(cells, GameCellBean.class))
				.orElse(Collections.emptySet());
	}

	@Override
	public Set<GameCell> mapToEntity(Set<GameCellBean> gameCellBeans) {
		return Optional.ofNullable(gameCellBeans)
				.map(cellBeans -> facade.mapAsSet(cellBeans, GameCell.class))
				.orElse(Collections.emptySet());
	}
}

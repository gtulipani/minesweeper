package com.minesweeper.mapper;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.minesweeper.bean.GameBean;
import com.minesweeper.entity.Game;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

@Component
public class GameMapperImpl implements GameMapper {
	private MapperFacade facade;

	@PostConstruct
	public void init() {
		MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
		mapperFactory.classMap(Game.class, GameBean.class)
				.byDefault()
				.register();
		facade = mapperFactory.getMapperFacade();
	}

	@Override
	public GameBean mapToBean(Game game) {
		return facade.map(game, GameBean.class);
	}

	@Override
	public Game mapToEntity(GameBean gameBean) {
		return facade.map(gameBean, Game.class);
	}
}

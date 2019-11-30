package com.minesweeper.mapper;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.minesweeper.bean.GameConfigurationBean;
import com.minesweeper.entity.GameConfiguration;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

@Component
public class GameConfigurationMapperImpl implements GameConfigurationMapper {
	private MapperFacade facade;

	@PostConstruct
	public void init() {
		MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
		mapperFactory.classMap(GameConfiguration.class, GameConfigurationBean.class)
				.byDefault()
				.register();
		facade = mapperFactory.getMapperFacade();
	}

	@Override
	public GameConfigurationBean mapToBean(GameConfiguration gameConfiguration) {
		return facade.map(gameConfiguration, GameConfigurationBean.class);
	}

	@Override
	public GameConfiguration mapToEntity(GameConfigurationBean gameConfigurationBean) {
		return facade.map(gameConfigurationBean, GameConfiguration.class);
	}
}

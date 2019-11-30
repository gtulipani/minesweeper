package com.minesweeper.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.minesweeper.bean.GameBean;

@AllArgsConstructor
@Service
@Slf4j
public class GameServiceImpl implements GameService {
	@Override
	public GameBean create(GameBean gameBean) {
		return null;
	}
}

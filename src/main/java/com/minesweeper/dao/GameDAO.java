package com.minesweeper.dao;

import java.util.Optional;
import java.util.Set;

import com.minesweeper.bean.GameBean;
import com.minesweeper.bean.GameCellBean;
import com.minesweeper.enums.GameStatus;

public interface GameDAO {
	GameBean create(GameBean gameBean, Set<GameCellBean> gameCellBeans);

	Optional<GameBean> findById(Long gameId);

	Optional<GameBean> findByIdAndGameStatusNotIn(Long gameId, Set<GameStatus> gameStatus);

	Optional<GameBean> findByIdAndGameStatusIs(Long gameId, GameStatus gameStatus);

	void updateGameStatusById(GameStatus gameStatus, Long gameId);
}

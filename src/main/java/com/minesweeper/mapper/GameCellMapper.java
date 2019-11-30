package com.minesweeper.mapper;

import java.util.Set;

import com.minesweeper.bean.GameCellBean;
import com.minesweeper.entity.GameCell;

public interface GameCellMapper {
	Set<GameCellBean> mapToBean(Set<GameCell> gameCell);

	Set<GameCell> mapToEntity(Set<GameCellBean> gameCellBeans);
}

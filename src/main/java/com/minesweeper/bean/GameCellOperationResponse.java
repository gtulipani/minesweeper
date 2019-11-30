package com.minesweeper.bean;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.minesweeper.enums.GameCellOperationStatus;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GameCellOperationResponse {
	private GameCellOperationStatus gameCellOperationStatus;
	private List<GameCellBean> gameCellBeans;
}

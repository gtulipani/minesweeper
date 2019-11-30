package com.minesweeper.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.minesweeper.enums.CellOperation;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GameCellOperation {
	private CellOperation cellOperation;
	private GameBean gameBean;
	private Long row;
	private Long column;
}

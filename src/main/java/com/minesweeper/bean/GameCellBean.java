package com.minesweeper.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.minesweeper.enums.CellContent;
import com.minesweeper.enums.CellOperation;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GameCellBean {
	private Long id;
	private Long row;
	private Long column;
	private CellContent cellContent;
	private Long minesAround;
	private CellOperation cellOperation;
}

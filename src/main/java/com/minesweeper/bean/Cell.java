package com.minesweeper.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.minesweeper.enums.CellContent;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Cell {
	private CellContent cellContent;
	private int minesAround; 
}

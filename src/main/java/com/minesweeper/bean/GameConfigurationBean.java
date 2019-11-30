package com.minesweeper.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GameConfigurationBean {
	private Long id;
	private int rows;
	private int columns;
	private int mines;
}

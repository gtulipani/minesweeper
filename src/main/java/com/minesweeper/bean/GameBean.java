package com.minesweeper.bean;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GameBean {
	private Long id;
	private Long rows;
	private Long columns;
	private Long mines;
	@JsonIgnore
	private Set<GameCellBean> gameCells;
}

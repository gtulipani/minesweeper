package com.minesweeper.bean;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.minesweeper.enums.GameCellOperationStatus;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameCellOperationResponse {
	private GameCellOperationStatus gameCellOperationStatus;
	private Set<GameCellBean> gameCellBeans;
}

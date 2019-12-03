package com.minesweeper.bean;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.minesweeper.enums.GameCellOperationStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "GameCellOperationResponse", description = "Response after performing an operation on a cell")
public class GameCellOperationResponse {
	@ApiModelProperty(required = true)
	private GameCellOperationStatus gameCellOperationStatus;
	@ApiModelProperty(required = true)
	private Set<GameCellBean> gameCellBeans;
}

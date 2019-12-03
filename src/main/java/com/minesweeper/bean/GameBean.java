package com.minesweeper.bean;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.minesweeper.enums.GameStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "GameBean", description = "Object containing all the important information about the Game")
public class GameBean {
	@ApiModelProperty(value = "ID from the game", example = "1")
	private Long id;
	@ApiModelProperty(value = "Quantity of rows from the game", example = "10", required = true)
	private Long rows;
	@ApiModelProperty(value = "Quantity of columns from the game", example = "10", required = true)
	private Long columns;
	@ApiModelProperty(value = "Quantity of mines from the game", example = "20", required = true)
	private Long mines;
	@ApiModelProperty
	private GameStatus gameStatus;
	@JsonIgnore
	private Set<GameCellBean> gameCells;
}

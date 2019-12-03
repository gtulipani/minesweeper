package com.minesweeper.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.minesweeper.enums.CellContent;
import com.minesweeper.enums.CellOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "GameCellBean", description = "Object containing all the important information about a Cell from the Game")
public class GameCellBean {
	@ApiModelProperty(value = "ID from the cell", example = "1", required = true)
	private Long id;
	@ApiModelProperty(value = "Row from the cell", example = "1", required = true)
	private Long row;
	@ApiModelProperty(value = "Column from the cell", example = "1", required = true)
	private Long column;
	@ApiModelProperty(value = "Content from the cell", required = true)
	private CellContent cellContent;
	@ApiModelProperty(value = "Mines around the cell", example = "1")
	private Long minesAround;
	@ApiModelProperty
	private CellOperation cellOperation;
}

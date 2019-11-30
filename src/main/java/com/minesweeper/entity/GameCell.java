package com.minesweeper.entity;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.GenericGenerator;

import com.minesweeper.enums.CellContent;
import com.minesweeper.enums.CellOperation;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "game_cells")
public class GameCell extends TimestampedEntity implements Serializable {
	private static final long serialVersionUID = -6507963547063710509L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long id;

	@Column(name = "row_number", nullable = false, length = 11)
	private Long row;

	@Column(name = "col_number", nullable = false, length = 11)
	private Long column;

	@Enumerated(EnumType.STRING)
	@Column(name = "cell_content", nullable = false, length = 10)
	private CellContent cellContent;

	@Column(length = 11)
	private Long minesAround;

	@Enumerated(EnumType.STRING)
	@Column(name = "cell_operation", nullable = false, length = 20)
	private CellOperation cellOperation;

	@Builder
	public GameCell(Long id,
					Long row,
					Long column,
					CellContent cellContent,
					Long minesAround,
					CellOperation cellOperation,
					Instant createdOn,
					Instant lastModified) {
		super(createdOn, lastModified);
		this.id = id;
		this.row = row;
		this.column = column;
		this.cellContent = cellContent;
		this.minesAround = minesAround;
		this.cellOperation = cellOperation;
	}
}

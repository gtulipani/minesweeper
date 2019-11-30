package com.minesweeper.entity;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.GenericGenerator;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "game_configurations")
public class GameConfiguration extends TimestampedEntity implements Serializable {
	private static final long serialVersionUID = -6507963547063710509L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long id;

	@Column(name = "rows_quantity", nullable = false)
	private Long rows;

	@Column(name = "columns_quantity", nullable = false)
	private Long columns;

	@Column(name = "mines_quantity", nullable = false)
	private Long mines;

	@OneToOne(mappedBy = "gameConfiguration")
	private Game game;

	@Builder
	public GameConfiguration(Long id, Long rows, Long columns, Long mines, Instant createdOn, Instant lastModified) {
		super(createdOn, lastModified);
		this.id = id;
		this.rows = rows;
		this.columns = columns;
		this.mines = mines;
	}
}

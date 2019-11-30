package com.minesweeper.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.hibernate.annotations.GenericGenerator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@ToString(exclude = "gameCells")
@Table(name = "games")
public class Game extends TimestampedEntity implements Serializable {
	private static final long serialVersionUID = -6507963547063710509L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long id;

	@Column(name = "rows_quantity", nullable = false, length = 11)
	private Long rows;

	@Column(name = "columns_quantity", nullable = false, length = 11)
	private Long columns;

	@Column(name = "mines_quantity", nullable = false, length = 11)
	private Long mines;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "game_id")
	private Set<GameCell> gameCells;

	@Builder
	public Game(Long id, Long rows, Long columns, Long mines, Set<GameCell> gameCells, Instant createdOn, Instant lastModified) {
		super(createdOn, lastModified);
		this.id = id;
		this.rows = rows;
		this.columns = columns;
		this.mines = mines;
		this.gameCells = gameCells;
	}

	public Set<GameCell> getGameCells() {
		if (gameCells == null) {
			gameCells = Sets.newHashSet();
		}
		return ImmutableSet.copyOf(gameCells);
	}
}

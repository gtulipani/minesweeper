package com.minesweeper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.minesweeper.entity.GameCell;
import com.minesweeper.enums.CellOperation;

@Repository
public interface GameCellRepository extends JpaRepository<GameCell, Long>  {
	@Modifying
	@Query("UPDATE GameCell g SET g.cellOperation= :cellOperation WHERE g.id = :id")
	void updateCellOperationById(@Param("cellOperation") CellOperation cellOperation, @Param("id") Long id);

	@Modifying
	@Query("UPDATE GameCell g SET g.cellOperation = :cellOperation, g.minesAround = :minesAround WHERE g.id = :id")
	void updateCellOperationAndMinesAroundById(@Param("cellOperation") CellOperation cellOperation,
											   @Param("minesAround") Long minesAround,
											   @Param("id") Long id);
}

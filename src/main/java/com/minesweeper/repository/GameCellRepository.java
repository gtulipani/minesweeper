package com.minesweeper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.minesweeper.entity.GameCell;

@Repository
public interface GameCellRepository extends JpaRepository<GameCell, Long>  {
}

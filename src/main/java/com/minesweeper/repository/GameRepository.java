package com.minesweeper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.minesweeper.entity.Game;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
}

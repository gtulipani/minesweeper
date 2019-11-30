package com.minesweeper.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.minesweeper.entity.Game;
import com.minesweeper.enums.GameStatus;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
	Optional<Game> findByIdAndGameStatusNotIn(Long id, Set<GameStatus> gameStatusSet);

	Optional<Game> findByIdAndGameStatusIs(Long id, GameStatus gameStatus);

	@Modifying
	@Query("UPDATE Game g SET g.gameStatus = :gameStatus WHERE g.id = :id")
	void updateGameStatusById(@Param("gameStatus") GameStatus gameStatus, @Param("id") Long id);
}

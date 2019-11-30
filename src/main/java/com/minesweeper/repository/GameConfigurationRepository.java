package com.minesweeper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.minesweeper.entity.GameConfiguration;

@Repository
public interface GameConfigurationRepository extends JpaRepository<GameConfiguration, Long> {
}

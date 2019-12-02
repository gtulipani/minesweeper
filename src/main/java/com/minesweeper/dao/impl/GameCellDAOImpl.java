package com.minesweeper.dao.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.minesweeper.dao.GameCellDAO;
import com.minesweeper.enums.CellOperation;
import com.minesweeper.repository.GameCellRepository;

@Slf4j
@AllArgsConstructor
@Component
public class GameCellDAOImpl implements GameCellDAO {
	private final GameCellRepository gameCellRepository;

	@Override
	@Transactional
	public void updateCellOperationById(CellOperation cellOperation, Long id) {
		gameCellRepository.updateCellOperationById(cellOperation, id);
	}

	@Override
	@Transactional
	public void updateCellOperationAndMinesAroundById(CellOperation cellOperation, Long minesAround, Long id) {
		gameCellRepository.updateCellOperationAndMinesAroundById(cellOperation, minesAround, id);
	}
}

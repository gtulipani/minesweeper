package com.minesweeper.dao.impl;

import static com.minesweeper.utils.TestConstants.ID;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.minesweeper.dao.GameCellDAO;
import com.minesweeper.enums.CellOperation;
import com.minesweeper.repository.GameCellRepository;

public class GameCellDAOImplTest {
	@Mock
	private GameCellRepository gameCellRepository;

	private GameCellDAO gameCellDAO;

	@BeforeMethod
	public void setup() {
		MockitoAnnotations.initMocks(this);
		gameCellDAO = new GameCellDAOImpl(gameCellRepository);
	}

	@Test
	public void testUpdateCellOperationById() {
		CellOperation cellOperation = CellOperation.REVEALED;
		Long id = ID;

		gameCellDAO.updateCellOperationById(cellOperation, id);

		verify(gameCellRepository, times(1)).updateCellOperationById(cellOperation, id);
		verifyNoMoreInteractions(gameCellRepository);
	}

	@Test
	public void testUpdateCellOperationAndMinesAroundById() {
		CellOperation cellOperation = CellOperation.REVEALED;
		Long minesAround = 5L;
		Long id = ID;

		gameCellDAO.updateCellOperationAndMinesAroundById(cellOperation, minesAround, id);

		verify(gameCellRepository, times(1)).updateCellOperationAndMinesAroundById(cellOperation, minesAround, id);
		verifyNoMoreInteractions(gameCellRepository);
	}
}

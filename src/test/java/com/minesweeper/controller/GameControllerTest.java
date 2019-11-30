package com.minesweeper.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.minesweeper.bean.GameBean;
import com.minesweeper.bean.GameCellOperationResponse;
import com.minesweeper.enums.CellOperation;
import com.minesweeper.mother.GameBeanMother;
import com.minesweeper.mother.GameCellOperationResponseMother;
import com.minesweeper.service.GameService;

public class GameControllerTest {
	@Mock
	private GameService gameService;

	private GameController gameController;

	@BeforeMethod
	public void setup() {
		MockitoAnnotations.initMocks(this);
		gameController = new GameController(gameService);
	}

	@Test
	public void testCreate() {
		GameBean request = GameBeanMother.empty().build();
		GameBean expectedResponse = GameBeanMother.basic().build();
		when(gameService.create(request)).thenReturn(expectedResponse);

		ResponseEntity<GameBean> response = gameController.create(request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isEqualTo(expectedResponse);
		verify(gameService, times(1)).create(request);
		verifyNoMoreInteractions(gameService);
	}

	@Test
	public void testPerformOperation() {
		GameBean gameBean = GameBeanMother.basic().build();
		Long gameId = gameBean.getId();
		CellOperation cellOperation = CellOperation.REVEALED;
		Long row = 1L;
		Long column = 1L;
		GameCellOperationResponse expectedResponse = GameCellOperationResponseMother.success().build();
		when(gameService.performOperation(gameId, cellOperation, row, column)).thenReturn(expectedResponse);

		ResponseEntity<GameCellOperationResponse> response = gameController.performOperation(gameId, cellOperation, row, column);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo(expectedResponse);
		verify(gameService, times(1)).performOperation(gameId, cellOperation, row, column);
		verifyNoMoreInteractions(gameService);
	}

	@Test
	public void testPause() {
		Long gameId = GameBeanMother.basic().build().getId();
		assertThat(gameController.pause(gameId).getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void testResume() {
		Long gameId = GameBeanMother.basic().build().getId();
		assertThat(gameController.resume(gameId).getStatusCode()).isEqualTo(HttpStatus.OK);
	}
}

package com.minesweeper.service.impl;

import static com.minesweeper.utils.TestConstants.COLUMNS_QUANTITY;
import static com.minesweeper.utils.TestConstants.ID;
import static com.minesweeper.utils.TestConstants.MINES_QUANTITY;
import static com.minesweeper.utils.TestConstants.MINE_COLUMNS;
import static com.minesweeper.utils.TestConstants.MINE_ROW;
import static com.minesweeper.utils.TestConstants.NUMBER_COLUMN;
import static com.minesweeper.utils.TestConstants.NUMBER_ROW;
import static com.minesweeper.utils.TestConstants.ROWS_QUANTITY;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;
import com.minesweeper.bean.GameBean;
import com.minesweeper.bean.GameCellBean;
import com.minesweeper.bean.GameCellOperation;
import com.minesweeper.component.GameCellHelper;
import com.minesweeper.enums.CellContent;
import com.minesweeper.enums.CellOperation;
import com.minesweeper.exception.InvalidPositionException;
import com.minesweeper.exception.MineExplodedException;
import com.minesweeper.mother.GameBeanMother;
import com.minesweeper.mother.GameCellBeanMother;
import com.minesweeper.mother.GameCellOperationMother;
import com.minesweeper.repository.GameCellRepository;

public class GameCellServiceImplTest {
	@Mock
	private GameCellRepository gameCellRepository;
	@Mock
	private GameCellHelper gameCellHelper;

	private GameCellServiceImpl gameCellService;

	@BeforeMethod
	public void setup() {
		MockitoAnnotations.initMocks(this);
		gameCellService = new GameCellServiceImpl(gameCellRepository, gameCellHelper);
	}

	@Test
	public void testGenerateRandomMines() {
		GameBean gameBean = GameBeanMother.basic().build();
		int totalCells = gameBean.getRows().intValue() * gameBean.getColumns().intValue();

		Set<GameCellBean> gameCellBeans = gameCellService.populateCells(gameBean);

		assertThat(gameCellBeans.stream()
				.filter(cell -> CellContent.MINE.equals(cell.getCellContent()))
				.count()).isEqualTo(gameBean.getMines().intValue());
		assertThat(gameCellBeans.stream()
				.filter(cell -> CellContent.NUMBER.equals(cell.getCellContent()))
				.count()).isEqualTo(totalCells - gameBean.getMines().intValue());
		assertThat(gameCellBeans.stream()
				.map(cell -> Pair.of(cell.getRow(), cell.getColumn()))
				.collect(Collectors.toSet())
				.size()).isEqualTo(totalCells);
	}

	@Test
	public void testPopulateWithMines() {
		Set<GameCellBean> gameCellBeans = gameCellService.populateWithMines(ROWS_QUANTITY, COLUMNS_QUANTITY, MINES_QUANTITY);

		assertThat(gameCellBeans.size()).isEqualTo(MINES_QUANTITY);
		Set<Pair<Long, Long>> rowColumnPairs = Sets.newHashSet();
		gameCellBeans.stream().forEach(gameCellBean -> {
			assertThat(gameCellBean.getCellContent()).isEqualTo(CellContent.MINE);
			rowColumnPairs.add(Pair.of(gameCellBean.getRow(), gameCellBean.getColumn()));
		});
		assertThat(rowColumnPairs.size()).isEqualTo(MINES_QUANTITY);
	}

	@Test
	public void testGetNewRandomPairInRange_createsPairAndFinishes() {
		Supplier<Long> rowSupplier = () -> ROWS_QUANTITY;
		Supplier<Long> columnSupplier = () -> COLUMNS_QUANTITY;
		BiPredicate<Long, Long> rowColumnVerifier = (row, column) -> true;
		Pair<Long, Long> expectedPair = Pair.of(ROWS_QUANTITY, COLUMNS_QUANTITY);

		assertThat(gameCellService.getNewRandomPairInRange(rowSupplier, columnSupplier, rowColumnVerifier)).isEqualTo(expectedPair);
	}

	@Test
	public void testGetNewRandomPairInRange_ifCreatesAlreadyExistingPair_createsAnotherOne() {
		Long firstRow = 1L;
		Long firstColumn = 1L;
		Long secondRow = 2L;
		Long secondColumn = 2L;
		Supplier<Long> rowSupplier = Mockito.mock(Supplier.class);
		Supplier<Long> columnSupplier = Mockito.mock(Supplier.class);
		BiPredicate<Long, Long> rowColumnVerifier = Mockito.mock(BiPredicate.class);
		Pair<Long, Long> expectedPair = Pair.of(secondRow, secondColumn);
		when(rowSupplier.get()).thenReturn(firstRow).thenReturn(secondRow);
		when(columnSupplier.get()).thenReturn(firstColumn).thenReturn(secondColumn);
		when(rowColumnVerifier.test(firstRow, firstColumn)).thenReturn(false);
		when(rowColumnVerifier.test(secondRow, secondColumn)).thenReturn(true);

		assertThat(gameCellService.getNewRandomPairInRange(rowSupplier, columnSupplier, rowColumnVerifier)).isEqualTo(expectedPair);
		verify(rowSupplier, times(2)).get();
		verify(columnSupplier, times(2)).get();
		verify(rowColumnVerifier, times(1)).test(firstRow, firstColumn);
		verify(rowColumnVerifier, times(1)).test(secondRow, secondColumn);
		verifyNoMoreInteractions(rowSupplier);
		verifyNoMoreInteractions(columnSupplier);
		verifyNoMoreInteractions(rowColumnVerifier);
	}

	@Test
	public void testMapToGameCellBean() {
		Pair<Long, Long> pair = Pair.of(MINE_ROW, MINE_COLUMNS);
		GameCellBean gameCellBean = GameCellBeanMother.mine().build();

		assertThat(gameCellService.mapToGameCellBean(pair)).isEqualTo(gameCellBean);
	}

	@Test
	public void testPopulateWithNumbers() {
		Set<GameCellBean> mines = Sets.newHashSet(GameCellBeanMother.mine().build());
		Long rows = ROWS_QUANTITY;
		Long columns = COLUMNS_QUANTITY;
		Long totalCells = rows * columns;
		Long minesQuantity = (long) mines.size();

		Set<GameCellBean> result = gameCellService.populateWithNumbers(mines, rows, columns);

		assertThat(result.stream()
				.filter(cell -> CellContent.MINE.equals(cell.getCellContent()))
				.count()).isEqualTo(minesQuantity);
		assertThat(result.stream()
				.filter(cell -> CellContent.NUMBER.equals(cell.getCellContent()))
				.count()).isEqualTo(totalCells - minesQuantity);
		assertThat(result.stream()
				.map(cell -> Pair.of(cell.getRow(), cell.getColumn()))
				.collect(Collectors.toSet())
				.size()).isEqualTo(totalCells.intValue());
	}

	@Test
	public void testPerformOperationREVEALED_onNumber_doesntThrowException() {
		GameCellBean number = GameCellBeanMother.number().build();
		GameCellBean mine = GameCellBeanMother.mine().build();
		GameBean gameBean = GameBeanMother.basic()
				.gameCells(Sets.newHashSet(number, mine))
				.build();
		Long row = number.getRow();
		Long column = number.getColumn();
		GameCellOperation gameCellOperation = GameCellOperationMother.revealed()
				.gameBean(gameBean)
				.row(row)
				.column(column)
				.build();
		when(gameCellHelper.isMine(number)).thenReturn(false);
		when(gameCellHelper.isMine(mine)).thenReturn(true);
		when(gameCellHelper.hasPosition(number, number.getRow(), number.getColumn())).thenReturn(true);
		when(gameCellHelper.hasPosition(mine, mine.getRow(), mine.getColumn())).thenReturn(true);

		assertThatCode(() -> gameCellService.performOperation(gameCellOperation))
				.doesNotThrowAnyException();
	}

	@Test
	public void testPerformOperationREVEALED_onMine_throwMineExplodedExceptionAndUpdatesCell() {
		GameCellBean number = GameCellBeanMother.number().build();
		GameCellBean mine = GameCellBeanMother.mine().build();
		GameBean gameBean = GameBeanMother.basic()
				.gameCells(Sets.newHashSet(number, mine))
				.build();
		Long row = mine.getRow();
		Long column = mine.getColumn();
		GameCellOperation gameCellOperation = GameCellOperationMother.revealed()
				.gameBean(gameBean)
				.row(row)
				.column(column)
				.build();
		when(gameCellHelper.isMine(number)).thenReturn(false);
		when(gameCellHelper.isMine(mine)).thenReturn(true);
		when(gameCellHelper.hasPosition(number, number.getRow(), number.getColumn())).thenReturn(true);
		when(gameCellHelper.hasPosition(mine, mine.getRow(), mine.getColumn())).thenReturn(true);

		assertThatExceptionOfType(MineExplodedException.class)
				.isThrownBy(() -> gameCellService.performOperation(gameCellOperation))
				.withMessage("Mine exploded in row=%s, column=%s on game with id=%s", row, column, gameBean.getId());
		verify(gameCellHelper, atLeastOnce()).isMine(any(GameCellBean.class));
		verify(gameCellHelper, atLeastOnce()).hasPosition(any(GameCellBean.class), anyLong(), anyLong());
		verify(gameCellRepository, times(1)).updateCellOperationById(CellOperation.REVEALED, mine.getId());
		verifyNoMoreInteractions(gameCellHelper);
	}

	@Test
	public void testRevealedOperationFunction() {
		CellOperation toUpdate = CellOperation.REVEALED;
		GameCellBean gameCellBean = GameCellBeanMother.number()
				.cellOperation(CellOperation.NONE)
				.build();
		GameBean gameBean = GameBeanMother.basic()
				.gameCells(Sets.newHashSet(gameCellBean))
				.build();
		GameCellOperation gameCellOperation = GameCellOperationMother.revealed()
				.gameBean(gameBean)
				.build();
		GameCellBean expected = GameCellBeanMother.number()
				.cellOperation(toUpdate)
				.minesAround(0L)
				.build();
		when(gameCellHelper.hasPosition(gameCellBean, gameCellOperation.getRow(), gameCellOperation.getColumn())).thenReturn(true);
		when(gameCellHelper.isMine(gameCellBean)).thenReturn(false);

		Function<GameCellOperation, Set<GameCellBean>> function = gameCellService.revealedOperationFunction();
		assertThat(function.apply(gameCellOperation)).contains(expected);
		verify(gameCellRepository, times(1)).updateCellOperationAndMinesAroundById(toUpdate, 0L, gameCellBean.getId());
	}

	@Test
	public void testRevealedOperationFunction_whenNoCellIsFound_throwsInvalidPositionException() {
		GameCellBean gameCellBean = GameCellBeanMother.number()
				.cellOperation(CellOperation.NONE)
				.build();
		GameBean gameBean = GameBeanMother.basic()
				.gameCells(Sets.newHashSet(gameCellBean))
				.build();
		GameCellOperation gameCellOperation = GameCellOperationMother.revealed()
				.gameBean(gameBean)
				.build();
		when(gameCellHelper.hasPosition(gameCellBean, gameCellOperation.getRow(), gameCellOperation.getColumn())).thenReturn(false);

		Function<GameCellOperation, Set<GameCellBean>> function = gameCellService.revealedOperationFunction();
		assertThatExceptionOfType(InvalidPositionException.class)
				.isThrownBy(() -> function.apply(gameCellOperation))
				.withMessage("Invalid position with row=%s and column=%s", gameCellOperation.getRow(), gameCellOperation.getColumn());
		verify(gameCellHelper, times(1)).hasPosition(gameCellBean, gameCellOperation.getRow(), gameCellOperation.getColumn());
		verifyNoMoreInteractions(gameCellHelper);
		verifyZeroInteractions(gameCellRepository);
	}

	@Test
	public void testRevealedOperationFunction_whenMineIsRevealed_throwsMineExplodedException() {
		CellOperation toUpdate = CellOperation.REVEALED;
		GameCellBean gameCellBean = GameCellBeanMother.number()
				.cellOperation(CellOperation.NONE)
				.build();
		GameBean gameBean = GameBeanMother.basic()
				.gameCells(Sets.newHashSet(gameCellBean))
				.build();
		GameCellOperation gameCellOperation = GameCellOperationMother.revealed()
				.gameBean(gameBean)
				.build();
		when(gameCellHelper.hasPosition(gameCellBean, gameCellOperation.getRow(), gameCellOperation.getColumn())).thenReturn(true);
		when(gameCellHelper.isMine(gameCellBean)).thenReturn(true);

		Function<GameCellOperation, Set<GameCellBean>> function = gameCellService.revealedOperationFunction();
		assertThatExceptionOfType(MineExplodedException.class)
				.isThrownBy(() -> function.apply(gameCellOperation))
				.withMessage("Mine exploded in row=%s, column=%s on game with id=%s",
						gameCellOperation.getRow(),
						gameCellOperation.getColumn(),
						gameBean.getId());
		verify(gameCellHelper, times(1)).hasPosition(gameCellBean, gameCellOperation.getRow(), gameCellOperation.getColumn());
		verify(gameCellHelper, times(1)).isMine(gameCellBean);
		verify(gameCellRepository, times(1)).updateCellOperationById(toUpdate, gameCellBean.getId());
		verifyNoMoreInteractions(gameCellHelper);
		verifyNoMoreInteractions(gameCellRepository);
	}

	@Test
	public void testQuestionMarkedOperationFunction() {
		CellOperation toUpdate = CellOperation.QUESTION_MARKED;
		GameCellBean gameCellBean = GameCellBeanMother.number()
				.cellOperation(CellOperation.NONE)
				.build();
		GameBean gameBean = GameBeanMother.basic()
				.gameCells(Sets.newHashSet(gameCellBean))
				.build();
		GameCellOperation gameCellOperation = GameCellOperationMother.revealed()
				.gameBean(gameBean)
				.build();
		Set<GameCellBean> expected = Sets.newHashSet(GameCellBeanMother.number()
				.cellOperation(toUpdate)
				.build());
		when(gameCellHelper.hasPosition(gameCellBean, gameCellOperation.getRow(), gameCellOperation.getColumn()))
				.thenReturn(true);

		Function<GameCellOperation, Set<GameCellBean>> function = gameCellService.questionMarkedFunction();
		assertThat(function.apply(gameCellOperation)).isEqualTo(expected);

		verify(gameCellHelper, times(1)).hasPosition(gameCellBean, gameCellOperation.getRow(), gameCellOperation.getColumn());
		verify(gameCellRepository, times(1)).updateCellOperationById(toUpdate, gameCellBean.getId());
	}

	@Test
	public void testFlaggedOperationFunction() {
		CellOperation toUpdate = CellOperation.FLAGGED;
		GameCellBean gameCellBean = GameCellBeanMother.number()
				.cellOperation(CellOperation.NONE)
				.build();
		GameBean gameBean = GameBeanMother.basic()
				.gameCells(Sets.newHashSet(gameCellBean))
				.build();
		GameCellOperation gameCellOperation = GameCellOperationMother.revealed()
				.gameBean(gameBean)
				.build();
		Set<GameCellBean> expected = Sets.newHashSet(GameCellBeanMother.number()
				.cellOperation(toUpdate)
				.build());
		when(gameCellHelper.hasPosition(gameCellBean, gameCellOperation.getRow(), gameCellOperation.getColumn()))
				.thenReturn(true);

		Function<GameCellOperation, Set<GameCellBean>> function = gameCellService.flaggedOperationFunction();
		assertThat(function.apply(gameCellOperation)).isEqualTo(expected);

		verify(gameCellHelper, times(1)).hasPosition(gameCellBean, gameCellOperation.getRow(), gameCellOperation.getColumn());
		verify(gameCellRepository, times(1)).updateCellOperationById(toUpdate, gameCellBean.getId());
	}

	@Test
	public void testUpdateCellWithCellOperation() {
		GameCellBean gameCellBean = GameCellBeanMother.number()
				.cellOperation(CellOperation.NONE)
				.build();
		GameBean gameBean = GameBeanMother.basic()
				.gameCells(Sets.newHashSet(gameCellBean))
				.build();
		GameCellOperation gameCellOperation = GameCellOperationMother.revealed()
				.gameBean(gameBean)
				.build();
		CellOperation cellOperation = CellOperation.FLAGGED;
		Set<GameCellBean> expected = Sets.newHashSet(GameCellBeanMother.number()
				.cellOperation(CellOperation.FLAGGED)
				.build());
		when(gameCellHelper.hasPosition(gameCellBean, gameCellOperation.getRow(), gameCellOperation.getColumn()))
				.thenReturn(true);

		assertThat(gameCellService.updateCellWithCellOperation(gameCellOperation, cellOperation)).isEqualTo(expected);

		verify(gameCellHelper, times(1)).hasPosition(gameCellBean, gameCellOperation.getRow(), gameCellOperation.getColumn());
		verify(gameCellRepository, times(1)).updateCellOperationById(cellOperation, gameCellBean.getId());
	}

	@Test
	public void testUpdateCellWithCellOperation_whenCellNotFound_throwsRuntimeException() {
		GameCellBean gameCellBean = GameCellBeanMother.number()
				.cellOperation(CellOperation.NONE)
				.build();
		GameBean gameBean = GameBeanMother.basic()
				.gameCells(Sets.newHashSet(gameCellBean))
				.build();
		GameCellOperation gameCellOperation = GameCellOperationMother.revealed()
				.gameBean(gameBean)
				.build();
		CellOperation cellOperation = CellOperation.FLAGGED;
		when(gameCellHelper.hasPosition(gameCellBean, gameCellOperation.getRow(), gameCellOperation.getColumn()))
				.thenReturn(false);

		assertThatExceptionOfType(RuntimeException.class)
				.isThrownBy(() -> gameCellService.updateCellWithCellOperation(gameCellOperation, cellOperation))
				.withMessage("Cell is not found in the DB. Unexpected error.");
		verify(gameCellHelper, times(1)).hasPosition(gameCellBean, gameCellOperation.getRow(), gameCellOperation.getColumn());
		verifyZeroInteractions(gameCellRepository);
	}

	@Test
	public void testUpdateCellOperationById() {
		CellOperation cellOperation = CellOperation.REVEALED;
		Long id = ID;

		gameCellService.updateCellOperationById(cellOperation, id);

		verify(gameCellRepository, times(1)).updateCellOperationById(cellOperation, id);
		verifyNoMoreInteractions(gameCellRepository);
	}

	@Test
	public void testUpdateCellOperationAndMinesAroundById() {
		CellOperation cellOperation = CellOperation.REVEALED;
		Long minesAround = 5L;
		Long id = ID;

		gameCellService.updateCellOperationAndMinesAroundById(cellOperation, minesAround, id);

		verify(gameCellRepository, times(1)).updateCellOperationAndMinesAroundById(cellOperation, minesAround, id);
		verifyNoMoreInteractions(gameCellRepository);
	}

	@Test
	public void testPopulateMinesAround_minesInCorners() {
		GameCellBean firstIsMine = GameCellBeanMother.mine().row(1L).column(1L).build();
		GameCellBean secondIsNumber = GameCellBeanMother.number().row(1L).column(2L).build();
		GameCellBean thirdIsMine = GameCellBeanMother.mine().row(1L).column(3L).build();
		GameCellBean fourthIsNumber = GameCellBeanMother.number().row(2L).column(1L).build();
		GameCellBean cellToCheck = GameCellBeanMother.number().row(2L).column(2L).build();
		GameCellBean sixthIsNumber = GameCellBeanMother.number().row(2L).column(3L).build();
		GameCellBean seventhIsMine = GameCellBeanMother.mine().row(3L).column(1L).build();
		GameCellBean eighthIsNumber = GameCellBeanMother.number().row(3L).column(2L).build();
		GameCellBean ninthIsMine = GameCellBeanMother.mine().row(3L).column(3L).build();
		Set<GameCellBean> cells = Sets.newHashSet(firstIsMine, secondIsNumber, thirdIsMine, fourthIsNumber, cellToCheck,
				sixthIsNumber, seventhIsMine, eighthIsNumber, ninthIsMine);
		GameBean gameBean = GameBeanMother.basic()
				.gameCells(cells)
				.build();

		// Mock responses for each cell except for the one to be tested
		cells
				.stream()
				.filter(cell -> !cell.equals(cellToCheck))
				.forEach(cell -> {
						when(gameCellHelper.isMine(cell)).thenReturn(CellContent.MINE.equals(cell.getCellContent()));
						when(gameCellHelper.hasPosition(cell, cell.getRow(), cell.getColumn())).thenReturn(true);
				});
		GameCellBean cellAfterMethod = GameCellBeanMother.number()
				.row(2L)
				.column(2L)
				.minesAround(4L)
				.build();

		Set<GameCellBean> result = gameCellService.populateMinesAround(cellToCheck, gameBean);

		assertThat(result.size()).isEqualTo(1);
		assertThat(result).contains(cellAfterMethod);
	}

	@Test
	public void testPopulateMinesAround_minesInCross() {
		GameCellBean firstIsNumber = GameCellBeanMother.number().row(1L).column(1L).build();
		GameCellBean secondIsMine = GameCellBeanMother.mine().row(1L).column(2L).build();
		GameCellBean thirdIsNumber = GameCellBeanMother.number().row(1L).column(3L).build();
		GameCellBean fourthIsMine = GameCellBeanMother.mine().row(2L).column(1L).build();
		GameCellBean cellToCheck = GameCellBeanMother.number().row(2L).column(2L).build();
		GameCellBean sixthIsMine = GameCellBeanMother.mine().row(2L).column(3L).build();
		GameCellBean seventhIsNumber = GameCellBeanMother.number().row(3L).column(1L).build();
		GameCellBean eighthIsMine = GameCellBeanMother.mine().row(3L).column(2L).build();
		GameCellBean ninthIsNumber = GameCellBeanMother.number().row(3L).column(3L).build();
		Set<GameCellBean> cells = Sets.newHashSet(firstIsNumber, secondIsMine, thirdIsNumber, fourthIsMine, cellToCheck,
				sixthIsMine, seventhIsNumber, eighthIsMine, ninthIsNumber);
		GameBean gameBean = GameBeanMother.basic()
				.gameCells(cells)
				.build();

		// Mock responses for each cell except for the one to be tested
		cells
				.stream()
				.filter(cell -> !cell.equals(cellToCheck))
				.forEach(cell -> {
					when(gameCellHelper.isMine(cell)).thenReturn(CellContent.MINE.equals(cell.getCellContent()));
					when(gameCellHelper.hasPosition(cell, cell.getRow(), cell.getColumn())).thenReturn(true);
				});
		GameCellBean cellAfterMethod = GameCellBeanMother.number()
				.row(2L)
				.column(2L)
				.minesAround(4L)
				.build();

		Set<GameCellBean> result = gameCellService.populateMinesAround(cellToCheck, gameBean);

		assertThat(result.size()).isEqualTo(1);
		assertThat(result).contains(cellAfterMethod);
	}

	@Test
	public void testBuildPossibleDirections() {
		Long row = NUMBER_ROW;
		Long column = NUMBER_COLUMN;

		List<Pair<Long, Long>> directions = gameCellService.buildPossibleDirections(row, column);

		assertThat(directions).hasSize(8);
		assertThat(directions).containsExactly(
				Pair.of(row - 1, column - 1),
				Pair.of(row - 1, column),
				Pair.of(row - 1, column + 1),
				Pair.of(row, column - 1),
				Pair.of(row, column + 1),
				Pair.of(row + 1, column - 1),
				Pair.of(row + 1, column),
				Pair.of(row + 1, column + 1));
	}

	@Test
	public void testHasMineInPosition_whenItsMine_returnTrue() {
		GameCellBean cell = GameCellBeanMother.number().build();
		GameBean gameBean = GameBeanMother.basic()
				.gameCells(Sets.newHashSet(cell))
				.build();
		Long row = 1L;
		Long column = 1L;
		Pair<Long, Long> pair = Pair.of(row, column);
		when(gameCellHelper.isMine(cell)).thenReturn(true);
		when(gameCellHelper.hasPosition(cell, row, column)).thenReturn(true);

		assertThat(gameCellService.hasMineInPosition(gameBean, pair)).isTrue();

		verify(gameCellHelper, times(1)).isMine(cell);
		verify(gameCellHelper, times(1)).hasPosition(cell, row, column);
		verifyNoMoreInteractions(gameCellHelper);
	}

	@Test
	public void testHasMineInPosition_whenItsNotMine_returnFalse() {
		GameCellBean cell = GameCellBeanMother.number().build();
		GameBean gameBean = GameBeanMother.basic()
				.gameCells(Sets.newHashSet(cell))
				.build();
		Long row = 1L;
		Long column = 1L;
		Pair<Long, Long> pair = Pair.of(row, column);
		when(gameCellHelper.isMine(cell)).thenReturn(false);
		when(gameCellHelper.hasPosition(cell, row, column)).thenReturn(true);

		assertThat(gameCellService.hasMineInPosition(gameBean, pair)).isFalse();

		verify(gameCellHelper, times(1)).isMine(cell);
		verifyNoMoreInteractions(gameCellHelper);
	}

	@Test
	public void testHasMineInPosition_whenItsOutsideOfBounds_returnFalse() {
		GameBean gameBean = GameBeanMother.basic()
				.build();
		Long row = 0L;
		Long column = 0L;
		Pair<Long, Long> pair = Pair.of(row, column);

		assertThat(gameCellService.hasMineInPosition(gameBean, pair)).isFalse();

		verifyZeroInteractions(gameCellHelper);
	}

	@Test
	public void testIsOutOfBounds_isTrue() {
		GameBean gameBean = GameBeanMother.basic().build();
		List<Pair<Long, Long>> limits = Arrays.asList(
				Pair.of(1L, 0L),
				Pair.of(0L, 0L),
				Pair.of(0L, 1L),
				Pair.of(0L, gameBean.getColumns()),
				Pair.of(0L, gameBean.getColumns() + 1),
				Pair.of(1L, gameBean.getColumns() + 1),
				Pair.of(gameBean.getRows(), gameBean.getColumns() + 1),
				Pair.of(gameBean.getRows() + 1, gameBean.getColumns() + 1),
				Pair.of(gameBean.getRows() + 1, gameBean.getColumns()),
				Pair.of(gameBean.getRows() + 1, 1L),
				Pair.of(gameBean.getRows() + 1, 0L),
				Pair.of(gameBean.getRows(), 0L));

		limits.forEach(pair -> assertThat(gameCellService.isOutOfBounds(gameBean, pair.getLeft(), pair.getRight())).isTrue());
	}

	@Test
	public void testIsOutOfBounds_isFalse() {
		GameBean gameBean = GameBeanMother.basic().build();
		List<Pair<Long, Long>> limits = Arrays.asList(
				Pair.of(1L, 1L),
				Pair.of(1L, gameBean.getColumns()),
				Pair.of(gameBean.getRows(), gameBean.getColumns()),
				Pair.of(gameBean.getRows(), 1L));

		limits.forEach(pair -> assertThat(gameCellService.isOutOfBounds(gameBean, pair.getLeft(), pair.getRight())).isFalse());
	}
}

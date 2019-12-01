package com.minesweeper.service.impl;

import static com.minesweeper.enums.CellOperation.FLAGGED;
import static com.minesweeper.enums.CellOperation.QUESTION_MARKED;
import static com.minesweeper.enums.CellOperation.REVEALED;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.minesweeper.bean.GameBean;
import com.minesweeper.bean.GameCellBean;
import com.minesweeper.bean.GameCellOperation;
import com.minesweeper.component.GameCellHelper;
import com.minesweeper.enums.CellContent;
import com.minesweeper.enums.CellOperation;
import com.minesweeper.exception.CellOperationNotSupportedException;
import com.minesweeper.exception.InvalidPositionException;
import com.minesweeper.exception.MineExplodedException;
import com.minesweeper.repository.GameCellRepository;
import com.minesweeper.service.GameCellService;

@Service
public class GameCellServiceImpl implements GameCellService {
	private final GameCellRepository gameCellRepository;
	private final GameCellHelper gameCellHelper;
	private final Map<CellOperation, Function<GameCellOperation, Set<GameCellBean>>> cellOperationFunction = ImmutableMap.of(
			REVEALED, revealedOperationFunction(),
			FLAGGED, flaggedOperationFunction(),
			QUESTION_MARKED, questionMarkedFunction());

	@Autowired
	public GameCellServiceImpl(GameCellRepository gameCellRepository,
							   GameCellHelper gameCellHelper) {
		this.gameCellRepository = gameCellRepository;
		this.gameCellHelper = gameCellHelper;
	}

	@Override
	public Set<GameCellBean> populateCells(GameBean gameBean) {
		long rows = gameBean.getRows();
		long columns = gameBean.getColumns();
		long minesQuantity = gameBean.getMines();

		Set<GameCellBean> cells = populateWithMines(rows, columns, minesQuantity);
		return populateWithNumbers(cells, rows, columns);
	}

	@Override
	public Set<GameCellBean> performOperation(GameCellOperation gameCellOperation) {
		return cellOperationFunction.entrySet().stream()
				.filter(entry -> entry.getKey().equals(gameCellOperation.getCellOperation()))
				.findFirst()
				.orElseThrow(() -> new CellOperationNotSupportedException(gameCellOperation.getCellOperation()))
				.getValue()
				.apply(gameCellOperation);
	}

	/**
	 * Private method that populates the {@link Set} received as parameter with mines in random and unique places.
	 */
	@VisibleForTesting
	Set<GameCellBean> populateWithMines(long rows, long columns, long mines) {
		List<Pair<Long, Long>> alreadyOccupiedCells = Lists.newArrayList();

		LongStream.range(1, mines +  1).forEach(i -> {
			
			// Generate a new pair inside the row and cols and verifying that it's not already occupied
			Pair<Long, Long> newPair = getNewRandomPairInRange(
					() -> ThreadLocalRandom.current().nextLong(1, rows + 1),
					() -> ThreadLocalRandom.current().nextLong(1, columns + 1),
					(row, col) -> alreadyOccupiedCells.stream().noneMatch(pair -> pair.compareTo(Pair.of(row, col)) == 0)
			);
			alreadyOccupiedCells.add(newPair);
		});

		return alreadyOccupiedCells.stream()
				.map(this::mapToGameCellBean)
				.collect(Collectors.toSet());
	}

	/**
	 * Private function that uses two {@link Supplier} passed as parameter to create new Integers (a row and a column) and
	 * verifies if it's valid with the {@link BiPredicate} also received as parameter. Continues to create random objects up
	 * until the rowColumnVerifier returns true
	 */
	@VisibleForTesting
	Pair<Long, Long> getNewRandomPairInRange(Supplier<Long> rowSupplier,
												   Supplier<Long> columnSupplier,
												   BiPredicate<Long, Long> rowColumnVerifier) {
		long randomRow = 0;
		long randomColumn = 0;
		boolean finished = false;
		while (!finished) {
			randomRow = rowSupplier.get();
			randomColumn = columnSupplier.get();
			if (rowColumnVerifier.test(randomRow, randomColumn)) {
				finished = true;
			}
		}
		return Pair.of(randomRow, randomColumn);
	}

	/**
	 * Private method that maps a {@link Pair} of (row, column) to {@link GameCellBean}
	 */
	@VisibleForTesting
	GameCellBean mapToGameCellBean(Pair<Long, Long> pair) {
		return GameCellBean.builder()
				.row(pair.getLeft())
				.column(pair.getRight())
				.cellContent(CellContent.MINE)
				.cellOperation(CellOperation.NONE)
				.build();
	}

	/**
	 * Private method that iterates over all the possible cells and fills the missing cells with NUMBER
	 */
	@VisibleForTesting
	Set<GameCellBean> populateWithNumbers(Set<GameCellBean> cells, Long rows, Long columns) {
		// We create an extra variable to compare always with the mines
		Set<GameCellBean> alreadyCalculatedMines = new HashSet<>(cells);
		for(long i = 1; i <= rows; i++) {
			for (long j = 1; j <= columns; j++) {
				long currentRow = i;
				long currentColumn = j;
				if (alreadyCalculatedMines.stream().noneMatch(cell -> (cell.getRow() == currentRow) && (cell.getColumn() == currentColumn))) {
					// If it wasn't part of the cells, then it's not a mine, we create a number
					cells.add(GameCellBean.builder()
							.row(i)
							.column(j)
							.cellOperation(CellOperation.NONE)
							.cellContent(CellContent.NUMBER)
							.build());
				}
			}
		}
		return cells;
	}

	/**
	 * Private method that contains all the logic to be done when the user is trying to reveal the content from a cell
	 */
	@VisibleForTesting
	Function<GameCellOperation, Set<GameCellBean>> revealedOperationFunction() {
		return gameCellOperation -> {
			Long row = gameCellOperation.getRow();
			Long column = gameCellOperation.getColumn();
			GameBean gameBean = gameCellOperation.getGameBean();

			GameCellBean cell = gameBean.getGameCells().stream()
					.filter(gameCellBean -> gameCellHelper.hasPosition(gameCellBean, row, column))
					.findFirst()
					.orElseThrow(() -> new InvalidPositionException(row, column));

			if (gameCellHelper.isMine(cell)) {
				updateCellOperationById(CellOperation.REVEALED, cell.getId());
				throw new MineExplodedException(row, column, gameBean);
			}

			Set<GameCellBean> updatedCells = populateMinesAround(cell, gameBean);
			updatedCells.forEach(updatedCell -> {
				updateCellOperationAndMinesAroundById(CellOperation.REVEALED, updatedCell.getMinesAround(), updatedCell.getId());
				updatedCell.setCellOperation(CellOperation.REVEALED);
			});
			return updatedCells;
		};
	}

	/**
	 * Private method that contains all the logic to be done when the user is trying to flag a cell
	 */
	@VisibleForTesting
	Function<GameCellOperation, Set<GameCellBean>> flaggedOperationFunction() {
		return gameCellOperation -> updateCellWithCellOperation(gameCellOperation, FLAGGED);
	}

	/**
	 * Private method that contains all the logic to be done when the user is trying to mark a cell with a question mark
	 */
	@VisibleForTesting
	Function<GameCellOperation, Set<GameCellBean>> questionMarkedFunction() {
		return gameCellOperation -> updateCellWithCellOperation(gameCellOperation, QUESTION_MARKED);
	}

	@VisibleForTesting
	Set<GameCellBean> updateCellWithCellOperation(GameCellOperation gameCellOperation, CellOperation cellOperation) {
		Long row = gameCellOperation.getRow();
		Long column = gameCellOperation.getColumn();
		GameBean gameBean = gameCellOperation.getGameBean();

		// We update the status from it in the DB
		GameCellBean existingCell = gameBean.getGameCells().stream()
				.filter(cell -> gameCellHelper.hasPosition(cell, row, column))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Cell is not found in the DB. Unexpected error."));

		updateCellOperationById(cellOperation, existingCell.getId());
		existingCell.setCellOperation(cellOperation);

		return Sets.newHashSet(existingCell);
	}

	@VisibleForTesting
	@Transactional
	void updateCellOperationById(CellOperation cellOperation, Long id) {
		gameCellRepository.updateCellOperationById(cellOperation, id);
	}

	@VisibleForTesting
	@Transactional
	void updateCellOperationAndMinesAroundById(CellOperation cellOperation, Long minesAround, Long id) {
		gameCellRepository.updateCellOperationAndMinesAroundById(cellOperation, minesAround, id);
	}

	/**
	 * Private method that calculates how many mines are around him.
	 */
	@VisibleForTesting
	Set<GameCellBean> populateMinesAround(GameCellBean gameCellBean, GameBean gameBean) {
		long row = gameCellBean.getRow();
		long column = gameCellBean.getColumn();

		// Check every possible direction (8 in total) and counting how many mines there are
		Stream<Boolean> results = buildPossibleDirections(row, column).stream()
				.map(pair -> hasMineInPosition(gameBean, pair));
		long minesAround = results
				.filter(Boolean::booleanValue)
				.count();

		gameCellBean.setMinesAround(minesAround);

		// if minesAround is zero then must call the neighboorhoods and re-apply same logic
		return Sets.newHashSet(gameCellBean);
	}

	/**
	 * Creates a {@link List} of {@link Pair} with all the adyacents directions from the cell
	 */
	@VisibleForTesting
	List<Pair<Long, Long>> buildPossibleDirections(Long row, Long column) {
		return Arrays.asList(Pair.of(row - 1, column - 1),
				Pair.of(row - 1, column),
				Pair.of(row - 1, column + 1),
				Pair.of(row, column - 1),
				Pair.of(row, column + 1),
				Pair.of(row + 1, column - 1),
				Pair.of(row + 1, column),
				Pair.of(row + 1, column + 1));
	}

	/**
	 * Iterates over all the existing {@link GameCellBean} and checks if the one with position defined by the {@link Pair}
	 * passed as parameter has its content as {@link CellContent#MINE}
	 */
	@VisibleForTesting
	boolean hasMineInPosition(GameBean gameBean, Pair<Long, Long> rowColumnPair) {
		if (isOutOfBounds(gameBean, rowColumnPair.getLeft(), rowColumnPair.getRight())) {
			return false;
		}

		return gameBean.getGameCells().stream().anyMatch(gameCellBean -> 
				gameCellHelper.isMine(gameCellBean) && 
				gameCellHelper.hasPosition(gameCellBean, rowColumnPair.getLeft(), rowColumnPair.getRight()));
	}

	/**
	 * Private method that checks if the row and column passed as parameter are Out of Bounds of the Game.
	 */
	@VisibleForTesting
	boolean isOutOfBounds(GameBean gameBean, long row, long column) {
		return ((row < 1) ||
				(row > gameBean.getRows()) ||
				(column < 1) ||
				(column > gameBean.getColumns()));
	}
}

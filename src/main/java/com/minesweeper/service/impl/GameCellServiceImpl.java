package com.minesweeper.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import lombok.AllArgsConstructor;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.minesweeper.bean.GameBean;
import com.minesweeper.bean.GameCellBean;
import com.minesweeper.component.GameCellHelper;
import com.minesweeper.enums.CellContent;
import com.minesweeper.enums.CellOperation;
import com.minesweeper.exception.MineExplodedException;
import com.minesweeper.service.GameCellService;

@AllArgsConstructor
@Service
public class GameCellServiceImpl implements GameCellService {
	private final GameCellHelper gameCellHelper;

	@Override
	public Set<GameCellBean> generateRandomMines(GameBean gameBean) {
		long rows = gameBean.getRows();
		long columns = gameBean.getColumns();
		long mines = gameBean.getMines();

		return populateWithMines(rows, columns, mines);
	}

	@Override
	public List<GameCellBean> performOperation(GameBean gameBean, CellOperation cellOperation, Long row, Long column) {
		if (gameBean.getGameCells().stream()
				.filter(gameCellHelper::isMine)
				.anyMatch(gameCellBean -> hasPosition(gameCellBean, row, column))) {
			throw new MineExplodedException(row, column, gameBean);
		}
		return Collections.emptyList();
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
	 * Private method that checks if the {@link GameCellBean} passed as parameter has the position defined by the parameters
	 */
	@VisibleForTesting
	boolean hasPosition(GameCellBean gameCellBean, Long row, Long column) {
		return row.longValue() == gameCellBean.getRow().longValue() &&
				column.longValue() == gameCellBean.getColumn().longValue();
	}
}

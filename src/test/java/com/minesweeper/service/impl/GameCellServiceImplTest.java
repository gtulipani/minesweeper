package com.minesweeper.service.impl;

import static com.minesweeper.utils.TestConstants.COLUMNS;
import static com.minesweeper.utils.TestConstants.MINES;
import static com.minesweeper.utils.TestConstants.ROWS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.util.Sets;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.minesweeper.bean.GameBean;
import com.minesweeper.bean.GameCellBean;
import com.minesweeper.enums.CellContent;
import com.minesweeper.mother.GameBeanMother;
import com.minesweeper.mother.GameCellBeanMother;

public class GameCellServiceImplTest {
	private GameCellServiceImpl gameCellService;

	@BeforeMethod
	public void setup() {
		gameCellService = new GameCellServiceImpl();
	}

	@Test
	public void testGenerateRandomMines() {
		GameBean gameBean = GameBeanMother.basic().build();
		Set<GameCellBean> gameCellBeans = gameCellService.generateRandomMines(gameBean);
		assertThat(gameCellBeans.size()).isEqualTo(gameBean.getMines().intValue());
		Set<Pair<Long, Long>> rowColumnPairs = Sets.newHashSet();
		gameCellBeans.stream().forEach(gameCellBean -> {
			assertThat(gameCellBean.getCellContent()).isEqualTo(CellContent.MINE);
			rowColumnPairs.add(Pair.of(gameCellBean.getRow(), gameCellBean.getColumn()));
		});
		assertThat(rowColumnPairs.size()).isEqualTo(gameBean.getMines().intValue());
	}

	@Test
	public void testPopulateWithMines() {
		Set<GameCellBean> gameCellBeans = gameCellService.populateWithMines(ROWS, COLUMNS, MINES);

		assertThat(gameCellBeans.size()).isEqualTo(MINES);
		Set<Pair<Long, Long>> rowColumnPairs = Sets.newHashSet();
		gameCellBeans.stream().forEach(gameCellBean -> {
			assertThat(gameCellBean.getCellContent()).isEqualTo(CellContent.MINE);
			rowColumnPairs.add(Pair.of(gameCellBean.getRow(), gameCellBean.getColumn()));
		});
		assertThat(rowColumnPairs.size()).isEqualTo(MINES);
	}

	@Test
	public void testGetNewRandomPairInRange_createsPairAndFinishes() {
		Supplier<Long> rowSupplier = () -> ROWS;
		Supplier<Long> columnSupplier = () -> COLUMNS;
		BiPredicate<Long, Long> rowColumnVerifier = (row, column) -> true;
		Pair<Long, Long> expectedPair = Pair.of(ROWS, COLUMNS);

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
		Pair<Long, Long> pair = Pair.of(ROWS, COLUMNS);
		GameCellBean gameCellBean = GameCellBeanMother.mine().build();

		assertThat(gameCellService.mapToGameCellBean(pair)).isEqualTo(gameCellBean);
	}
}

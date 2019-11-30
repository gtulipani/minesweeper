package com.minesweeper.service.impl;

import static com.minesweeper.utils.TestConstants.COLUMNS_QUANTITY;
import static com.minesweeper.utils.TestConstants.MINES_QUANTITY;
import static com.minesweeper.utils.TestConstants.MINE_COLUMNS;
import static com.minesweeper.utils.TestConstants.MINE_ROW;
import static com.minesweeper.utils.TestConstants.ROWS_QUANTITY;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;
import com.minesweeper.bean.GameBean;
import com.minesweeper.bean.GameCellBean;
import com.minesweeper.component.GameCellHelper;
import com.minesweeper.enums.CellContent;
import com.minesweeper.enums.CellOperation;
import com.minesweeper.exception.MineExplodedException;
import com.minesweeper.mother.GameBeanMother;
import com.minesweeper.mother.GameCellBeanMother;

public class GameCellServiceImplTest {
	@Mock
	private GameCellHelper gameCellHelper;

	private GameCellServiceImpl gameCellService;

	@BeforeMethod
	public void setup() {
		MockitoAnnotations.initMocks(this);
		gameCellService = new GameCellServiceImpl(gameCellHelper);
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
	public void testPerformOperation_onNumber_doesntThrowException() {
		GameCellBean number = GameCellBeanMother.number().build();
		GameCellBean mine = GameCellBeanMother.mine().build();
		GameBean gameBean = GameBeanMother.basic()
				.gameCells(Sets.newHashSet(number, mine))
				.build();
		CellOperation cellOperation = CellOperation.REVEALED;
		Long row = number.getRow();
		Long column = number.getColumn();
		when(gameCellHelper.isMine(number)).thenReturn(false);
		when(gameCellHelper.isMine(mine)).thenReturn(true);

		assertThatCode(() -> gameCellService.performOperation(gameBean, cellOperation, row, column))
				.doesNotThrowAnyException();
		verify(gameCellHelper, times(1)).isMine(number);
		verify(gameCellHelper, times(1)).isMine(mine);
		verifyNoMoreInteractions(gameCellHelper);
	}

	@Test
	public void testPerformOperation_onMine_throwMineExplodedException() {
		GameCellBean number = GameCellBeanMother.number().build();
		GameCellBean mine = GameCellBeanMother.mine().build();
		GameBean gameBean = GameBeanMother.basic()
				.gameCells(Sets.newHashSet(number, mine))
				.build();
		CellOperation cellOperation = CellOperation.REVEALED;
		Long row = mine.getRow();
		Long column = mine.getColumn();
		when(gameCellHelper.isMine(number)).thenReturn(false);
		when(gameCellHelper.isMine(mine)).thenReturn(true);

		assertThatExceptionOfType(MineExplodedException.class)
				.isThrownBy(() -> gameCellService.performOperation(gameBean, cellOperation, row, column))
				.withMessage("Mine exploded in row=%s, column=%s on game with id=%s", row, column, gameBean.getId());
		verify(gameCellHelper, atLeastOnce()).isMine(any(GameCellBean.class));
		verifyNoMoreInteractions(gameCellHelper);
	}
}

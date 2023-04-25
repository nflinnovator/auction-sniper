package auction.sniper.unit;

import static auction.sniper.core.SniperSnapshot.SniperState.WINNING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import auction.sniper.adapters.ui.SnipersTableModel;
import auction.sniper.adapters.ui.SnipersTableModel.Column;
import auction.sniper.core.AuctionSniper;
import auction.sniper.core.Item;
import auction.sniper.core.SniperSnapshot;

@DisplayName("Snipers Table Model Unit Test Case")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SnipersTableModelTest {

	private final Mockery context = new Mockery();
	private TableModelListener listener = context.mock(TableModelListener.class);
	private final SnipersTableModel model = new SnipersTableModel();
	private final AuctionSniper sniper = new AuctionSniper(new Item("item 0", 1234), null);

	@BeforeEach
	public void attachModelListener() {
		model.addTableModelListener(listener);
	}

	@Test
	@Order(1)
	void hasEnoughColumns() {
		assertThat(model.getColumnCount(), equalTo(Column.values().length));
	}

	@Test
	@Order(2)
	void setsUpColumnHeadings() {
		for (Column column : Column.values()) {
			assertEquals(column.name, model.getColumnName(column.ordinal()));
		}
	}

	@Test
	@Order(3)
	void setsSniperValuesInColumns() {

		context.checking(new Expectations() {
			{
				allowing(listener).tableChanged(with(anyInsertionEvent()));
				oneOf(listener).tableChanged(with(aChangeInRow(0)));
			}
		});

		model.sniperAdded(sniper);
		SniperSnapshot bidding = sniper.getSnapshot();
		model.sniperStateChanged(bidding);

		assertRowMatchesSnapshot(0, bidding);
	}

	@Test
	@Order(4)
	void notifiesListenersWhenAddingASniper() {
		context.checking(new Expectations() {
			{
				oneOf(listener).tableChanged(with(anInsertionAtRow(0)));
			}
		});

		assertEquals(0, model.getRowCount());

		model.sniperAdded(sniper);

		assertEquals(1, model.getRowCount());
		SniperSnapshot joining = sniper.getSnapshot();
		assertRowMatchesSnapshot(0, joining);
	}

	@Test
	@Order(5)
	void holdsSnipersInAdditionOrder() {
		final var sniper2 = new AuctionSniper(new Item("item 1", 1234), null);
		context.checking(new Expectations() {
			{
				ignoring(listener);
			}
		});

		model.sniperAdded(sniper);
		model.sniperAdded(sniper2);

		assertEquals("item 0", cellValue(0, Column.ITEM_IDENTIFIER));
		assertEquals("item 1", cellValue(1, Column.ITEM_IDENTIFIER));
	}

	@Test
	@Order(6)
	void updatesCorrectRowForSniper() {
		AuctionSniper sniper2 = new AuctionSniper(new Item("item 1", 1234), null);
		context.checking(new Expectations() {
			{
				allowing(listener).tableChanged(with(anyInsertionEvent()));

				oneOf(listener).tableChanged(with(aChangeInRow(1)));
			}
		});

		model.sniperAdded(sniper);
		model.sniperAdded(sniper2);

		SniperSnapshot winning1 = sniper2.getSnapshot().winning(123);
		model.sniperStateChanged(winning1);

		assertRowMatchesSnapshot(1, winning1);
	}

	@Test
	@Order(7)
	void throwsDefectIfNoExistingSniperForAnUpdate() {

		assertThrows(RuntimeException.class, () -> {
			model.sniperStateChanged(new SniperSnapshot("item 1", 123, 234, WINNING));
		});
	}

	Matcher<TableModelEvent> anyInsertionEvent() {
		return hasProperty("type", equalTo(TableModelEvent.INSERT));
	}

	private Matcher<TableModelEvent> aChangeInRow(int row) {
		return samePropertyValuesAs(new TableModelEvent(model, row));
	}

	private void assertRowMatchesSnapshot(int row, SniperSnapshot snapshot) {
		assertEquals(snapshot.getItemId(), cellValue(row, Column.ITEM_IDENTIFIER));
		assertEquals(snapshot.getLastPrice(), cellValue(row, Column.LAST_PRICE));
		assertEquals(snapshot.getLastBid(), cellValue(row, Column.LAST_BID));
		assertEquals(SnipersTableModel.textFor(snapshot.getState()), cellValue(row, Column.SNIPER_STATE));
	}

	private Object cellValue(int rowIndex, Column column) {
		return model.getValueAt(rowIndex, column.ordinal());
	}

	private Matcher<TableModelEvent> anInsertionAtRow(int row) {
		return samePropertyValuesAs(
				new TableModelEvent(model, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
	}

}

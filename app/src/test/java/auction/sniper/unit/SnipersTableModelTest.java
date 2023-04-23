package auction.sniper.unit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import auction.sniper.SniperSnapshot;
import auction.sniper.ui.SnipersTableModel;
import auction.sniper.ui.SnipersTableModel.Column;

@DisplayName("Snipers Table Model Test Case")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SnipersTableModelTest {

	private final JUnit5Mockery context = new JUnit5Mockery();
	private TableModelListener listener = context.mock(TableModelListener.class);
	private final SnipersTableModel model = new SnipersTableModel();

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
		SniperSnapshot joining = SniperSnapshot.joining("item id");
		SniperSnapshot bidding = joining.bidding(555, 666);
		context.checking(new Expectations() {
			{
				allowing(listener).tableChanged(with(anyInsertionEvent()));
				oneOf(listener).tableChanged(with(aChangeInRow(0)));
			}
		});
		model.addSniper(joining);
		model.sniperStateChanged(bidding);
		assertRowMatchesSnapshot(0, bidding);
	}

	@Test
	@Order(4)
	void notifiesListenersWhenAddingASniper() {
		SniperSnapshot joining = SniperSnapshot.joining("item123");
		context.checking(new Expectations() {
			{
				oneOf(listener).tableChanged(with(anInsertionAtRow(0)));
			}
		});
		assertEquals(0, model.getRowCount());
		model.addSniper(joining);
		assertEquals(1, model.getRowCount());
		assertRowMatchesSnapshot(0, joining);
	}

	@Test
	void holdsSnipersInAdditionOrder() {
		context.checking(new Expectations() {
			{
				ignoring(listener);
			}
		});
		model.addSniper(SniperSnapshot.joining("item 0"));
		model.addSniper(SniperSnapshot.joining("item 1"));
		assertEquals("item 0", cellValue(0, Column.ITEM_IDENTIFIER));
		assertEquals("item 1", cellValue(1, Column.ITEM_IDENTIFIER));
	}

	Matcher<TableModelEvent> anyInsertionEvent() {
		return hasProperty("type", equalTo(TableModelEvent.INSERT));
	}

	private Matcher<TableModelEvent> aChangeInRow(int row) {
		return samePropertyValuesAs(new TableModelEvent(model, row));
	}

	private void assertRowMatchesSnapshot(int row, SniperSnapshot snapshot) {
		assertEquals(snapshot.itemId, cellValue(row, Column.ITEM_IDENTIFIER));
		assertEquals(snapshot.lastPrice, cellValue(row, Column.LAST_PRICE));
		assertEquals(snapshot.lastBid, cellValue(row, Column.LAST_BID));
		assertEquals(SnipersTableModel.textFor(snapshot.state), cellValue(row, Column.SNIPER_STATE));
	}

	private Object cellValue(int rowIndex, Column column) {
		return model.getValueAt(rowIndex, column.ordinal());
	}

	private Matcher<TableModelEvent> anInsertionAtRow(int row) {
		return samePropertyValuesAs(
				new TableModelEvent(model, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
	}

}

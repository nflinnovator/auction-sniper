package auction.sniper.unit;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static auction.sniper.ui.SnipersTableModel.textFor;

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
import auction.sniper.SniperSnapshot.SniperState;
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
		context.checking(new Expectations() {
			{
				oneOf(listener).tableChanged(with(aRowChangedEvent()));
			}
		});

		model.sniperStateChanged(new SniperSnapshot("item id", 555, 666, SniperState.BIDDING));

		assertColumnEquals(Column.ITEM_IDENTIFIER, "item id");
		assertColumnEquals(Column.LAST_PRICE, 555);
		assertColumnEquals(Column.LAST_BID, 666);
		assertColumnEquals(Column.SNIPER_STATE, textFor(SniperState.BIDDING));
	}

	private void assertColumnEquals(Column column, Object expected) {
		final int rowIndex = 0;
		final int columnIndex = column.ordinal();
		assertEquals(expected, model.getValueAt(rowIndex, columnIndex));
	}

	private Matcher<TableModelEvent> aRowChangedEvent() {
		return samePropertyValuesAs(new TableModelEvent(model, 0));
	}

}

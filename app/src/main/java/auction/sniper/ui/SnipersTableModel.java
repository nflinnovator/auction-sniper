package auction.sniper.ui;

import javax.swing.table.AbstractTableModel;

import auction.sniper.SniperListener;
import auction.sniper.SniperSnapshot;
import auction.sniper.SniperSnapshot.SniperState;

public class SnipersTableModel extends AbstractTableModel implements SniperListener {

	private static final long serialVersionUID = 1L;

	private final static SniperSnapshot STARTING_UP = new SniperSnapshot("", 0, 0, SniperState.JOINING);

	private final static String[] STATUS_TEXT = { "Joining", "Bidding", "Winning", "Lost", "Won" };

	private SniperSnapshot snapshot = STARTING_UP;

	@Override
	public int getColumnCount() {
		return Column.values().length;
	}

	@Override
	public int getRowCount() {
		return 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return Column.at(columnIndex).valueIn(snapshot);
	}

	@Override
	public String getColumnName(int column) {
		return Column.at(column).name;
	}

	@Override
	public void sniperStateChanged(SniperSnapshot newSnapshot) {
		this.snapshot = newSnapshot;
		fireTableRowsUpdated(0, 0);
	}

	public static String textFor(SniperState state) {
		return STATUS_TEXT[state.ordinal()];
	}

	public enum Column {

		ITEM_IDENTIFIER("Item") {
			@Override
			public Object valueIn(SniperSnapshot snapshot) {
				return snapshot.itemId;
			}
		},
		LAST_PRICE("Last Price") {
			@Override
			public Object valueIn(SniperSnapshot snapshot) {
				return snapshot.lastPrice;
			}
		},
		LAST_BID("Last Bid") {
			@Override
			public Object valueIn(SniperSnapshot snapshot) {
				return snapshot.lastBid;
			}
		},
		SNIPER_STATE("State") {
			@Override
			public Object valueIn(SniperSnapshot snapshot) {
				return SnipersTableModel.textFor(snapshot.state);
			}

		};

		abstract public Object valueIn(SniperSnapshot snapshot);

		public static Column at(int offset) {
			return values()[offset];
		}

		public final String name;

		private Column(String name) {
			this.name = name;
		}
	}

}

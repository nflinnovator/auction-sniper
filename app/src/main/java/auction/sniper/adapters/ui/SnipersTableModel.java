package auction.sniper.adapters.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import com.objogate.exception.Defect;

import auction.sniper.core.AuctionSniper;
import auction.sniper.core.SniperListener;
import auction.sniper.core.SniperSnapshot;
import auction.sniper.core.SniperPortfolio.PortfolioListener;
import auction.sniper.core.SniperSnapshot.SniperState;

public class SnipersTableModel extends AbstractTableModel implements SniperListener, PortfolioListener {

	private static final long serialVersionUID = 1L;

	private final static String[] STATUS_TEXT = { "Joining", "Bidding", "Winning", "Losing", "Lost", "Won" };

	private List<SniperSnapshot> snapshots = new ArrayList<>();

	@Override
	public int getColumnCount() {
		return Column.values().length;
	}

	@Override
	public int getRowCount() {
		return snapshots.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return Column.at(columnIndex).valueIn(snapshots.get(rowIndex));
	}

	@Override
	public String getColumnName(int column) {
		return Column.at(column).name;
	}

	@Override
	public void sniperStateChanged(SniperSnapshot newSnapshot) {
		int row = rowMatching(newSnapshot);
		snapshots.set(row, newSnapshot);
		fireTableRowsUpdated(row, row);
	}

	@Override
	public void sniperAdded(AuctionSniper sniper) {
		addSniperSnapshot(sniper.getSnapshot());
		sniper.addSniperListener(new SwingThreadSniperListener(this));
	}

	private void addSniperSnapshot(SniperSnapshot snapshot) {
		snapshots.add(snapshot);
		int row = snapshots.size() - 1;
		fireTableRowsInserted(row, row);
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

	private int rowMatching(SniperSnapshot snapshot) {
		for (int i = 0; i < snapshots.size(); i++) {
			if (snapshot.isForSameItemAs(snapshots.get(i))) {
				return i;
			}
		}
		throw new Defect("Cannot find match for " + snapshot);
	}

	private class SwingThreadSniperListener implements SniperListener {

		SniperListener sniperListener;

		SwingThreadSniperListener(SniperListener listener) {
			this.sniperListener = listener;
		}

		@Override
		public void sniperStateChanged(SniperSnapshot state) {
			SwingUtilities.invokeLater(() -> sniperListener.sniperStateChanged(state));
		}
	}

}

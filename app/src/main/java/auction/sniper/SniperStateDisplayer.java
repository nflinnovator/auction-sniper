package auction.sniper;

import static auction.sniper.ui.MainWindow.STATUS_BIDDING;
import static auction.sniper.ui.MainWindow.STATUS_LOST;
import static auction.sniper.ui.MainWindow.STATUS_WINNING;

import javax.swing.SwingUtilities;

import auction.sniper.ui.MainWindow;

public class SniperStateDisplayer implements SniperListener {
	
	private MainWindow ui;
	
	public SniperStateDisplayer(MainWindow ui) {
		this.ui = ui;
	}

	@Override
	public void sniperBidding() {
		showStatus(STATUS_BIDDING);
	} 
	
	@Override
	public void sniperLost() {
		showStatus(STATUS_LOST);
	}
	
	@Override
	public void sniperWinning() {
		showStatus(STATUS_WINNING);
	}
	
	private void showStatus(final String status) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() { ui.showStatus(status); }
		});
	}

}

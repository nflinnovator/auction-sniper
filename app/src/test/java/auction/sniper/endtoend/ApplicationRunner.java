package auction.sniper.endtoend;

import static auction.sniper.endtoend.FakeAuctionServer.XMPP_HOSTNAME;
import static auction.sniper.ui.MainWindow.APPLICATION_TITLE;
import static auction.sniper.ui.SnipersTableModel.textFor;

import auction.sniper.App;
import auction.sniper.SniperSnapshot.SniperState;

public class ApplicationRunner {

	static final String SNIPER_XMPP_ID = "sniper@56c832ad6430/Auction";
	public static final String SNIPER_ID = "sniper";
	static final String SNIPER_PASSWORD = "sniper";
	private AuctionSniperDriver driver;

	private String itemId;

	void startBiddingIn(final FakeAuctionServer auction) {
		itemId = auction.getItemId();
		var thread = new Thread("Test Application") {
			@Override
			public void run() {
				try {
					App.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
		
		driver = new AuctionSniperDriver(10000);
		driver.hasTitle(APPLICATION_TITLE);
		driver.hasColumnTitles();
		driver.showsSniperStatus("", 0, 0, textFor(SniperState.JOINING));
	}

	public void hasShownSniperIsBidding(int lastPrice, int lastBid) {
        driver.showsSniperStatus(itemId, lastPrice, lastBid, textFor(SniperState.BIDDING));
    }

    public void hasShownSniperIsWinning(int winningBid) {
        driver.showsSniperStatus(itemId, winningBid, winningBid, textFor(SniperState.WINNING));
    }

	public void showsSniperHasLostAuction(int lastPrice, int lastBid) {
        driver.showsSniperStatus(itemId, lastPrice, lastBid, textFor(SniperState.LOST));
    }

    public void showsSniperHasWonAuction(int lastPrice) {
        driver.showsSniperStatus(itemId, lastPrice, lastPrice, textFor(SniperState.WON));
    }

	void stop() {
		if (driver != null)
			driver.dispose();
	}

}

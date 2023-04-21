package auction.sniper.endtoend;

import static auction.sniper.endtoend.FakeAuctionServer.XMPP_HOSTNAME;
import static auction.sniper.ui.MainWindow.STATUS_JOINING;
import static auction.sniper.ui.MainWindow.STATUS_BIDDING;
import static auction.sniper.ui.MainWindow.STATUS_LOST;

import auction.sniper.App;

class ApplicationRunner {

	static final String SNIPER_XMPP_ID = "sniper@56c832ad6430/Auction";
	static final String SNIPER_ID = "sniper";
	static final String SNIPER_PASSWORD = "sniper";
	private AuctionSniperDriver driver;

	void startBiddingIn(final FakeAuctionServer auction) {
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
		driver = new AuctionSniperDriver(1000);
		driver.showsSniperStatus(STATUS_JOINING);
	}

	public void hasShownSniperIsBidding() {
		driver.showsSniperStatus(STATUS_BIDDING);
	}

	void showsSniperHasLostAuction() {
		driver.showsSniperStatus(STATUS_LOST);
	}

	void stop() {
		if (driver != null)
			driver.dispose();
	}

}

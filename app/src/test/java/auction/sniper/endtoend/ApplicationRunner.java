package auction.sniper.endtoend;

import static auction.sniper.adapters.ui.SnipersTableModel.textFor;
import static auction.sniper.core.SniperSnapshot.SniperState.BIDDING;
import static auction.sniper.core.SniperSnapshot.SniperState.JOINING;
import static auction.sniper.core.SniperSnapshot.SniperState.LOST;
import static auction.sniper.core.SniperSnapshot.SniperState.WINNING;
import static auction.sniper.core.SniperSnapshot.SniperState.LOSING;
import static auction.sniper.core.SniperSnapshot.SniperState.WON;
import static auction.sniper.endtoend.FakeAuctionServer.XMPP_HOSTNAME;
import static auction.sniper.ui.MainWindow.APPLICATION_TITLE;

import auction.sniper.App;

public class ApplicationRunner {

	public static final String SNIPER_XMPP_ID = "sniper@56c832ad6430/Auction";
	public static final String SNIPER_ID = "sniper";
	public static final String SNIPER_PASSWORD = "sniper";
	private AuctionSniperDriver driver;

	public void startBiddingIn(FakeAuctionServer... auctions) {
		startSniper();
		for (FakeAuctionServer auction : auctions) {
			openBiddingFor(auction, Integer.MAX_VALUE);
		}
	}

	public void startBiddingWithStopPrice(FakeAuctionServer auction, int stopPrice) {
		startSniper();
		openBiddingFor(auction, stopPrice);
	}

	public void hasShownSniperIsBidding(FakeAuctionServer auction, int lastPrice, int lastBid) {
		driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, textFor(BIDDING));
	}

	public void hasShownSniperIsWinning(FakeAuctionServer auction, int winningBid) {
		driver.showsSniperStatus(auction.getItemId(), winningBid, winningBid, textFor(WINNING));
	}

	public void showsSniperHasLostAuction(FakeAuctionServer auction, int lastPrice, int lastBid) {
		driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, textFor(LOST));
	}

	public void showsSniperHasWonAuction(FakeAuctionServer auction, int lastPrice) {
		driver.showsSniperStatus(auction.getItemId(), lastPrice, lastPrice, textFor(WON));
	}

	public void hasShownSniperIsLosing(FakeAuctionServer auction, int lastPrice, int lastBid) {
		driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, textFor(LOSING));
	}

	void stop() {
		if (driver != null)
			driver.dispose();
	}

	private void startSniper() {
		Thread thread = new Thread("Test Application") {
			@Override
			public void run() {
				try {
					App.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
		driver = new AuctionSniperDriver(1000);
		driver.hasTitle(APPLICATION_TITLE);
		driver.hasColumnTitles();
	}

	private void openBiddingFor(FakeAuctionServer auction, int stopPrice) {
		final String itemId = auction.getItemId();
		driver.startBiddingFor(itemId, stopPrice);
		driver.showsSniperStatus(itemId, 0, 0, textFor(JOINING));
	}

	protected static String[] arguments(FakeAuctionServer... auctions) {
		String[] arguments = new String[auctions.length + 3];
		arguments[0] = XMPP_HOSTNAME;
		arguments[1] = SNIPER_ID;
		arguments[2] = SNIPER_PASSWORD;
		for (int i = 0; i < auctions.length; i++) {
			arguments[i + 3] = auctions[i].getItemId();
		}
		return arguments;
	}

}

package auction.sniper.endtoend;

import static auction.sniper.adapters.ui.SnipersTableModel.textFor;
import static auction.sniper.core.SniperSnapshot.SniperState.BIDDING;
import static auction.sniper.core.SniperSnapshot.SniperState.JOINING;
import static auction.sniper.core.SniperSnapshot.SniperState.LOST;
import static auction.sniper.core.SniperSnapshot.SniperState.WINNING;
import static auction.sniper.core.SniperSnapshot.SniperState.LOSING;
import static auction.sniper.core.SniperSnapshot.SniperState.WON;
import static auction.sniper.core.SniperSnapshot.SniperState.FAILED;
import static auction.sniper.endtoend.FakeAuctionServer.XMPP_HOSTNAME;
import static auction.sniper.ui.MainWindow.APPLICATION_TITLE;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import java.io.File;
import java.io.IOException;
import java.util.logging.LogManager;

import org.apache.commons.io.FileUtils;
import org.hamcrest.Matcher;

import auction.sniper.App;

public class ApplicationRunner {

	public static final String SNIPER_XMPP_ID = "sniper@56c832ad6430/Auction";
	public static final String SNIPER_ID = "sniper";
	public static final String SNIPER_PASSWORD = "sniper";
	private AuctionSniperDriver driver;
	private AuctionLogDriver logDriver = new AuctionLogDriver();

	void startBiddingIn(FakeAuctionServer... auctions) {
		startSniper();
		for (FakeAuctionServer auction : auctions) {
			openBiddingFor(auction, Integer.MAX_VALUE);
		}
	}

	void startBiddingWithStopPrice(FakeAuctionServer auction, int stopPrice) {
		startSniper();
		openBiddingFor(auction, stopPrice);
	}

	void hasShownSniperIsBidding(FakeAuctionServer auction, int lastPrice, int lastBid) {
		driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, textFor(BIDDING));
	}

	void hasShownSniperIsWinning(FakeAuctionServer auction, int winningBid) {
		driver.showsSniperStatus(auction.getItemId(), winningBid, winningBid, textFor(WINNING));
	}

	void showsSniperHasLostAuction(FakeAuctionServer auction, int lastPrice, int lastBid) {
		driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, textFor(LOST));
	}

	void showsSniperHasWonAuction(FakeAuctionServer auction, int lastPrice) {
		driver.showsSniperStatus(auction.getItemId(), lastPrice, lastPrice, textFor(WON));
	}

	void hasShownSniperIsLosing(FakeAuctionServer auction, int lastPrice, int lastBid) {
		driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, textFor(LOSING));
	}

	void showsSniperHasFailed(FakeAuctionServer auction) {
		driver.showsSniperStatus(auction.getItemId(), 0, 0, textFor(FAILED));
	}

	void reportsInvalidMessage(FakeAuctionServer auction, String brokenMessage) throws IOException {
		logDriver.hasEntry(containsString(brokenMessage));
	}

	void stop() {
		if (driver != null)
			driver.dispose();
	}

	private void startSniper() {
		logDriver.clearLog();
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

	private class AuctionLogDriver {
		public static final String LOG_FILE_NAME = "auction-sniper.log";
		private final File logFile = new File(LOG_FILE_NAME);

		@SuppressWarnings({ "deprecation", "unused" })
		public void hasEntry(Matcher<String> matcher) throws IOException {
			assertThat(FileUtils.readFileToString(logFile), matcher);
		}

		@SuppressWarnings("unused")
		public void clearLog() {
			LogManager.getLogManager().reset();
		}
	}

}

package auction.sniper.endtoend;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@DisplayName("Auction Sniper Application End to End Test Case")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuctionSniperEndToEndTest {

	private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
	private final ApplicationRunner application = new ApplicationRunner();

	@Test
	@Order(1)
	void sniperJoinsAuctionUntilAuctionCloses() throws Exception {

		// Step 1
		auction.startSellingItem();

		// Step 2
		application.startBiddingIn(auction);

		// Step 3
		auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

		// Step 4
		auction.announceClosed();

		// Step 5
		application.showsSniperHasLostAuction(0,0);

	}

	@Test
	@Order(2)
	void sniperMakesAHigherBidButLoses() throws Exception {

		// Step 1
		auction.startSellingItem();

		// Step 2
		application.startBiddingIn(auction);
		auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

		// Step 3
		auction.reportPrice(1000, 98, "other bidder");
		application.hasShownSniperIsBidding(1000,1098);

		// Step 4
		auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

		// Step 5
		auction.announceClosed();
		application.showsSniperHasLostAuction(1000,1098);
	}

	@Test
	@Order(3)
	void sniperWinsAnAuctionByBiddingHigher() throws Exception {

		// Step 1
		auction.startSellingItem();

		// Step2
		application.startBiddingIn(auction);
		auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

		// Step 3
		auction.reportPrice(1000, 98, "other bidder");
		application.hasShownSniperIsBidding(1000,1098); // Last Price, Last Bid 

		// Step 4
		auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

		// Step 5
		auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
		application.hasShownSniperIsWinning(1098);

		// Step 6
		auction.announceClosed();
		application.showsSniperHasWonAuction(1098);
	}

	// Additional cleanup
	@AfterEach
	void stopAuction() {
		auction.stop();
	}

	@AfterEach
	void stopApplication() {
		application.stop();
	}

}

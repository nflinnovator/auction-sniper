package auction.sniper.endtoend;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Auction Sniper Application End to End Test Case")
class AuctionSniperEndToEndTest {

	private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
	private final ApplicationRunner application = new ApplicationRunner();

	@Test
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
		application.showsSniperHasLostAuction();

	}

	@Test
	void sniperMakesAHigherBidButLoses() throws Exception {
		
		// Step 1
		auction.startSellingItem();
		
		// Step 2
		application.startBiddingIn(auction);
		auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
		
		// Step 3
		auction.reportPrice(1000, 98, "other bidder");
		application.hasShownSniperIsBidding();
		
		// Step 4
		auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);
		
		// Step 5
		auction.announceClosed();
		application.showsSniperHasLostAuction();
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

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
	private final FakeAuctionServer auction2 = new FakeAuctionServer("item-65432");
	
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
		application.showsSniperHasLostAuction(auction,0, 0);

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
		application.hasShownSniperIsBidding(auction,1000, 1098);

		// Step 4
		auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

		// Step 5
		auction.announceClosed();
		application.showsSniperHasLostAuction(auction,1000, 1098);
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
		application.hasShownSniperIsBidding(auction,1000, 1098); // Last Price, Last Bid

		// Step 4
		auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

		// Step 5
		auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
		application.hasShownSniperIsWinning(auction,1098);

		// Step 6
		auction.announceClosed();
		application.showsSniperHasWonAuction(auction,1098);
	}

	@Test
	@Order(4)
	void sniperBidsForMultipleItems() throws Exception {
		
		// Step 1
		auction.startSellingItem();
		auction2.startSellingItem();
		
		// Step 2
		application.startBiddingIn(auction, auction2);
		auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
		auction2.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
		
		// Step 3
		auction.reportPrice(1000, 98, "other bidder");
		auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);
		auction2.reportPrice(500, 21, "other bidder");
		auction2.hasReceivedBid(521, ApplicationRunner.SNIPER_XMPP_ID);
		
		// Step 4
		auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
		auction2.reportPrice(521, 22, ApplicationRunner.SNIPER_XMPP_ID);
		application.hasShownSniperIsWinning(auction, 1098);
		application.hasShownSniperIsWinning(auction2, 521);
		
		// Step 5
		auction.announceClosed();
		auction2.announceClosed();
		application.showsSniperHasWonAuction(auction, 1098);
		application.showsSniperHasWonAuction(auction2, 521);
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

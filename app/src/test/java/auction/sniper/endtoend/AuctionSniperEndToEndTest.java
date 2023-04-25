package auction.sniper.endtoend;

import static auction.sniper.endtoend.ApplicationRunner.SNIPER_XMPP_ID;

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
	@DisplayName("A sniper joins an auction until the auction closes")
	void sniperJoinsAuctionUntilAuctionCloses() throws Exception {

		// Step 1
		auction.startSellingItem();

		// Step 2
		application.startBiddingIn(auction);

		// Step 3
		auction.hasReceivedJoinRequestFromSniper(SNIPER_XMPP_ID);

		// Step 4
		auction.announceClosed();

		// Step 5
		application.showsSniperHasLostAuction(auction, 0, 0);

	}

	@Test
	@Order(2)
	@DisplayName("A sniper makes a higher bid but loses")
	void sniperMakesAHigherBidButLoses() throws Exception {

		// Step 1
		auction.startSellingItem();

		// Step 2
		application.startBiddingIn(auction);
		auction.hasReceivedJoinRequestFromSniper(SNIPER_XMPP_ID);

		// Step 3
		auction.reportPrice(1000, 98, "other bidder");
		application.hasShownSniperIsBidding(auction, 1000, 1098);

		// Step 4
		auction.hasReceivedBid(1098, SNIPER_XMPP_ID);

		// Step 5
		auction.announceClosed();
		application.showsSniperHasLostAuction(auction, 1000, 1098);
	}

	@Test
	@Order(3)
	@DisplayName("A sniper wins an auction by bidding higher")
	void sniperWinsAnAuctionByBiddingHigher() throws Exception {

		// Step 1
		auction.startSellingItem();

		// Step2
		application.startBiddingIn(auction);
		auction.hasReceivedJoinRequestFromSniper(SNIPER_XMPP_ID);

		// Step 3
		auction.reportPrice(1000, 98, "other bidder");
		application.hasShownSniperIsBidding(auction, 1000, 1098); // Last Price, Last Bid

		// Step 4
		auction.hasReceivedBid(1098, SNIPER_XMPP_ID);

		// Step 5
		auction.reportPrice(1098, 97, SNIPER_XMPP_ID);
		application.hasShownSniperIsWinning(auction, 1098);

		// Step 6
		auction.announceClosed();
		application.showsSniperHasWonAuction(auction, 1098);
	}

	@Test
	@Order(4)
	@DisplayName("A sniper bids for multiple items")
	void sniperBidsForMultipleItems() throws Exception {

		// Step 1
		auction.startSellingItem();
		auction2.startSellingItem();

		// Step 2
		application.startBiddingIn(auction, auction2);
		auction.hasReceivedJoinRequestFromSniper(SNIPER_XMPP_ID);
		auction2.hasReceivedJoinRequestFromSniper(SNIPER_XMPP_ID);

		// Step 3
		auction.reportPrice(1000, 98, "other bidder");
		auction.hasReceivedBid(1098, SNIPER_XMPP_ID);
		auction2.reportPrice(500, 21, "other bidder");
		auction2.hasReceivedBid(521, SNIPER_XMPP_ID);

		// Step 4
		auction.reportPrice(1098, 97, SNIPER_XMPP_ID);
		auction2.reportPrice(521, 22, SNIPER_XMPP_ID);
		application.hasShownSniperIsWinning(auction, 1098);
		application.hasShownSniperIsWinning(auction2, 521);

		// Step 5
		auction.announceClosed();
		auction2.announceClosed();
		application.showsSniperHasWonAuction(auction, 1098);
		application.showsSniperHasWonAuction(auction2, 521);
	}

	@Test
	@Order(5)
	@DisplayName("A sniper loses an auction when the price is too high")
	void sniperLosesAnAuctionWhenThePriceIsTooHigh() throws Exception {

		// Step 1
		auction.startSellingItem();
		application.startBiddingWithStopPrice(auction, 1100);
		auction.hasReceivedJoinRequestFromSniper(SNIPER_XMPP_ID);
		auction.reportPrice(1000, 98, "other bidder");
		application.hasShownSniperIsBidding(auction, 1000, 1098);

		// Step 2
		auction.hasReceivedBid(1098, SNIPER_XMPP_ID);

		// Step 3
		auction.reportPrice(1197, 10, "third party");
		application.hasShownSniperIsLosing(auction, 1197, 1098);

		// Step 4
		auction.reportPrice(1207, 10, "fourth party");
		application.hasShownSniperIsLosing(auction, 1207, 1098);

		// Step 5
		auction.announceClosed();
		application.showsSniperHasLostAuction(auction, 1207, 1098);
	}

	@Test
	@Order(6)
	@DisplayName("A sniper reports invalid auction message and stops responding to events")
	void sniperReportsInvalidAuctionMessageAndStopsRespondingToEvents() throws Exception {
		String brokenMessage = "a broken message";

		auction.startSellingItem();
		auction2.startSellingItem();

		application.startBiddingIn(auction, auction2);
		auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);

		auction.reportPrice(500, 20, "other bidder");
		auction.hasReceivedBid(520, ApplicationRunner.SNIPER_XMPP_ID);

		auction.sendInvalidMessageContaining(brokenMessage);
		application.showsSniperHasFailed(auction);

		auction.reportPrice(520, 21, "other bidder");
		waitForAnotherAuctionEvent();

		application.reportsInvalidMessage(auction, brokenMessage);
		application.showsSniperHasFailed(auction);

	}

	private void waitForAnotherAuctionEvent() throws Exception {
		auction2.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		auction2.reportPrice(600, 6, "other bidder");
		application.hasShownSniperIsBidding(auction2, 600, 606);
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

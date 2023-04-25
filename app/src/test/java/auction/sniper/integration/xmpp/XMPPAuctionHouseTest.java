package auction.sniper.integration.xmpp;

import static auction.sniper.endtoend.ApplicationRunner.SNIPER_ID;
import static auction.sniper.endtoend.ApplicationRunner.SNIPER_PASSWORD;
import static auction.sniper.endtoend.ApplicationRunner.SNIPER_XMPP_ID;
import static auction.sniper.endtoend.FakeAuctionServer.XMPP_HOSTNAME;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.jivesoftware.smack.XMPPException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import auction.sniper.adapters.xmpp.XMPPAuctionHouse;
import auction.sniper.core.AuctionEventListener;
import auction.sniper.core.Item;
import auction.sniper.endtoend.FakeAuctionServer;
import auction.sniper.xmpp.XMPPAuctionException;

@DisplayName("XMPP Auction House Integration Test Case")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class XMPPAuctionHouseTest {

	private final FakeAuctionServer auctionServer = new FakeAuctionServer("item-54321");
	private XMPPAuctionHouse auctionHouse;

	@Test
	@Order(1)
	void receivesEventsFromAuctionServerAfterJoining() throws Exception {

		CountDownLatch auctionWasClosed = new CountDownLatch(1);

		final var auction = auctionHouse.auctionFor(new Item(auctionServer.getItemId(), 567));

		auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed));

		auction.join();
		auctionServer.hasReceivedJoinRequestFromSniper(SNIPER_XMPP_ID);
		auctionServer.announceClosed();

		assertTrue(auctionWasClosed.await(2, TimeUnit.SECONDS));
	}

	private AuctionEventListener auctionClosedListener(CountDownLatch auctionWasClosed) {
		return new AuctionEventListener() {

			@Override
			public void auctionClosed() {
				auctionWasClosed.countDown();
			}

			@Override
			public void currentPrice(int price, int increment, PriceSource priceSource) {
				// no op
			}

			@Override
			public void auctionFailed() {

			}
		};
	}

	@BeforeEach
	void createConnection() throws XMPPException, XMPPAuctionException {
		auctionHouse = XMPPAuctionHouse.connect(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD);
	}

	@BeforeEach
	void startTheAuction() throws XMPPException {
		auctionServer.startSellingItem();
	}

	@AfterEach
	void closeConnection() {
		if (auctionHouse != null) {
			auctionHouse.disconnect();
		}
	}

	@AfterEach
	void stopAuction() {
		auctionServer.stop();
	}

}

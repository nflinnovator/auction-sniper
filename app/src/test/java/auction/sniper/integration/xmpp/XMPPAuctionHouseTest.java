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
import org.junit.jupiter.api.Test;

import auction.sniper.adapters.xmpp.XMPPAuctionHouse;
import auction.sniper.core.AuctionEventListener;
import auction.sniper.endtoend.FakeAuctionServer;

@DisplayName("XMPP Auction House Integration Test Case")
class XMPPAuctionHouseTest {

	private final FakeAuctionServer auctionServer = new FakeAuctionServer("item-54321");
	private XMPPAuctionHouse auctionHouse;

	@Test
	void receivesEventsFromAuctionServerAfterJoining() throws Exception {
		CountDownLatch auctionWasClosed = new CountDownLatch(1);

		final var auction = auctionHouse.auctionFor(auctionServer.getItemId());
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
		};
	}

	@BeforeEach
	void createConnection() throws XMPPException {
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

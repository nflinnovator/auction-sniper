package auction.sniper.unit;

import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;

import auction.sniper.Auction;
import auction.sniper.AuctionSniper;
import auction.sniper.SniperListener;

class AuctionSniperTest {

	private final JUnit5Mockery context = new JUnit5Mockery();
	private final SniperListener sniperListener = context.mock(SniperListener.class);
	private final Auction auction = context.mock(Auction.class);
	private final AuctionSniper sniper = new AuctionSniper(auction, sniperListener);

	@Test
	void reportsLostWhenAuctionCloses() {
		context.checking(new Expectations() {
			{
				atLeast(1).of(sniperListener).sniperLost();
			}
		});
		sniper.auctionClosed();
	}

	@Test
	void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
		final var price = 1001;
		final var increment = 25;
		context.checking(new Expectations() {
			{
				oneOf(auction).bid(price + increment);
				atLeast(1).of(sniperListener).sniperBidding();
			}
		});
		sniper.currentPrice(price, increment);
	}

}

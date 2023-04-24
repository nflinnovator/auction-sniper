package auction.sniper.unit;

import static auction.sniper.core.SniperSnapshot.SniperState.BIDDING;
import static auction.sniper.core.SniperSnapshot.SniperState.LOST;
import static auction.sniper.core.SniperSnapshot.SniperState.WINNING;
import static auction.sniper.core.SniperSnapshot.SniperState.WON;
import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import auction.sniper.core.Auction;
import auction.sniper.core.AuctionSniper;
import auction.sniper.core.SniperListener;
import auction.sniper.core.SniperSnapshot;
import auction.sniper.core.AuctionEventListener.PriceSource;
import auction.sniper.core.SniperSnapshot.SniperState;

@DisplayName("Auction Sniper Unit Test Case")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuctionSniperTest {

	static final String ITEM_ID = "test item id";

	private final Mockery context = new Mockery();
	private final States sniperState = context.states("sniper");
	private final SniperListener sniperListener = context.mock(SniperListener.class);
	private final Auction auction = context.mock(Auction.class);
	private final AuctionSniper sniper = new AuctionSniper(ITEM_ID, auction);
	
    @BeforeEach
    void addAuctionSniperListener() {
        sniper.addSniperListener(sniperListener);
    }

	@Test
	@Order(1)
	void reportsLostIfAuctionClosesImmediately() {
		context.checking(new Expectations() {
			{
				atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThat(LOST)));
			}
		});
		sniper.auctionClosed();
	}

	@Test
	@Order(2)
	public void reportsLostIfAuctionClosesWhenBidding() {
		context.checking(new Expectations() {
			{
				ignoring(auction);
				allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(BIDDING)));
				then(sniperState.is("bidding"));
				atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThat(LOST)));
				when(sniperState.is("bidding"));
			}
		});
		sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
		sniper.auctionClosed();
	}

	@Test
	@Order(3)
	void reportsIsWinningWhenCurrentPriceComesFromSniper() {
		context.checking(new Expectations() {
			{
				ignoring(auction);
				allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.BIDDING)));
				then(sniperState.is("bidding"));

				atLeast(1).of(sniperListener)
						.sniperStateChanged(new SniperSnapshot(ITEM_ID, 135, 135, SniperState.WINNING));
				when(sniperState.is("bidding"));

			}
		});
		sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);
		sniper.currentPrice(123, 45, PriceSource.FromSniper);
	}

	@Test
	@Order(4)
	void bidsHigherAndReportsBiddingWhenNewPriceArrivesAndItIsFromAnotherSniper() {
		final var price = 1001;
		final var increment = 25;
		final var bid = price + increment;

		context.checking(new Expectations() {
			{
				oneOf(auction).bid(bid);
				atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, price, bid, BIDDING));
			}
		});
		sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
	}

	@Test
	@Order(5)
	void bidsHigherAndReportsWinningWhenNewPriceArrivesAndItIsFromSniper() {
		final var price = 1001;
		final var increment = 25;
		final var bid = price + increment;

		context.checking(new Expectations() {
			{
				oneOf(auction).bid(bid);
				atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, price, bid, WINNING));
			}
		});
		sniper.currentPrice(price, increment, PriceSource.FromSniper);
	}

	@Test
	@Order(6)
	void reportsWonIfAuctionClosesWhenWinning() {
		context.checking(new Expectations() {
			{
				ignoring(auction);
				then(sniperState.is("winning"));

				atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThat(WON)));
				when(sniperState.is("winning"));
			}
		});

		sniper.currentPrice(123, 45, PriceSource.FromSniper);
		sniper.auctionClosed();
	}

	private Matcher<SniperSnapshot> aSniperThatIs(final SniperState state) {
		return new FeatureMatcher<SniperSnapshot, SniperState>(equalTo(state), "sniper that is ", "was") {
			@Override
			protected SniperState featureValueOf(SniperSnapshot actual) {
				return actual.state;
			}
		};
	}
	
	private Matcher<SniperSnapshot> aSniperThat(final SniperState state) {
		return new FeatureMatcher<SniperSnapshot, SniperState>(equalTo(state), "sniper that is ", "was") {
			@Override
			protected SniperState featureValueOf(SniperSnapshot actual) {
				return actual.state;
			}
		};
	}

}

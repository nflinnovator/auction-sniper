package auction.sniper.unit;

import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import auction.sniper.core.Auction;
import auction.sniper.core.AuctionHouse;
import auction.sniper.core.AuctionSniper;
import auction.sniper.core.SniperCollector;
import auction.sniper.core.SniperLauncher;

@DisplayName("Sniper Launcher Unit Test Case")
class SniperLauncherTest {

	private final Mockery context = new Mockery();
	private final Auction auction = context.mock(Auction.class);
	private final AuctionHouse auctionHouse = context.mock(AuctionHouse.class);
	private final SniperCollector sniperCollector = context.mock(SniperCollector.class);
	private final SniperLauncher launcher = new SniperLauncher(auctionHouse, sniperCollector);
	private final States auctionState = context.states("auction state").startsAs("not joined");

	@Test
	void addsNewSniperToCollectorAndThenJoinsAuction() {
		final String itemId = "item 123";
		context.checking(new Expectations() {
			{
				allowing(auctionHouse).auctionFor(itemId);
				will(returnValue(auction));

				oneOf(auction).addAuctionEventListener(with(sniperForItem(itemId)));
				when(auctionState.is("not joined"));

				oneOf(sniperCollector).addSniper(with(sniperForItem(itemId)));
				when(auctionState.is("not joined"));

				oneOf(auction).join();
				then(auctionState.is("joined"));
			}
		});

		launcher.joinAuction(itemId);
	}

	private Matcher<AuctionSniper> sniperForItem(String itemId) {
		return new FeatureMatcher<AuctionSniper, String>(equalTo(itemId), "sniper with itemId id", "itemId") {
			@Override
			protected String featureValueOf(AuctionSniper actual) {
				return actual.getSnapshot().itemId;
			}
		};
	}

}

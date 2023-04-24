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
import auction.sniper.core.Item;
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
		final var item = new Item("item 123", 456);
		context.checking(new Expectations() {
			{
				allowing(auctionHouse).auctionFor(item);
				will(returnValue(auction));

				oneOf(auction).addAuctionEventListener(with(sniperForItem(item)));
				when(auctionState.is("not joined"));

				oneOf(sniperCollector).addSniper(with(sniperForItem(item)));
				when(auctionState.is("not joined"));

				oneOf(auction).join();
				then(auctionState.is("joined"));
			}
		});

		launcher.joinAuction(item);
	}

	private Matcher<AuctionSniper> sniperForItem(Item item) {
		return new FeatureMatcher<AuctionSniper, String>(equalTo(item.identifier), "sniper with itemId id", "itemId") {
			@Override
			protected String featureValueOf(AuctionSniper actual) {
				return actual.getSnapshot().itemId;
			}
		};
	}

}

package auction.sniper.core;

public class SniperLauncher implements UserRequestListener {

	private final AuctionHouse auctionHouse;
	private final SniperCollector collector;

	public SniperLauncher(AuctionHouse auctionHouse, SniperCollector collector) {
		this.auctionHouse = auctionHouse;
		this.collector = collector;
	}

	@Override
	public void joinAuction(Item item) {
		final var auction = auctionHouse.auctionFor(item);
		final var sniper = new AuctionSniper(item, auction);
		auction.addAuctionEventListener(sniper);
		collector.addSniper(sniper);
		auction.join();
	}

}

package auction.sniper.core;

public class SniperLauncher implements UserRequestListener {

	private final AuctionHouse auctionHouse;
	private final SniperCollector collector;

	public SniperLauncher(AuctionHouse auctionHouse, SniperCollector collector) {
		this.auctionHouse = auctionHouse;
		this.collector = collector;
	}

	@Override
	public void joinAuction(String itemId) {
		final var auction = auctionHouse.auctionFor(itemId);
		final var sniper = new AuctionSniper(itemId, auction);
		auction.addAuctionEventListener(sniper);
		collector.addSniper(sniper);
		auction.join();
	}

}

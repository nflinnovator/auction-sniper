package auction.sniper.core;

import auction.sniper.shared.Announcer;

public class AuctionSniper implements AuctionEventListener {

	 private final Item item;
	private final Auction auction;
	private SniperSnapshot snapshot;

	private final Announcer<SniperListener> listeners = Announcer.to(SniperListener.class);

	public AuctionSniper(Item item, Auction auction) {
		this.item = item;
        this.auction = auction;
        this.snapshot = SniperSnapshot.joining(item.identifier);
	}

	@Override
	public void auctionClosed() {
		snapshot = snapshot.closed();
		notifyChange();
	}

	public void currentPrice(int price, int increment, PriceSource priceSource) {
		switch (priceSource) {
		case FromSniper:
			snapshot = snapshot.winning(price);
			break;
		case FromOtherBidder:
			int bid = price + increment;
			if (item.allowsBid(bid)) {
				auction.bid(bid);
				snapshot = snapshot.bidding(price, bid);
			} else {
				snapshot = snapshot.losing(price);
			}
			break;
		}
		notifyChange();
	}

	public void addSniperListener(SniperListener listener) {
		this.listeners.addListener(listener);
	}

	private void notifyChange() {
		listeners.announce().sniperStateChanged(snapshot);
	}

	public SniperSnapshot getSnapshot() {
		return snapshot;
	}

}

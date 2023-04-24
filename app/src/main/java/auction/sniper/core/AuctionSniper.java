package auction.sniper.core;

import auction.sniper.shared.Announcer;

public class AuctionSniper implements AuctionEventListener {

	private final Auction auction;
	private SniperSnapshot snapshot;

	private final Announcer<SniperListener> listeners = Announcer.to(SniperListener.class);
	
	public AuctionSniper(String itemId, Auction auction) {
        this.auction = auction;
        this.snapshot = SniperSnapshot.joining(itemId);
    }


	@Override
	public void auctionClosed() {
		snapshot = snapshot.closed();
		notifyChange();
	}

	@Override
	public void currentPrice(int price, int increment, PriceSource priceSource) {
		switch (priceSource) {
		case FromSniper:
			snapshot = snapshot.winning(price);
			break;
		case FromOtherBidder:
			final var bid = price + increment;
			auction.bid(bid);
			snapshot = snapshot.bidding(price, bid);
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

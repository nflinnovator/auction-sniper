package auction.sniper.core;

public interface Auction {
	void bid(int amount);

	void join();

	void addAuctionEventListener(AuctionEventListener auctionEventListener);
}

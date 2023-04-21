package auction.sniper;

import java.util.EventListener;

public interface SniperListener extends EventListener {
	void sniperLost();
	void sniperBidding();
	void sniperWinning();
}
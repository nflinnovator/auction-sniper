package auction.sniper;

import java.util.EventListener;

public interface SniperListener extends EventListener {
	void sniperStateChanged(SniperSnapshot sniperSnapshot);
}

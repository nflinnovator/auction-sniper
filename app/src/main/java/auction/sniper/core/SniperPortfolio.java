package auction.sniper.core;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import auction.sniper.shared.Announcer;

public class SniperPortfolio implements SniperCollector {

	public interface PortfolioListener extends EventListener {
		void sniperAdded(AuctionSniper sniper);
	}

	private final List<AuctionSniper> snipers = new ArrayList<>();
	private final Announcer<PortfolioListener> announcer = Announcer.to(PortfolioListener.class);

	@Override
	public void addSniper(AuctionSniper sniper) {
		snipers.add(sniper);
		announcer.announce().sniperAdded(sniper);
	}

	public void addPortfolioListener(PortfolioListener listener) {
		announcer.addListener(listener);
	}
}

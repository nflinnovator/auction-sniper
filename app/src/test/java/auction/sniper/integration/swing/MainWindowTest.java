package auction.sniper.integration.swing;

import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.objogate.wl.swing.probe.ValueMatcherProbe;

import auction.sniper.core.Item;
import auction.sniper.core.SniperPortfolio;
import auction.sniper.endtoend.AuctionSniperDriver;
import auction.sniper.ui.MainWindow;

@DisplayName("Main Window Integration Test Case")
class MainWindowTest {

	private final SniperPortfolio portfolio = new SniperPortfolio();
	private final MainWindow mainWindow = new MainWindow(portfolio);
	private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

	@Test
	void makesUserRequestWhenJoinButtonClicked() {
		final var itemProbe = new ValueMatcherProbe<>(equalTo(new Item("item-id", 789)), "item request");

		mainWindow.addUserRequestListener(itemProbe::setReceivedValue);

		driver.startBiddingFor("item-id", 789);
		driver.check(itemProbe);
	}

}

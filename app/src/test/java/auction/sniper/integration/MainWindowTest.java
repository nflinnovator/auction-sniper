package auction.sniper.integration;

import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.objogate.wl.swing.probe.ValueMatcherProbe;

import auction.sniper.endtoend.AuctionSniperDriver;
import auction.sniper.ui.MainWindow;
import auction.sniper.ui.SnipersTableModel;
import auction.sniper.ui.UserRequestListener;

@DisplayName("Main Window Integration Test Case")
class MainWindowTest {

	private final SnipersTableModel tableModel = new SnipersTableModel();
	private final MainWindow mainWindow = new MainWindow(tableModel);
	private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

	@Test
	void makesUserRequestWhenJoinButtonClicked() {
		final var buttonProbe = new ValueMatcherProbe<>(equalTo("item-id"), "join request");
		mainWindow.addUserRequestListener(new UserRequestListener() {
			public void joinAuction(String itemId) {
				buttonProbe.setReceivedValue(itemId);
			}
		});
		driver.startBiddingFor("item-id");
		driver.check(buttonProbe);
	}

}

package auction.sniper.ui;

import static auction.sniper.App.MAIN_WINDOW_NAME;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import auction.sniper.adapters.ui.SnipersTableModel;
import auction.sniper.core.Item;
import auction.sniper.core.SniperPortfolio;
import auction.sniper.core.UserRequestListener;
import auction.sniper.shared.Announcer;

public class MainWindow extends JFrame {

	public static final String APPLICATION_TITLE = "Auction Sniper";
	public static final String NEW_ITEM_ID_NAME = "new item id";
	public static final String JOIN_BUTTON_NAME = "join button";
	public static final String NEW_ITEM_STOP_PRICE_NAME = "stop price";

	private static final long serialVersionUID = 1L;
	private static final String SNIPERS_TABLE_NAME = "Snipers Table";

	private final Announcer<UserRequestListener> userRequests = Announcer.to(UserRequestListener.class);

	private JTextField itemIdField;

	private JFormattedTextField stopPriceField;

	public MainWindow(SniperPortfolio portfolio) {
		super(APPLICATION_TITLE);
		setName(MAIN_WINDOW_NAME);
		fillContentPane(makeSnipersTable(portfolio), makeControls());
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void addUserRequestListener(UserRequestListener userRequestListener) {
		userRequests.addListener(userRequestListener);
	}

	private void fillContentPane(JTable snipersTable, JPanel controlsPanel) {
		final var contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(controlsPanel, BorderLayout.NORTH);
		contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
	}

	private JTable makeSnipersTable(SniperPortfolio portfolio) {
		final var tableModel = new SnipersTableModel();
		portfolio.addPortfolioListener(tableModel);
		final var snipersTable = new JTable(tableModel);
		snipersTable.setName(SNIPERS_TABLE_NAME);
		return snipersTable;
	}

	private JPanel makeControls() {
		final var controls = new JPanel(new FlowLayout());
		itemIdField = itemIdField();
		stopPriceField = stopPriceField();
		controls.add(itemIdField);
		controls.add(stopPriceField);

		JButton joinAuctionButton = new JButton("Join Auction");
		joinAuctionButton.setName(JOIN_BUTTON_NAME);
		joinAuctionButton.addActionListener(e -> userRequests.announce().joinAuction(new Item(itemId(), stopPrice())));
		controls.add(joinAuctionButton);

		return controls;
	}

	private int stopPrice() {
		return ((Number) stopPriceField.getValue()).intValue();
	}

	private String itemId() {
		return itemIdField.getText();
	}

	private JTextField itemIdField() {
		final var itemIdField = new JTextField();
		itemIdField.setName(NEW_ITEM_ID_NAME);
		itemIdField.setColumns(10);
		return itemIdField;
	}

	private JFormattedTextField stopPriceField() {
		final var stopPriceField = new JFormattedTextField(NumberFormat.getIntegerInstance());
		stopPriceField.setColumns(7);
		stopPriceField.setName(NEW_ITEM_STOP_PRICE_NAME);
		return stopPriceField;
	}

}

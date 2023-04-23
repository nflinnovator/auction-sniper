package auction.sniper.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import static auction.sniper.App.MAIN_WINDOW_NAME;

public class MainWindow extends JFrame {

	public static final String APPLICATION_TITLE = "Auction Sniper";
	public static final String NEW_ITEM_ID_NAME = "new item id";
	public static final String JOIN_BUTTON_NAME = "join button";

	private static final long serialVersionUID = 1L;
	private static final String SNIPERS_TABLE_NAME = "Snipers Table";

	private final SnipersTableModel snipers;

	private final Announcer<UserRequestListener> userRequests = Announcer.to(UserRequestListener.class);

	public MainWindow(SnipersTableModel snipers) {
		super(APPLICATION_TITLE);
		this.snipers = snipers;
		setName(MAIN_WINDOW_NAME);
		fillContentPane(makeSnipersTable(), makeControls());
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

	private JTable makeSnipersTable() {
		final JTable snipersTable = new JTable(snipers);
		snipersTable.setName(SNIPERS_TABLE_NAME);
		return snipersTable;
	}

	private JPanel makeControls() {
		var controls = new JPanel(new FlowLayout());
		var itemIdField = new JTextField();
		itemIdField.setColumns(25);
		itemIdField.setName(NEW_ITEM_ID_NAME);
		controls.add(itemIdField);
		JButton joinAuctionButton = new JButton("Join Auction");
		joinAuctionButton.setName(JOIN_BUTTON_NAME);
		joinAuctionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				userRequests.announce().joinAuction(itemIdField.getText());
			}
		});
		controls.add(joinAuctionButton);
		return controls;
	}

}

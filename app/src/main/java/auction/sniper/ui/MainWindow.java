package auction.sniper.ui;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import static auction.sniper.App.MAIN_WINDOW_NAME;

public class MainWindow extends JFrame {

	public static final String APPLICATION_TITLE = "Auction Sniper";
	
	private static final long serialVersionUID = 1L;
	private static final String SNIPERS_TABLE_NAME = "Snipers Table";

	private final SnipersTableModel snipers;

	public MainWindow(SnipersTableModel snipers) {
		super(APPLICATION_TITLE);
		this.snipers = snipers;
		setName(MAIN_WINDOW_NAME);
		fillContentPane(makeSnipersTable());
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true); 
	}
	
	private void fillContentPane(JTable snipersTable) {
		final var contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
	}

	private JTable makeSnipersTable() {
		final JTable snipersTable = new JTable(snipers);
		snipersTable.setName(SNIPERS_TABLE_NAME);
		return snipersTable;
	}

}

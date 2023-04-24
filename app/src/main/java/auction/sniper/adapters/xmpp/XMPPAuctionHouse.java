package auction.sniper.adapters.xmpp;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auction.sniper.core.Auction;
import auction.sniper.core.AuctionHouse;
import auction.sniper.core.Item;
import auction.sniper.xmpp.XMPPAuction;

public class XMPPAuctionHouse implements AuctionHouse {

	private static String ITEM_ID_AS_LOGIN = "auction-%s";
	private static String AUCTION_RESOURCE = "Auction";
	public static String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

	private final XMPPConnection connection;

	public XMPPAuctionHouse(XMPPConnection connection) {
		this.connection = connection;
	}

	@Override
	public Auction auctionFor(Item item) {
		return new XMPPAuction(connection, auctionId(item.identifier, connection));
	}

	public static XMPPAuctionHouse connect(String hostname, String username, String password) throws XMPPException {
		XMPPConnection connection = new XMPPConnection(hostname);
		connection.connect();
		connection.login(username, password, AUCTION_RESOURCE);
		return new XMPPAuctionHouse(connection);
	}

	public void disconnect() {
		connection.disconnect();
	}

	private static String auctionId(String itemId, XMPPConnection connection) {
		return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
	}

}

package auction.sniper.adapters.xmpp;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.io.FilenameUtils;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auction.sniper.core.Auction;
import auction.sniper.core.AuctionHouse;
import auction.sniper.core.Item;
import auction.sniper.xmpp.LoggingXMPPFailureReporter;
import auction.sniper.xmpp.XMPPAuction;
import auction.sniper.xmpp.XMPPAuctionException;
import auction.sniper.xmpp.XMPPFailureReporter;

public class XMPPAuctionHouse implements AuctionHouse {

	private static String ITEM_ID_AS_LOGIN = "auction-%s";
	private static String AUCTION_RESOURCE = "Auction";
	public static String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

	private static final String LOGGER_NAME = "auction-sniper";
	public static final String LOG_FILE_NAME = "auction-sniper.log";

	private final XMPPConnection connection;
	private final XMPPFailureReporter failureReporter;

	public XMPPAuctionHouse(XMPPConnection connection) throws XMPPAuctionException {
		this.connection = connection;
		this.failureReporter = new LoggingXMPPFailureReporter(makeLogger());
	}

	@Override
	public Auction auctionFor(Item item) {
		return new XMPPAuction(connection, auctionId(item.identifier, connection), failureReporter);
	}

	public static XMPPAuctionHouse connect(String hostname, String username, String password)
			throws XMPPException, XMPPAuctionException {
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

	private Logger makeLogger() throws XMPPAuctionException {
		Logger logger = Logger.getLogger(LOGGER_NAME);
		logger.setUseParentHandlers(false);
		logger.addHandler(simpleFileHandler());
		return logger;
	}

	private FileHandler simpleFileHandler() throws XMPPAuctionException {
		try {
			FileHandler handler = new FileHandler(LOG_FILE_NAME);
			handler.setFormatter(new SimpleFormatter());
			return handler;
		} catch (Exception e) {
			throw new XMPPAuctionException(
					"Could not create logger FileHandler " + FilenameUtils.getFullPath(LOG_FILE_NAME), e);
		}
	}

}

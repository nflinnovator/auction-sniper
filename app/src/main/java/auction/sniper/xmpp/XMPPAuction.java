package auction.sniper.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auction.sniper.core.Auction;
import auction.sniper.core.AuctionEventListener;
import auction.sniper.shared.Announcer;

public final class XMPPAuction implements Auction {

	public static String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
	public static String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";

	private final Chat chat;
	private final XMPPFailureReporter failureReporter;

	private final Announcer<AuctionEventListener> auctionEventListeners = Announcer.to(AuctionEventListener.class);

	public XMPPAuction(XMPPConnection connection, String auctionId, XMPPFailureReporter failureReporter) {
		this.failureReporter = failureReporter;
		AuctionMessageTranslator translator = translatorFor(connection);
		this.chat = connection.getChatManager().createChat(auctionId, translator);
		addAuctionEventListener(chatDisconnectorFor(translator));
	}

	@Override
	public void bid(int amount) {
		sendMessage(String.format(BID_COMMAND_FORMAT, amount));
	}

	@Override
	public void join() {
		sendMessage(JOIN_COMMAND_FORMAT);
	}

	@Override
	public void addAuctionEventListener(AuctionEventListener listener) {
		auctionEventListeners.addListener(listener);
	}

	private AuctionMessageTranslator translatorFor(XMPPConnection connection) {
		return new AuctionMessageTranslator(connection.getUser(), auctionEventListeners.announce(), failureReporter);
	}

	private AuctionEventListener chatDisconnectorFor(AuctionMessageTranslator translator) {
		return new AuctionEventListener() {
			@Override
			public void auctionClosed() {
				// no op
			}

			@Override
			public void currentPrice(int price, int increment, PriceSource priceSource) {
				// no op
			}

			@Override
			public void auctionFailed() {
				chat.removeMessageListener(translator);
			}
		};
	}

	private void sendMessage(final String message) {
		try {
			chat.sendMessage(message);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

}

package auction.sniper.endtoend;

import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import auction.sniper.App;

class FakeAuctionServer {

	static final String ITEM_ID_AS_LOGIN = "auction-%s";
	static final String AUCTION_RESOURCE = "Auction";
	static final String XMPP_HOSTNAME = "localhost";
	private static final String AUCTION_PASSWORD = "auction";

	private final String itemId;
	private final XMPPConnection connection;
	private Chat currentChat;

	private final SingleMessageListener messageListener = new SingleMessageListener();

	FakeAuctionServer(String itemId) {
		this.itemId = itemId;
		this.connection = new XMPPConnection(XMPP_HOSTNAME);
	}

	void startSellingItem() throws XMPPException {
		connection.connect();
		connection.login(String.format(ITEM_ID_AS_LOGIN, itemId), AUCTION_PASSWORD, AUCTION_RESOURCE);
		connection.getChatManager().addChatListener(new ChatManagerListener() {
			public void chatCreated(Chat chat, boolean createdLocally) {
				currentChat = chat;
				chat.addMessageListener(messageListener);
			}
		});
	}

	void hasReceivedJoinRequestFrom(String sniperId) throws InterruptedException {
		receivesAMessageMatching(sniperId, equalTo(App.JOIN_COMMAND_FORMAT));
	}

	void reportPrice(int price, int increment, String bidder) throws XMPPException {
		currentChat.sendMessage(
				String.format("SOLVersion: 1.1; Event: PRICE; " + "CurrentPrice: %d; Increment: %d; Bidder: %s;", price,
						increment, bidder));
	}

	void hasReceivedBid(int bid, String sniperId) throws InterruptedException {
		receivesAMessageMatching(sniperId, equalTo(String.format(App.BID_COMMAND_FORMAT, bid)));
	}

	void announceClosed() throws XMPPException {
		currentChat.sendMessage("SOLVersion: 1.1; Event: CLOSE;");
	}

	void stop() {
		connection.disconnect();
	}

	String getItemId() {
		return itemId;
	}

	private void receivesAMessageMatching(String sniperId, Matcher<? super String> messageMatcher)
			throws InterruptedException {
		messageListener.receivesAMessage(messageMatcher);
		assertThat(currentChat.getParticipant(), equalTo(sniperId));
	}

}

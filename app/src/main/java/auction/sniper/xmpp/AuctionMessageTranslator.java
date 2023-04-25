package auction.sniper.xmpp;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import auction.sniper.core.AuctionEventListener;
import auction.sniper.core.AuctionEventListener.PriceSource;

public class AuctionMessageTranslator implements MessageListener {

	private final String sniperId;

	private final AuctionEventListener listener;

	private XMPPFailureReporter failureReporter;

	public AuctionMessageTranslator(String sniperId, AuctionEventListener listener,
			XMPPFailureReporter failureReporter) {
		this.sniperId = sniperId;
		this.listener = listener;
		this.failureReporter = failureReporter;
	}

	@Override
	public void processMessage(Chat chat, Message message) {
		String messageBody = message.getBody();

		try {
			translate(messageBody);
		} catch (Exception e) {
			failureReporter.cannotTranslateMessage(sniperId, messageBody, e);
			listener.auctionFailed();
		}
	}

	private void translate(String messageBody) throws Exception {
		AuctionEvent event = AuctionEvent.from(messageBody);

		String type = event.type();
		if ("CLOSE".equals(type)) {
			listener.auctionClosed();
		} else if ("PRICE".equals(type)) {
			listener.currentPrice(event.currentPrice(), event.increment(), event.isFrom(sniperId));
		}
	}

	private static final class AuctionEvent {
		private final Map<String, String> fields = new HashMap<String, String>();

		public String type() throws MissingValueException {
			return get("Event");
		}

		public int currentPrice() throws MissingValueException {
			return getInt("CurrentPrice");
		}

		public int increment() throws MissingValueException {
			return getInt("Increment");
		}

		private int getInt(String fieldName) {
			return Integer.parseInt(get(fieldName));
		}

		private String get(String fieldName) {
			return fields.get(fieldName);
		}

		private void addField(String field) {
			String[] pair = field.split(":");
			fields.put(pair[0].trim(), pair[1].trim());
		}

		static AuctionEvent from(String messageBody) {
			AuctionEvent event = new AuctionEvent();
			for (String field : fieldsIn(messageBody)) {
				event.addField(field);
			}
			return event;
		}

		static String[] fieldsIn(String messageBody) {
			return messageBody.split(";");
		}

		public PriceSource isFrom(String sniperId) throws MissingValueException {
			return sniperId.equals(bidder()) ? PriceSource.FromSniper : PriceSource.FromOtherBidder;
		}

		private String bidder() throws MissingValueException {
			return get("Bidder");
		}
	}

	private static class MissingValueException extends RuntimeException {

		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unused")
		MissingValueException(String fieldName) {
			super(fieldName);
		}
	}

}

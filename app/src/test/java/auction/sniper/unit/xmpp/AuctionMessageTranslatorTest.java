package auction.sniper.unit.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import auction.sniper.core.AuctionEventListener;
import auction.sniper.core.AuctionEventListener.PriceSource;
import auction.sniper.xmpp.AuctionMessageTranslator;
import auction.sniper.xmpp.XMPPFailureReporter;

import static auction.sniper.endtoend.ApplicationRunner.SNIPER_ID;

@DisplayName("Auction Message Translator Unit Test Case")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuctionMessageTranslatorTest {

	public static final Chat UNUSED_CHAT = null;

	private final Mockery context = new Mockery();
	private final AuctionEventListener listener = context.mock(AuctionEventListener.class);
	private final XMPPFailureReporter failureReporter = context.mock(XMPPFailureReporter.class);
	private final AuctionMessageTranslator translator = new AuctionMessageTranslator(SNIPER_ID, listener,
			failureReporter);

	@Test
	@Order(1)
	void notifiesAuctionClosedWhenCloseMessageReceived() {
		context.checking(new Expectations() {
			{
				oneOf(listener).auctionClosed();
			}
		});
		Message message = new Message();
		message.setBody("SOLVersion: 1.1; Event: CLOSE;");
		translator.processMessage(UNUSED_CHAT, message);
	}

	@Test
	@Order(2)
	void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromOtherBidder() {
		context.checking(new Expectations() {
			{
				exactly(1).of(listener).currentPrice(192, 7, PriceSource.FromOtherBidder);
			}
		});
		Message message = new Message();
		message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;");
		translator.processMessage(UNUSED_CHAT, message);
	}

	@Test
	@Order(3)
	void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromSniper() {
		context.checking(new Expectations() {
			{
				exactly(1).of(listener).currentPrice(234, 5, PriceSource.FromSniper);
			}
		});
		Message message = new Message();
		message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 234; Increment: 5; Bidder: " + SNIPER_ID + ";");
		translator.processMessage(UNUSED_CHAT, message);
	}

	@Test
	@Order(4)
	void notifiesAuctionFailedWhenBadMessageReceived() {
		String badMessage = "a bad message";

		expectFailureWithMessage(badMessage);

		Message message = new Message();
		message.setBody("a bad message");

		translator.processMessage(UNUSED_CHAT, message);
	}

	@Test
	@Order(5)
	void notifiesAuctionFailedWhenEventTypeMissing() {
		context.checking(new Expectations() {
			{
				exactly(1).of(listener).auctionFailed();
			}
		});

		Message message = new Message();
		message.setBody("SOLVersion: 1.1; CurrentPrice: 234; Increment: 5; Bidder: " + SNIPER_ID + ";");
		translator.processMessage(UNUSED_CHAT, message);
	}

	private Message message(String body) {
		Message message = new Message();
		message.setBody(body);
		return message;
	}

	private void expectFailureWithMessage(final String badMessage) {
		context.checking(new Expectations() {
			{
				oneOf(listener).auctionFailed();
				oneOf(failureReporter).cannotTranslateMessage(with(SNIPER_ID), with(badMessage),
						with(any(Exception.class)));
			}
		});
	}

}

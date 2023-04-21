package auction.sniper.unit;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;

import auction.sniper.AuctionEventListener;
import auction.sniper.AuctionMessageTranslator;

class AuctionMessageTranslatorTest {

	static final Chat UNUSED_CHAT = null;

	private final JUnit5Mockery context = new JUnit5Mockery();

	private final AuctionEventListener listener = context.mock(AuctionEventListener.class);

	private final AuctionMessageTranslator translator = new AuctionMessageTranslator(listener);

	@Test
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
	void notifiesBidDetailsWhenCurrentPriceMessageReceived() {
		context.checking(new Expectations() {
			{
				exactly(1).of(listener).currentPrice(192, 7);
			}
		});
		Message message = new Message();
		message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;");
		translator.processMessage(UNUSED_CHAT, message);
	}

}

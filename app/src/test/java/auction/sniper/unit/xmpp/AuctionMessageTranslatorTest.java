package auction.sniper.unit.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.jupiter.api.Test;

import auction.sniper.core.AuctionEventListener;
import auction.sniper.core.AuctionEventListener.PriceSource;
import auction.sniper.xmpp.AuctionMessageTranslator;

import static auction.sniper.endtoend.ApplicationRunner.SNIPER_ID;

class AuctionMessageTranslatorTest {

	static final Chat UNUSED_CHAT = null;

	private final JUnit5Mockery context = new JUnit5Mockery();

	private final AuctionEventListener listener = context.mock(AuctionEventListener.class);

	private final AuctionMessageTranslator translator = new AuctionMessageTranslator(SNIPER_ID, listener);

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
	public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromSniper() {
		context.checking(new Expectations() {
			{
				exactly(1).of(listener).currentPrice(234, 5, PriceSource.FromSniper);
			}
		});
		Message message = new Message();
		message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 234; Increment: 5; Bidder: " + SNIPER_ID + ";");
		translator.processMessage(UNUSED_CHAT, message);
	}

}

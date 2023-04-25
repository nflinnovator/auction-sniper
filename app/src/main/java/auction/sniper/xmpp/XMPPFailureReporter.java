package auction.sniper.xmpp;

public interface XMPPFailureReporter {
	void cannotTranslateMessage(String auctionId, String failedMessage, Exception exception);
}

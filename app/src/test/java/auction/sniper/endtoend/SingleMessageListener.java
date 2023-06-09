package auction.sniper.endtoend;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.hamcrest.Matcher;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;

class SingleMessageListener implements MessageListener {

	private final ArrayBlockingQueue<Message> messages = new ArrayBlockingQueue<Message>(1);

	@Override
	public void processMessage(Chat chat, Message message) {
		messages.add(message);
	}

	void receivesAMessage() throws InterruptedException {
		assertThat("Message", messages.poll(5, TimeUnit.SECONDS), is(notNullValue()));
	}

	public void receivesAMessage(Matcher<? super String> messageMatcher) throws InterruptedException {
		final Message message = messages.poll(5, TimeUnit.SECONDS);
		assertThat(message, hasProperty("body", messageMatcher));
	}

}

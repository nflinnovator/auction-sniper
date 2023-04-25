package auction.sniper.unit.xmpp;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import auction.sniper.xmpp.LoggingXMPPFailureReporter;

@DisplayName("Logging XMPP Failure Reporter Unit Test Case")
@SuppressWarnings("deprecation")
class LoggingXMPPFailureReporterTest {

	private final Mockery context = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};
	private final Logger logger = context.mock(Logger.class);
	private final LoggingXMPPFailureReporter reporter = new LoggingXMPPFailureReporter(logger);

	@AfterAll
	public static void resetLogging() {
		LogManager.getLogManager().reset();
	}

	@Test
	void writesMessageTranslationFailureToLog() {
		context.checking(new Expectations() {
			{
				oneOf(logger).severe("<auction id> " + "Could not translate message \"bad message\" "
						+ "because \"java.lang.Exception: an exception\"");
			}
		});

		reporter.cannotTranslateMessage("auction id", "bad message", new Exception("an exception"));
	}

}

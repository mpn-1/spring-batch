package org.springframework.batch.integration.retry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

@MessageEndpoint
public class SimpleService implements Service {

	private final Log logger = LogFactory.getLog(getClass());

	private final List<String> processed = new CopyOnWriteArrayList<>();

	private List<String> expected = new ArrayList<>();

	private final AtomicInteger count = new AtomicInteger(0);

	public void setExpected(List<String> expected) {
		this.expected = expected;
	}

	/**
	 * Public getter for the processed.
	 * @return the processed
	 */
	public List<String> getProcessed() {
		return processed;
	}

	@ServiceActivator(inputChannel = "requests", outputChannel = "replies")
	public String process(String message) {
		String result = message + ": " + count.incrementAndGet();
		logger.debug("Handling: " + message);
		if (count.get() <= expected.size()) {
			processed.add(message);
		}
		if ("fail".equals(message)) {
			throw new RuntimeException("Planned failure");
		}
		return result;
	}

}

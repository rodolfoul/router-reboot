package org.rl.checker;

import java.io.PrintWriter;
import java.net.UnknownHostException;

public class RebootBuilder {

	private PrintWriter logWriter = new PrintWriter(System.out);

	public RebootBuilder() {
	}

	public RebootBuilder setLogWriter(PrintWriter pw) {
		this.logWriter = pw;

		return this;
	}

	public PostMethod build() {
		PostMethod postMethod = new PostMethod();

		postMethod.setLogWriter(logWriter);

		try {
			HostAliveChecker aliveChecker = new HostAliveChecker("192.168.23.1");
			aliveChecker.setLogWriter(logWriter);
			postMethod.setHostAliveChecker(aliveChecker);

		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
		return postMethod;
	}
}
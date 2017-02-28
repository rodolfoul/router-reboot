package org.rl.checker;

import java.io.PrintStream;
import java.net.UnknownHostException;

public class RebootBuilder {

	private PrintStream ps = System.out;

	public RebootBuilder() {
	}

	public RebootBuilder setPrintStream(PrintStream ps) {
		this.ps = ps;

		return this;
	}

	public PostMethod build() {
		PostMethod postMethod = new PostMethod();

		postMethod.setPrintStream(ps);

		try {
			HostAliveChecker aliveChecker = new HostAliveChecker("192.168.23.1", true);
			aliveChecker.setLogStream(ps);
			postMethod.setHostAliveChecker(aliveChecker);

		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
		return postMethod;
	}
}
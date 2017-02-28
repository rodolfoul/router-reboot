package org.rl.checker;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

class HostAliveChecker implements Runnable {
	private final InetAddress host;
	private final AtomicBoolean hostAlive = new AtomicBoolean(false);
	private final boolean exitOnHostDown;
	private PrintStream logStream;

	public HostAliveChecker(String host) throws UnknownHostException {
		this(host, false);
	}

	public HostAliveChecker(String host, boolean exitOnHostDown) throws UnknownHostException {
		this.host = InetAddress.getByName(host);
		this.exitOnHostDown = exitOnHostDown;
	}

	@Override
	public void run() {
		try {
			while (true) {
				hostAlive.set(host.isReachable(500));
				if (!isHostAlive() && exitOnHostDown) {
					logStream.println("Host went down, exiting...");
					return;
				}
				Thread.sleep(500);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			logStream.println("Thread interrupted.");
		}
	}

	public boolean isHostAlive() {
		return hostAlive.get();
	}

	public void setLogStream(PrintStream logStream) {
		this.logStream = logStream;
	}
}
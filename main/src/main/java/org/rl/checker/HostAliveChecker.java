package org.rl.checker;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

class HostAliveChecker implements Runnable {
	private final InetAddress host;
	private final AtomicBoolean hostAlive = new AtomicBoolean(false);
	private PrintWriter logWriter;

	HostAliveChecker(String host) throws UnknownHostException {
		this.host = InetAddress.getByName(host);
	}

	@Override
	public void run() {
		try {
			while (true) {
				hostAlive.set(host.isReachable(500));
				if (!isHostAlive()) {
					logWriter.println("Host went down, exiting...");
					return;
				}
				Thread.sleep(500);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			logWriter.println("Thread interrupted.");
		}
	}

	public boolean isHostAlive() {
		return hostAlive.get();
	}

	void setLogWriter(PrintWriter logWriter) {
		this.logWriter = logWriter;
	}
}
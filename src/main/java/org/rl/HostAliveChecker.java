package org.rl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

public class HostAliveChecker implements Runnable {
	private final InetAddress host;
	private final AtomicBoolean hostAlive = new AtomicBoolean(false);
	private final boolean exitOnHostDown;

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
				if (!hostAlive.get()) {
					System.out.println("Host went down, exiting...");
					System.exit(0);
				}
				Thread.sleep(500);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("Thread interrupted.");
		}
	}

	public boolean isHostAlive() {
		return hostAlive.get();
	}
}
package org.rl;

import org.rl.checker.PostMethod;
import org.rl.checker.RebootBuilder;

import javax.websocket.EndpointConfig;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;

@ServerEndpoint("/doReboot")
public class RebootForwarder {

	@OnOpen
	public synchronized void doReboot(Session session, EndpointConfig ec) throws IOException, InterruptedException {

		try (PipedWriter sink = new PipedWriter();
		     PipedReader src = new PipedReader(sink);
		     BufferedReader br = new BufferedReader(src)) {

			PostMethod postMethod = new RebootBuilder().setLogWriter(new PrintWriter(sink)).build();
			new Thread(postMethod).start();

			try {
				String line;
				while ((line = br.readLine()) != null) {
					session.getBasicRemote().sendText(line + "\n");
				}
			} catch (IOException e) {
				//Threads exit make pipedReader throw an exception.
			}
		}

		session.close();
	}
}
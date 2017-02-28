package org.rl;

import org.rl.checker.PostMethod;

import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@ServerEndpoint("/doReboot")
public class RebootForwarder {

	private ProcessBuilder processBuilder;

	public RebootForwarder() throws IOException {
		Path binFolder = Paths.get(System.getProperty("java.home"), "bin");
		Path javaExecutable = binFolder.resolve("java.exe");
		if (!Files.exists(javaExecutable)) {
			javaExecutable = binFolder.resolve("java");
		}

		try {
			Path jarFile = Paths.get(new String(Files.readAllBytes(
					Paths.get(getClass().getResource("/routerRebootPath").toURI())), StandardCharsets.UTF_8));


//			Path jarFile = Paths.get(getClass().getProtectionDomain().getCodeSource().getLocation().toURI())
//			                    .resolve("../lib/router-reboot-main.jar").toRealPath();
			processBuilder = new ProcessBuilder(javaExecutable.toRealPath().toString(), "-jar", jarFile.toString());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

	}

	@OnMessage
	public void handleMessage(String message) {
		System.out.println(message);
	}

	@OnOpen
	public synchronized void doReboot(Session session, EndpointConfig ec) throws IOException, InterruptedException {
		PostMethod postMethod = new PostMethod();
		postMethod.execute();
	}
}
package org.rl.checker;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.List;

public class PostMethod {

	private RequestConfig requestConfig;
	private PrintStream ps;
	private HostAliveChecker aliveChecker;

	PostMethod() {
		int timeOutMs = 5 * 1000; // Timeout in millis.
		requestConfig = RequestConfig.custom()
		                             .setConnectionRequestTimeout(timeOutMs)
		                             .setConnectTimeout(timeOutMs)
		                             .setSocketTimeout(timeOutMs)
		                             .build();
	}

	public void setPrintStream(PrintStream ps) {
		this.ps = ps;
	}

	public void setHostAliveChecker(HostAliveChecker aliveChecker) {
		this.aliveChecker = aliveChecker;
	}

	public void execute() throws IOException {
		Thread t = new Thread(aliveChecker);
		t.setDaemon(true);
		t.start();

		CloseableHttpClient client = HttpClients.createDefault();

		try {
			boolean loggedIn = false;
			while (true) {
				try {
					if (!loggedIn) {
						loggedIn = tryLogin(client);
					}

					if (loggedIn) {
						if (tryReboot(client)) {
							ps.println("Successful reboot!");
							return;
						}
					}

				} catch (SocketTimeoutException | ConnectTimeoutException e) {
					ps.println("Timed out during reboot process");
				}
			}

		} catch (HttpHostConnectException e) {
			ps.println("Could not connect to client, exiting.");
			ps.println(e.getCause());
			return;
		}
	}

	private boolean tryLogin(CloseableHttpClient client) throws IOException {
		ps.println("Trying to log in.");
		HttpPost httpPost = new HttpPost("http://192.168.23.1/goform/login");
		httpPost.setConfig(requestConfig);
		List<BasicNameValuePair> postData = Arrays.asList(new BasicNameValuePair("loginUsername", "admin"),
		                                                  new BasicNameValuePair("loginPassword", "password"));
		httpPost.setEntity(new UrlEncodedFormEntity(postData));
		CloseableHttpResponse response = client.execute(httpPost);
		if ("http://192.168.23.1/RgSwInfo.asp".equals(response.getHeaders("Location")[0].getValue())) {
			ps.println("Successful log in!");
			return true;
		}
		return false;
	}

	private boolean tryReboot(CloseableHttpClient client) throws IOException {
		ps.println("Trying to reboot client.");
		HttpPost httpPost = new HttpPost("http://192.168.23.1/goform/RgSetup");
		httpPost.setConfig(requestConfig);

		httpPost.setEntity(new UrlEncodedFormEntity(Arrays.asList(new BasicNameValuePair("RebootAction", "1"))));

		CloseableHttpResponse response = client.execute(httpPost);
		HttpEntity entity = response.getEntity();

		int responseCode = response.getStatusLine().getStatusCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			printEntityContent(entity);
			return true;
		}

		return false;
	}

	private void printEntityContent(HttpEntity entity) throws IOException {
		BufferedReader is = new BufferedReader(
				new InputStreamReader(entity.getContent()));
		String s;
		while ((s = is.readLine()) != null) {
			ps.println(s);
		}
	}
}
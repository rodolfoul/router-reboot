package org.rl;

import org.rl.checker.RebootBuilder;

import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {
		new RebootBuilder().build().execute();
	}
}
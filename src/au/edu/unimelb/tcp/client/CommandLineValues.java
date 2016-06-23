package au.edu.unimelb.tcp.client;

import org.kohsuke.args4j.Option;

public class CommandLineValues {

	@Option(required = true, name = "-h", aliases ={"--host"}, usage="Host Address")
	private String host;
	
	// Give it a default value of 4444 sec
	@Option(required = true, name = "-p", aliases = {"--port"}, usage="Port Address")
	private int port = 4444;

	public int getPort() {
		return port;
	}

	public String getHost() {
		return host;
	}

}

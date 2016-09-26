package com.mattrader.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * An abstract class that provides methods useful for managing connections to services.
 * A service when instanced won't try directly to connect to Darwin, but it's possible
 * to start a connection by the mean of {@link ServiceCom#open()}; status of a service
 * is requested through a call to {@link ServiceCom#isOpen()}
 * 
 * @author Luca Poletti
 *
 */
public abstract class ServiceCom {

	private LogCom log;

	protected Socket socket;
	protected BufferedReader input;
	protected PrintWriter output;
	protected ServiceStreamReaderCom streamReader;
	protected Thread thread;
	
	protected MTClientBaseCom dcb;
	/**
	 * Constructor, it prepare the structure, but it neither creates any connection
	 * nor spawns any thread
	 * 
	 * @param callerDcb - the client that creating this service
	 */
	ServiceCom(MTClientBaseCom callerDcb) {
		dcb = callerDcb;

		log = dcb.log();
		log.c(this, "constructor");

		socket = null;
		input = null;
		output = null;
		thread = null;
	}
	
	/**
	 * open this service, creating necessary connections
	 */
	void open() {
		log.c(this, "opening");
		if(!this.isOpen()) {
			try {
				socket = new Socket(dcb.hostname, this.getPort());
				input = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
				output = new PrintWriter(socket.getOutputStream(),true);
			} catch (IOException e) {
				System.err.println("Error: unable to open socket");
				CloseHelperCom.close(output);
				CloseHelperCom.close(input);
				CloseHelperCom.close(socket);
				output = null; // delete any reference for GCing it
				input = null; // delete any reference for GCing it
				socket = null; // delete any reference for GCing it
			}
			streamReader = new ServiceStreamReaderCom(input, dcb);
			thread = new Thread(streamReader);
			thread.start();
		}
	}
	
	/**
	 * close this service, disconnect any connection and stop the thread
	 */
	void close() { // FIXME NullPointerException; //credo per un bug nella open
		log.c(this, "close");
		CloseHelperCom.close(output);
		CloseHelperCom.close(input);
		CloseHelperCom.close(socket);
		CloseHelperCom.close(thread);;
		// END thread interruption;
		output = null; // delete any reference for GCing it
		input = null; // delete any reference for GCing it
		socket = null; // delete any reference for GCing it
	}

	/**
	 * Check if the Heartbeat signal has been received
	 * 
	 * @see ServiceStreamReaderCom#checkHeartbeat()
	 */
	void checkHeartbeat() {
		streamReader.checkHeartbeat();
	}

	/**
	 * Low level function. Print arbitrary output to the console Darwin
	 * 
	 * @param cmd - the String containing the command
	 */
	void d__println(String cmd) { // only for debugging pourpouse, to be shifted into
				// packagePrivate visibility or eventually remove.
		output.println(cmd);
		log.cmd(cmd);
	}

	/**
	 * @return true if the service is open and connections have been instantiated
	 */
	public boolean isOpen() { // FIXME [?] it return false always
		return !(socket == null || socket.isClosed() == true || socket.isConnected() == false);
	}

	/**
	 * @return the port on the Darwin corresponding to this service
	 */
	public abstract int getPort();
	
	/**
	 * Low level call. Request the status of Darwin
	 */
	void d_DARWINSTATUS() {
		d__println("DARWINSTATUS");
	}
	
	//////// Open and close events ////////

	/**
	 * abstract method, provided for event dispatch
	 */
	@Deprecated
	public abstract void onOpen();

	/**
	 * abstract method, provided for event dispatch
	 */
	@Deprecated
	public abstract void onClose();
	
	/**
	 * Control if received messages must be shown in console
	 * 
	 * @param doPrintStream - true to print data in console
	 */
	protected void enableStreamOutput(boolean doPrintStream) {
		streamReader.setOverridePrint(doPrintStream);
	}

}

package com.mattrader.common;

import java.io.BufferedReader;

/**
 * This class implements a runnable and is used by {@link ServiceCom} to spawn a
 * thread that will read data from socket. This runnable waits for a message
 * from Darwin and will dispatch it to {@link DispatcherCom} class.
 * 
 * @author Luca Poletti
 *
 */
class ServiceStreamReaderCom implements Runnable {

	BufferedReader socketIn;
	boolean interrupted;
	
	DispatcherCom dispatcher;
	
	MTClientBaseCom dcb;
	
	LogCom log;
	
	private boolean skippedHeartbeat;
	private int capturedHeartbeat;
	
	private Object lockHeartbeat;

	boolean overridePrint; //debug
	boolean lostConnectionErrorSent; //send lost connection error only once

	/**
	 * Constructor
	 * 
	 * @param socketInput - BufferedReader from which this runnable has to read
	 * @param callerDcb - {@link MTClientBaseCom} client hosting the service
	 * running this runnable
	 */
	ServiceStreamReaderCom(BufferedReader socketInput, MTClientBaseCom callerDcb) {
		socketIn = socketInput;
		interrupted = false;
		dcb = callerDcb;
		dispatcher = dcb.dispatcher;
		overridePrint = false;
		
		log = dcb.log();
		
		skippedHeartbeat = false;
		lostConnectionErrorSent = false;
		capturedHeartbeat = 0;
		lockHeartbeat = new Object();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		String output = "";
		while(!interrupted && socketIn != null) {
			try {
				output = socketIn.readLine();
				if((overridePrint || dcb.printOutput) && !(dcb.ignoreHeartbeat && output.equals("H")))
					System.out.println(output);
				dispatcher.dispatch(output);
				if(output.equals("H"))
					synchronized(lockHeartbeat) {
						capturedHeartbeat++;
					}
			} catch (Exception e) {
				if (!lostConnectionErrorSent) {
					log.e(this, "Connection lost!!");
					lostConnectionErrorSent = true;
				}
				return;
			}
		}
	}

	/**
	 * Check if the Heartbeat signal has been received
	 * 
	 * @see ServiceCom#checkHeartbeat()
	 */
	void checkHeartbeat() {
		synchronized(lockHeartbeat) {
			if(capturedHeartbeat == 0)
				if(skippedHeartbeat == true)
					if (!lostConnectionErrorSent) {
						log.e(this,"heartbeat signal not received in last 20 seconds, check connection");
						lostConnectionErrorSent = true;
					}
				else
					skippedHeartbeat = true;
			else
				skippedHeartbeat = false;
			capturedHeartbeat = 0;
		}
	}

	/**
	 * Control if received messages must be shown in console; override default
	 * behavior
	 * 
	 * @param override - true to print data in console
	 */
	void setOverridePrint(boolean override) {
		overridePrint = override;
	}

}

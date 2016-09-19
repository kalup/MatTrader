package com.mattrader.common;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The role of this Singleton is managing all the clients that will be connected to the Darwin.
 * <p>
 * There is a single global DarwinClientManagerCom and it's the resource that is necessary to 
 * query in order to obtain a {@link DarwinClientBaseCom}. It also provide a method to stop 
 * every client running.
 * <p>
 * Every access to any method is accomplished by the mean of the Manager
 * <pre>
 * {@code
 * DarwinClientManagerCom.Manager.getClient(clientName);
 * 
 * DarwinClientManagerCom.Manager.getClients();
 * 
 * DarwinClientManagerCom.Manager.stopAll();
 * }
 * </pre>
 * It is thread safe.
 * <p>
 * @author Luca Poletti
 *
 */
public enum DarwinClientManagerCom {

	/**
	 * The instance of the Singleton
	 */
	Manager; // Enum list
	
	/**
	 * The logger for all the uncaught Exceptions that may rise at run time
	 */
	private final LogCom uncaughtExceptionLogger;

	/**
	 * The map that keep track of all the clients instantiated
	 */
	private ConcurrentHashMap<String, DarwinClientBaseCom> dcbMap;
		// Maybe better: Collections.synchronizedMap(new HashMap(...));

	/**
	 * Constructor, it get called automatically the first time Manager is required.
	 * Thread safe
	 */
	private DarwinClientManagerCom() {
		uncaughtExceptionLogger = new LogCom();
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){			
			public void uncaughtException(Thread thread, Throwable throwable) {
				uncaughtExceptionLogger.e(thread, throwable);
			}
		});
		dcbMap = new ConcurrentHashMap<String, DarwinClientBaseCom>();
	}
	
	/**
	 * Method to obtain a {@link DarwinClientBaseCom} given the name. If in memory 
	 * it exists a client with the same name, it will be returned, else a new one
	 * will be
	 * @param name - the name of the client
	 * @return a {@link DarwinClientBaseCom} client
	 */
	public synchronized DarwinClientBaseCom getClient(String name) {
		if(!dcbMap.isEmpty() && dcbMap.containsKey(name))
			return dcbMap.get(name);
		DarwinClientBaseCom dcb = new DarwinClientBaseCom(name);
		dcbMap.put(name, dcb);
		return dcb;
	}
	
	/**
	 * Method that returns a collection of all the clients instantiated
	 * @return a collection of {@link DarwinClientBaseCom} clients
	 */
	public synchronized Collection<DarwinClientBaseCom> getClients() {
		return dcbMap.values();
	}
	
	/**
	 * Method that stop every {@link DarwinClientBaseCom} client instantiated
	 */
	public synchronized void stopAll() {
		for(String name : dcbMap.keySet()) {
			dcbMap.get(name).close();
		}
		dcbMap.clear();
	}

}

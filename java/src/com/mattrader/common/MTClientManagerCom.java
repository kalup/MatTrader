package com.mattrader.common;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The role of this Singleton is managing all the clients that will be connected to the Darwin.
 * <p>
 * There is a single global MTClientManagerCom and it's the resource that is necessary to 
 * query in order to obtain a {@link MTClientBaseCom}. It also provide a method to stop 
 * every client running.
 * <p>
 * Every access to any method is accomplished by the mean of the Manager
 * <pre>
 * {@code
 * MTClientManagerCom.Manager.getClient(clientName);
 * 
 * MTClientManagerCom.Manager.getClients();
 * 
 * MTClientManagerCom.Manager.stopAll();
 * }
 * </pre>
 * It is thread safe.
 * <p>
 * @author Luca Poletti
 *
 */
public enum MTClientManagerCom {

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
	private ConcurrentHashMap<String, MTClientBaseCom> dcbMap;
		// Maybe better: Collections.synchronizedMap(new HashMap(...));

	private IProvider provider;
	
	/**
	 * Constructor, it get called automatically the first time Manager is required.
	 * Thread safe
	 */
	private MTClientManagerCom() {
		uncaughtExceptionLogger = new LogCom();
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){			
			public void uncaughtException(Thread thread, Throwable throwable) {
				uncaughtExceptionLogger.e(thread, throwable);
			}
		});
		dcbMap = new ConcurrentHashMap<String, MTClientBaseCom>();
		provider = null;
	}
	
	/**
	 * Method used to set a provider. Once a provider has been set it can't be changed anymore.
	 * 
	 * @param provider
	 */
	public synchronized void setProvider(IProvider provider) {
		if(this.provider != null)
			return;
		this.provider = provider;
	}
	
	/**
	 * Method to obtain a {@link MTClientBaseCom} given the name. If in memory 
	 * it exists a client with the same name, it will be returned, else a new one
	 * will be returned
	 * @param name - the name of the client
	 * @return a {@link MTClientBaseCom} client
	 */
	public synchronized MTClientBaseCom getClient(String name) throws NullPointerException {
		if(provider == null) {
			this.uncaughtExceptionLogger.e(this, "An instance of IProvider must be set before "
					+ "requesting a client. Use MTClientManagerCom#setProvider(IProvider) method.");
			throw new NullPointerException();
		}
		if(!dcbMap.isEmpty() && dcbMap.containsKey(name))
			return dcbMap.get(name);
		MTClientBaseCom dcb = new MTClientBaseCom(name, provider);
		dcbMap.put(name, dcb);
		return dcb;
	}
	
	/**
	 * Method that returns a collection of all the clients instantiated
	 * @return a collection of {@link MTClientBaseCom} clients
	 */
	public synchronized Collection<MTClientBaseCom> getClients() {
		return dcbMap.values();
	}
	
	/**
	 * Method that stop every {@link MTClientBaseCom} client instantiated
	 */
	public synchronized void stopAll() {
		for(String name : dcbMap.keySet()) {
			dcbMap.get(name).close();
		}
		dcbMap.clear();
	}

}

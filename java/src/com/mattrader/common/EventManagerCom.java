package com.mattrader.common;

import java.util.EventListener;
import java.util.EventObject;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class manage a generic type of event, a class extending this one will handle events
 * and for every type of event there exists one EventManagerCom. For each event there may
 * exist many different callback.
 * <p>
 * A callback must be implemented by creating a class implementing the interface
 * {@link DirectaConnectorListener} and override the
 * {@link DirectaConnectorListener#onEvent(EventObject)} method
 * <p>
 * To register a callback {@link EventManagerCom#addDirectaConnectorListener(DirectaConnectorListener)}
 * method must be called, passing as an argument the listener implementing the callback.
 * <br>
 * To remove a callback {@link EventManagerCom#removeDirectaConnectorListener(DirectaConnectorListener)}
 * method must be called, passing as an argument the listener implementing the callback.
 * <p>
 * When an event has to be fired is necessary to call {@link EventManagerCom#receivedEvent(EventObject)}
 * 
 * @author Luca Poletti
 *
 */
public class EventManagerCom {
	
	private LogCom log;
	
	/**
	 * Constructor
	 * 
	 * @param dcb - {@link DarwinClientBaseCom} served by this {@link EventManagerCom}
	 */
	EventManagerCom(DarwinClientBaseCom dcb) {
		log = dcb.log();
	}

	private CopyOnWriteArrayList<DirectaConnectorListener> listenerList = new CopyOnWriteArrayList<DirectaConnectorListener>();

	/**
	 * Register a {@link DirectaConnectorListener} implementing a callback. When an event is
	 * received the callback will be invoked. There may be many Listeners.
	 * 
	 * @param lis - the listener implementing a callback
	 */
	public synchronized void addDirectaConnectorListener(DirectaConnectorListener lis) {
		listenerList.add(lis);
	}

	/**
	 * Unregister a {@link DirectaConnectorListener} implementing a callback. Remove the
	 * callback from the list. When an event is received this callback will not be invoked
	 * anymore
	 * 
	 * @param lis - the listener implementing a callback
	 */
	public synchronized void removeDirectaConnectorListener(DirectaConnectorListener lis) {
		listenerList.remove(lis);
	}

	/**
	 * An interface extending {@link EventListener}. It defines a method that all event
	 * listener must implements: {@link DirectaConnectorListener#onEvent(EventObject)}
	 * 
	 * @author Luca Poletti
	 *
	 */
	public interface DirectaConnectorListener extends EventListener {
		/**
		 * This method is invoked when an event is fired and this listener is registered
		 * 
		 * @param event - the fired event
		 */
		public void onEvent(EventObject event);
	}

	/**
	 * This method is invoked when an event is fired. It will sequentially call any listener
	 * registered in this manager.
	 * 
	 * @param event - the fired event
	 */
	public void receivedEvent(EventObject event) {
		log.ff(this,event.toString());
		for (int i = 0; i < listenerList.size(); i++) {
			listenerList.get(i).onEvent(event);
		}
	}

}

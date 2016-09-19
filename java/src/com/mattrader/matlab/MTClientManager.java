package com.mattrader.matlab;

import java.util.Collection;

import com.mattrader.common.MTClientBaseCom;
import com.mattrader.common.MTClientManagerCom;

public enum MTClientManager {

	Manager;

	public synchronized MTClientBase getClient(String name) {
		try {
			return new MTClientBase(MTClientManagerCom.Manager.getClient(name));
		} catch (Exception e) {
			return null;
		}
	}

	public synchronized MTClientBase[] getClients() {
		Collection<MTClientBaseCom> clientsCom = MTClientManagerCom.Manager.getClients();
		MTClientBase[] clients = new MTClientBase[clientsCom.size()];
		int i = 0;
		for(MTClientBaseCom clientCom : clientsCom)
			try {
				clients[i] = new MTClientBase(clientCom);
				++i;
			} catch (Exception e) {
			}
		return clients;
	}

	public synchronized void stopAll() {
		MTClientManagerCom.Manager.stopAll();
	}
}

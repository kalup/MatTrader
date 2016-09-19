package com.mattrader.matlab;

import java.util.Collection;

import com.mattrader.common.DarwinClientBaseCom;
import com.mattrader.common.DarwinClientManagerCom;

public enum DarwinClientManager {

	Manager;

	public synchronized DarwinClientBase getClient(String name) {
		try {
			return new DarwinClientBase(DarwinClientManagerCom.Manager.getClient(name));
		} catch (Exception e) {
			return null;
		}
	}

	public synchronized DarwinClientBase[] getClients() {
		Collection<DarwinClientBaseCom> clientsCom = DarwinClientManagerCom.Manager.getClients();
		DarwinClientBase[] clients = new DarwinClientBase[clientsCom.size()];
		int i = 0;
		for(DarwinClientBaseCom clientCom : clientsCom)
			try {
				clients[i] = new DarwinClientBase(clientCom);
				++i;
			} catch (Exception e) {
			}
		return clients;
	}

	public synchronized void stopAll() {
		DarwinClientManagerCom.Manager.stopAll();
	}
}

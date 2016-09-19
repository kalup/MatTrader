package com.mattrader.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author Luca
 *
 * Help closing resources to keep code clean.
 * Since we are constrained to 1.6 to compatibility with MATLAB, closeable seems not to be an option.
 * 
 * Internal use only. Protected class.
 */
class CloseHelperCom {

	static void close(PrintWriter res) {
		if(res != null) {
			res.close();
		}
	}
	
	static void close(BufferedReader res) {
		if(res != null) {
			try {
				res.close();
			} catch (IOException e) {
				System.err.println("Error: unable to close resource");
			}
		}
	}
	
	static void close(Socket res) {
		if(res != null) {
			try {
				res.close();
			} catch (IOException e) {
				System.err.println("Error: unable to close resource");
			}
		}
	}
	
	static void close(Thread res) {
		if(res != null) {
			// TODO: study how interrupt a thread properly;
			res.interrupt();
			res = null;
		}
	}

}

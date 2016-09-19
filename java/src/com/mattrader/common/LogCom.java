package com.mattrader.common;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * This class is an helper for logging features. It redirect logs to console and to file,
 * provides a fast logging syntax and methods to change log levels.
 * <p>
 * Log levels are:
 * <p>
 * - e: error or severe<br>
 * - w: warning<br>
 * - i: info<br>
 * - c: config<br>
 * - f: fine<br>
 * - ff: finer<br>
 * - fff: finest<br>
 * <p>
 * Log to console is enabled by default for warning end severe messages.
 * It's specific for every client.
 * <br>
 * Log to file is enabled by default for a level greater than fine. It's
 * specific for every client.
 * <br>
 * There is also a log feature that has to be used for logging uncaught
 * exceptions. It is shared among all clients. It's enabled by default
 * for error level, since it logs exceptions.
 * <p>
 * Log on file will take action if in the folder where the main thread
 * has been spawned there is a folder called Log
 * 
 * @author Luca Poletti
 *
 */
public class LogCom {

	private Logger fileLog; // log output to file
	private Logger consoleLog; // log output to console
	private Logger fileCommandLog; // log sent commands to file
	private FileHandler fileHandler; // file where data are logged
	private FileHandler fileCommandHandler; // file where commands are logged
	private ConsoleHandler consoleHandler = new ConsoleHandler(); // console handler
	private EventManagerCom errorEventManager = null;

	/**
	 * Constructor. It takes as an argument a client, in that way the
	 * logger and the file created will be specific for that client.
	 * The name of the file, if it will be created, will be in the format
	 * <p>
	 * dcb_CLIENTNAME_DATE.log
	 * <p>
	 * @param dcb - the {@link DarwinClientBaseCom} client associated
	 * with the log
	 */
	LogCom(DarwinClientBaseCom dcb) {
		consoleLog = Logger.getLogger("com.mattrader.common.consolelog."+dcb.clientName);
        // Request that every detail gets logged.
		consoleLog.setLevel(Level.SEVERE); //used to be Level.WARNING
		// Setup consoleHandler
		consoleLog.setUseParentHandlers(false);
		consoleHandler.setFormatter(new ConsoleSimpleFormatter());
		consoleLog.addHandler(consoleHandler);

		fileLog = Logger.getLogger("com.mattrader.common.filelog."+dcb.clientName);
		fileLog.setLevel(Level.WARNING);
		fileCommandLog = Logger.getLogger("com.mattrader.common.filecommandlog."+dcb.clientName);
		fileCommandLog.setLevel(Level.WARNING);
		File logDir = new File("Log");
		if (!logDir.exists()) logDir.mkdir(); //crea la cartella Log
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
			Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
			fileHandler = new FileHandler("Log/dcb_"
					+ dcb.clientName + "_"
					+ dateFormat.format(calendar.getTime())
					+ ".log", true); 
				// Dove lo metto questo file? Cartella temp? "/t"
			fileHandler.setFormatter(new SimpleFormatter());
			fileCommandHandler = new FileHandler("Log/command_"
					+ dcb.clientName + "_"
					+ dateFormat.format(calendar.getTime())
					+ ".log", true); 
				// Dove lo metto questo file? Cartella temp? "/t"
			fileCommandHandler.setFormatter(new SimpleFormatter());
		} catch (Exception e) {
			consoleLog.severe("Could not create client log file. In order to track errors"
					+ " create a folder called \"Log\" in the current directory");
			return;
		}
		// Remove the console from this Logger
		fileLog.setUseParentHandlers(false);
        // Send fileLog output to our FileHandler.
        fileLog.addHandler(fileHandler);
        // Request that every detail gets logged.
        fileLog.setLevel(Level.INFO);
		// Remove the console from this Logger
		fileCommandLog.setUseParentHandlers(false);
        // Send fileLog output to our FileHandler.
		fileCommandLog.addHandler(fileCommandHandler);
        // Request that every detail gets logged.
		fileCommandLog.setLevel(Level.INFO);

	}

	/**
	 * Constructor. It takes no arguments, and is thought as a default logger
	 * to log uncaught exceptions.
	 * The name of the file, if it will be created, will be in the format
	 * <p>
	 * uncaughtException_DATE.log
	 */
	LogCom() {
		consoleLog = Logger.getLogger("com.mattrader.common.consolelog.uncaughtException");
        // Request that every detail gets logged.
		consoleLog.setLevel(Level.SEVERE); //used to be level.SEVERE 
		// Setup consoleHandler
		consoleLog.setUseParentHandlers(false);
		consoleHandler.setFormatter(new ConsoleSimpleFormatter());
		consoleLog.addHandler(consoleHandler);

		fileLog = Logger.getLogger("com.mattrader.common.filelog.uncaughtException");
		fileLog.setLevel(Level.WARNING);
		File logDir = new File("Log");
		if (!logDir.exists()) logDir.mkdir(); //crea la cartella Log
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
			Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
			fileHandler = new FileHandler("Log/uncaughtException_"
					+ dateFormat.format(calendar.getTime())
					+ ".log", true); 
				// Dove lo metto questo file? Cartella temp? "%t/"
			fileHandler.setFormatter(new SimpleFormatter());
		} catch (Exception e) {
			consoleLog.warning("Could not create generic log file. In order to track errors"
					+ " create a folder called \"Log\" in the current directory");
			return;
		}
		// Remove the console from this Logger
		fileLog.setUseParentHandlers(false);
        // Send fileLog output to our FileHandler.
        fileLog.addHandler(fileHandler);
        // Request that every detail gets logged.
        fileLog.setLevel(Level.SEVERE);
	}

	/**
	 * Set the log level specifying which message levels will be logged by the console.
	 * Message levels lower than this value will be discarded.
	 * The level value Level.OFF can be used to turn off logging.
	 * If the new level is null, it means that this node should inherit its level from
	 * its nearest ancestor with a specific (non-null) level value.
	 * @param level - the new value for the log level (may be null)
	 */
	synchronized void changeConsoleLogLevel(Level level) {
		consoleLog.setLevel(level);
	}

	/**
	 * Set the log level specifying which message levels will be logged by the log file.
	 * Message levels lower than this value will be discarded.
	 * The level value Level.OFF can be used to turn off logging.
	 * If the new level is null, it means that this node should inherit its level from
	 * its nearest ancestor with a specific (non-null) level value.
	 * @param level - the new value for the log level (may be null)
	 */
	synchronized void changeFileLogLevel(Level level) {
		fileLog.setLevel(level);
	}

	/**
	 * Log a COMMAND message.
	 * @param msg - The string message to log
	 */
	void cmd(String msg) {
		fileCommandLog.info(msg + "\n\n");
	}

	/**
	 * Log a FINEST message.
	 * @param obj - The object that call this logger
	 * @param msg - The string message to log
	 */
	void fff(Object obj, String msg) {
		String newMsg = mFormat(obj.getClass().getName(), msg);
		fileLog.finest(newMsg);
		consoleLog.finest(newMsg);
	}

	/**
	 * Log a FINER message.
	 * @param obj - The object that call this logger
	 * @param msg - The string message to log
	 */
	void ff(Object obj, String msg) {
		String newMsg = mFormat(obj.getClass().getName(), msg);
		fileLog.finer(newMsg);
		consoleLog.finer(newMsg);
	}

	/**
	 * Log a FINE message.
	 * @param obj - The object that call this logger
	 * @param msg - The string message to log
	 */
	void f(Object obj, String msg) {
		String newMsg = mFormat(obj.getClass().getName(), msg);
		fileLog.fine(newMsg);
		consoleLog.fine(newMsg);
	}

	/**
	 * Log a CONFIG message.
	 * @param obj - The object that call this logger
	 * @param msg - The string message to log
	 */
	void c(Object obj, String msg) {
		String newMsg = mFormat(obj.getClass().getName(), msg);
		fileLog.config(newMsg);
		consoleLog.config(newMsg);
	}

	/**
	 * Log an INFO message.
	 * @param obj - The object that call this logger
	 * @param msg - The string message to log
	 */
	void i(Object obj, String msg) {
		String newMsg = mFormat(obj.getClass().getName(), msg);
		fileLog.info(newMsg);
		consoleLog.info(newMsg);
	}

	/**
	 * Log a WARNING message.
	 * @param obj - The object that call this logger
	 * @param msg - The string message to log
	 */
	void w(Object obj, String msg) {
		String newMsg = mFormat(obj.getClass().getName(), msg);
		fileLog.warning(newMsg);
		consoleLog.warning(newMsg);
	}

	/**
	 * Log an ERROR (SEVERE) message
	 * @param obj - The object that call this logger.
	 * @param msg - The string message to log
	 */
	void e(Object obj, String msg) {
		String newMsg = mFormat(obj.getClass().getName(), msg);
		fileLog.severe(newMsg);
		consoleLog.severe(newMsg);
		if(errorEventManager != null)
			errorEventManager.receivedEvent(new EventCom.ErrorEvent(obj, msg, -1));
	}

	/**
	 * Log an ERROR (SEVERE) message
	 * @param obj - The object that call this logger.
	 * @param t - The Throwable to log, generally an exception
	 */
	void e(Object obj, Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(new PrintWriter(pw));
		String newMsg = mFormat(obj.getClass().getName(), sw.toString());
		fileLog.severe(newMsg);
		consoleLog.severe(newMsg);
		if(errorEventManager != null)
			errorEventManager.receivedEvent(new EventCom.ErrorEvent(obj, newMsg, -1));
	}

	/**
	 * Format a log string in a more readable way
	 * 
	 * @param className - the name of the class calling the logger
	 * @param logString - the string to be logged
	 * @return a String in the logger format
	 */
	String mFormat(String className, String logString) {
		return className + ":\n\n\t " + logString + "\n\n";
	}
	
	/**
	 * This method is used to set an {@link EventManagerCom} that
	 * will be used to notify the occurrence of an error
	 * 
	 * @param errorEventManager the manager of type {@link EventManagerCom}
	 */
	void setErrorEventManager(EventManagerCom errorEventManager) {
		this.errorEventManager = errorEventManager;
	}
	
	/**
	 * A formatter to print the log output ttpically in a single
	 * line
	 * 
	 * @author Luca Poletti
	 *
	 */
	private class ConsoleSimpleFormatter extends Formatter {
		
		/**
		 * The format of the message logged.
		 * This prints 1 line with the log level (4$), the log
		 * message (5$) and the timestamp (1$) in a square bracket
		 * in the HH:MM:SS format 
		 */
		private static final String format="%4$s: %5$s [%1$tk:%1$tM:%1$tS]%n";
		private final Date dat = new Date();
		
		/* (non-Javadoc)
		 * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
		 */
		public synchronized String format(LogRecord record) {
			dat.setTime(record.getMillis());
			String source;
			if (record.getSourceClassName() != null) {
				source = record.getSourceClassName();
				if (record.getSourceMethodName() != null) {
					source += " " + record.getSourceMethodName();
				}
			} else {
				source = record.getLoggerName();
			}
			String message = formatMessage(record);
			String throwable = "";
			if (record.getThrown() != null) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				pw.println();
				record.getThrown().printStackTrace(pw);
				pw.close();
				throwable = sw.toString();
			}
			return String.format(format,
					dat,
					source,
					record.getLoggerName(),
					record.getLevel(),
					message,
					throwable);
		}
	}
}

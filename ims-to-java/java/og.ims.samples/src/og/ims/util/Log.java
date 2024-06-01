package og.ims.util;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import com.ibm.ims.dli.DLIException;
import com.ibm.ims.dli.EnvironInfo;
import com.ibm.ims.dli.ProgramInfo;
import com.ibm.ims.dli.tm.Application;
import com.ibm.ims.dli.tm.ApplicationFactory;

public class Log {

	private static final Logger imsLogger = Logger.getLogger("com.ibm.ims.db.opendb");
	private static final Logger myLogger = Logger.getLogger("og.ims");

	public static class MyConsoleHandler extends StreamHandler {
	    
		public MyConsoleHandler() {
	        super(System.out, new SimpleFormatter());
	    }
	    
	    @Override
	    public void publish(LogRecord record) {
	        super.publish(record);
	        flush();
	    }		
	}
	
	public static void entering(Class<?> cls, String method, String message) {
			myLogger.entering(cls.getName(), method, message);
	}

	public static void trace(Class<?> cls, String method, String message) {
		myLogger.logp(Level.FINER, cls.getName(), method, "TRACE {0}", message);
	}

	public static void enableInfo() {
		//log to stdout
		MyConsoleHandler fh = new MyConsoleHandler();
		fh.setFormatter(new SimpleFormatter());
		fh.setLevel(Level.FINEST);

		imsLogger.setLevel(Level.INFO);
		imsLogger.addHandler(fh);

		//new StreamHandler(System.out, new SimpleFormatter())
		myLogger.setLevel(Level.FINE);
		myLogger.addHandler(fh);
	}

	public static void enableDebug() {
		//log to stdout
		MyConsoleHandler fh = new MyConsoleHandler();
		fh.setFormatter(new SimpleFormatter());
		fh.setLevel(Level.FINEST);

		imsLogger.setLevel(Level.FINEST);
		imsLogger.addHandler(fh);

		//new StreamHandler(System.out, new SimpleFormatter())
		myLogger.setLevel(Level.FINEST);
		myLogger.addHandler(fh);
	}

	public static void info(Class<?> cls, String method,String message) {
		//Level.INFO seems to write to STDERR and STDOUT so using FINE instead
		myLogger.logp(Level.FINE, cls.getName(), method, "INFO {0}", message);
		
	}

	public static void infoIMS(Class<?> cls, String method) throws DLIException {
		Application app = ApplicationFactory.createApplication();
		ProgramInfo prog = app.inqyProgram();
		info(cls,method,"prog name=" + prog.getProgramName());
		EnvironInfo env = app.inqyEnviron();
		String psbName = env.getPSBName();
		info(cls,method,"PSB name=" + psbName);
		app.end();
	}
}

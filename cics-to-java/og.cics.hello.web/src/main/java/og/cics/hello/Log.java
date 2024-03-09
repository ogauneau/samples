package og.cics.hello;

import com.ibm.cics.server.TDQ;

public class Log {

	private static TDQ logTDQueue = new TDQ();
	
	public static void info(String msg) {
		// write to CURRENTJVM output file
		System.out.println(msg);
		try {
			// also write to CSMT queue/CICS joblog
			logTDQueue.setName("CSMT");
			logTDQueue.writeString(msg);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

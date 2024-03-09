package og.cics.hello;

import java.io.UnsupportedEncodingException;

import com.ibm.cics.server.Channel;
import com.ibm.cics.server.Container;
import com.ibm.cics.server.Task;
import com.ibm.cics.server.invocation.CICSProgram;

/*
 *  The HelloJava.execute() method can be called from COBOL using
 *  program name OGHWJ3 as indicated on the @CICSProgram annotation.
 *  
 *  EXEC CICS LINK PROGRAM(JAVA-PROGNAME)
 *               CHANNEL(CHANNEL-NAME)
 *               END-EXEC
 *               
 *  CICS program OGHWJ3 will be automatically defined in CICS once
 *  exported as OSGI bundle to a CICS bundle directory.
 *  No need to define it with CEDA.
 *               
 */
public class HelloJava {

	private String channelName = "HWCHANNEL";
	private String argumentContainer = "INHWJAV";
	private String resultContainer = "OUTHWJAV";
	
	/* Trying out the sample
	* You can use CECI to invoke the sample program:
	* CECI PUT CONTAINER(GREETINGS) CHAR FROM(HELLO) CHANNEL(HWCHANNEL)
	* CECI LINK PROGRAM(OGHWJ3)
	*/
	@CICSProgram("OGHWJ3") 
	public void execute() {
		System.out.println("HelloJava is running");
		String arg = getArgument();
		setResult("Java OSGI says hello to "+arg);		
	}

	private void setResult(String msg) {
		try {
			System.out.println("putting into container, msg=" + msg);
			Channel ch = Task.getTask().getChannel(channelName);
			Container container = ch.createContainer(resultContainer);
			container.putString(msg);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			throw new RuntimeException(e);
		}		
	}

	private String getArgument() {
		try {
			Channel ch = Task.getTask().getChannel(channelName);
			Container container = ch.getContainer(argumentContainer);
			byte[] cobolArg = container.get();
			String arg;
			try {
				arg = new String(cobolArg, "Cp1047");
			} catch (UnsupportedEncodingException e) {
				arg = e.getMessage();
				e.printStackTrace(System.err);
			}  
			System.out.println("Hello Java received arg=" + arg);
			return arg;
		} catch (Exception e) {
			e.printStackTrace(System.err);
			throw new RuntimeException(e);
		}
	}
}

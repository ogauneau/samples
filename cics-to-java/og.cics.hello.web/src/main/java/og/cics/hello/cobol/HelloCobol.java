package og.cics.hello.cobol;

import java.io.UnsupportedEncodingException;

import com.ibm.cics.jcicsx.BITContainer;
import com.ibm.cics.jcicsx.CHARContainer;
import com.ibm.cics.jcicsx.CICSConditionException;
import com.ibm.cics.jcicsx.CICSContext;
import com.ibm.cics.jcicsx.Channel;
import com.ibm.cics.jcicsx.ChannelProgramLinker;

import og.cics.hello.Log;

public class HelloCobol {

	private ChannelProgramLinker cobolProg;
	private String progName = "OGCOBHW2";
	private String channelName = "HWCHANNEL";
	private String resultContainerCobol = "OUTHWCOB";
	private String resultContainerJava = "OUTHWJAV";
	private String argumentContainer = "INHWCOB";

	public HelloCobol() {
		// Gets the current CICS Context for the environment we're running in
		CICSContext task = CICSContext.getCICSContext();
		// Create a reference to the Program we will invoke and specify the channel
		cobolProg = task.createProgramLinkerWithChannel(progName, channelName);
	}

	/*
	 * execute() calls the COBOL program OGCOBHW2
	 */
	public void execute() {
		try {
			Log.info("Before link to COBOL program " + progName);
			cobolProg.link();
			Log.info("After link to COBOL program " + progName);
		} catch (CICSConditionException e) {
			throw new RuntimeException(e);
		}
	}


	public void setArgument(String userName) {
		try {
			Log.info("Set channel container USERNAME=" + userName);
			Channel ch = cobolProg.getChannel();
			CHARContainer charContainer = ch.getCHARContainer(argumentContainer);
			charContainer.put(userName);			
		} catch (CICSConditionException e) {
			throw new RuntimeException(e);
		}
	}

	public String[] getResult() {
		try {
			String [] results = new String[2];
			
			Channel ch = cobolProg.getChannel();
			BITContainer bitContainer = ch.getBITContainer(resultContainerCobol);
			byte[] bytes = bitContainer.get();
			String res;
			try {
				res = new String(bytes, "Cp1047");
			} catch (UnsupportedEncodingException e) {
				res = e.getMessage();
				e.printStackTrace(System.err);
			}  
			Log.info("Got result=" + res);

			CHARContainer charContainer= ch.getCHARContainer(resultContainerJava);
			String resJava = charContainer.get();
			Log.info("Got result=" + resJava);
			
			results[0]=res;
			results[1]=resJava;
			return results;
		} catch (CICSConditionException e) {
			throw new RuntimeException(e);
		}
	}

}

package og.ims.data.common.gsam;

import com.ibm.ims.dli.DLIException;
import com.ibm.ims.dli.Path;

public interface GSAMRecord {

	/*
	 * copy a GSAMRecord to an IMS DLI Path object
	 */
	public void copyTo(Path dliPath) throws DLIException;

}

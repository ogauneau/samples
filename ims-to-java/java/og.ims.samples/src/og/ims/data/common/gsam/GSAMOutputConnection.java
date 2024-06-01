package og.ims.data.common.gsam;

import com.ibm.ims.dli.DLIException;
import com.ibm.ims.dli.GSAMPCB;
import com.ibm.ims.dli.IMSConnectionSpec;
import com.ibm.ims.dli.IMSConnectionSpecFactory;
import com.ibm.ims.dli.PSB;
import com.ibm.ims.dli.PSBFactory;
import com.ibm.ims.dli.Path;

import og.ims.common.data.Database;
import og.ims.program.ProgramScope;
import og.ims.util.Log;

public class GSAMOutputConnection {
	ProgramScope mypsb;
	PSB imspsb;
	GSAMPCB gsampcb;
	Database gsamDB;

	/*
	 * Create an writable connection to a GSAM database
	 */
	public GSAMOutputConnection(ProgramScope psb,Database gsam) {
		mypsb = psb;
		gsamDB = gsam;
		openConnection();
	}
	
	/*
	 *  Open the IMS PCB of this GSAM database
	 */
	public void openConnection() {
		try {			
			Log.entering(getClass(),"openConnection","psb=" + mypsb.getDatabaseView().getName());
			IMSConnectionSpec cSpec = IMSConnectionSpecFactory.createIMSConnectionSpec();
			//cSpec.setDatastoreName("IF1A");
			cSpec.setDriverType(IMSConnectionSpec.DRIVER_TYPE_2);
			cSpec.setDatabaseName("class://" + mypsb.getDatabaseView().getCanonicalName());
			
			imspsb = PSBFactory.createPSB(cSpec);
			imspsb.allocate();
			Log.trace(getClass(),"openConnection","PSB=" + imspsb.getIMSName() + " allocated");
			
			gsampcb = imspsb.getGSAMPCB(gsamDB.getPcbReference());
			gsampcb.open();

		} catch (DLIException e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * Insert a record with the current GSAM PCB
	 */
	public void insert(GSAMRecord rec) {
		try {
			Log.entering(getClass(),"insert","pcb="+gsampcb.getAIB().getResourceName());
			Path imsGSAMRecord = gsampcb.getPathForInsert();

			// Set values to individual fields in a GSAM record
			rec.copyTo(imsGSAMRecord);

			//TODO: return a RSA object but I don't use it... 
			gsampcb.insert(imsGSAMRecord);

			Log.trace(getClass(),"insert","record inserted");			
			
		} catch (DLIException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * Close the GSAM PCB
	 */
	public void close() {
		if(gsampcb!=null)
			gsampcb.close();		
	}
}

package og.ims.common.data.dli;

import com.ibm.ims.dli.DLIException;
import com.ibm.ims.dli.IMSConnectionSpec;
import com.ibm.ims.dli.IMSConnectionSpecFactory;
import com.ibm.ims.dli.PCB;
import com.ibm.ims.dli.PSB;
import com.ibm.ims.dli.PSBFactory;
import com.ibm.ims.dli.PathSet;
import com.ibm.ims.dli.SSAList;

import og.ims.common.data.Connection;
import og.ims.common.data.Database;
import og.ims.common.data.ResultSet;
import og.ims.common.data.Table;
import og.ims.program.ProgramScope;
import og.ims.util.Log;

public class DLIConnection implements Connection {

	ProgramScope mypsb;
	PSB imspsb;
	PCB imspcb;
	Database imsDB;

	public DLIConnection(ProgramScope arg,Database db) {
		mypsb = arg;
		imsDB = db;
		try {
			openConnection();
		} catch (DLIException e) {
			throw new RuntimeException(e);
		}
	}

	public void openConnection() throws DLIException {
		IMSConnectionSpec connSpec = IMSConnectionSpecFactory.createIMSConnectionSpec();
		// if using IMS catalog
		// ds.setDatabaseName("DFSIVP64");
		// else
		connSpec.setDatabaseName("class://" + mypsb.getDatabaseView().getCanonicalName());
		connSpec.setDriverType(2);

		imspsb = PSBFactory.createPSB(connSpec); 
		imspsb.allocate(); // Allocate and establish a connection to the data
		Log.trace(getClass(),"openConnection","PSB=" + imspsb.getIMSName() + " allocated");

		String pcbRef = imsDB.getPcbReference();
		Log.trace(getClass(),"openConnection","Getting database PCB, name=" + pcbRef);
		imspcb = imspsb.getPCB(pcbRef); 				
	}
	
	public void selectAllFromTable(ResultSet rs) {

		try {
			Log.entering(getClass(),"selectAllFromTable","psb=" + mypsb.getDatabaseView().getName());
			
			DLIResultSet dliResultSet;
			
			if(rs instanceof DLIResultSet)
				dliResultSet = (DLIResultSet) rs;
			else
				throw new IllegalArgumentException("Unsupported ResultSet "+rs.getClass().getCanonicalName()+", expected a DLIResultSet");
			

			String segmentName = dliResultSet.getTable().getSegment().getSegmentNameReference();
			Log.trace(getClass(),"selectAllFromTable","Getting records using segment " + segmentName);
			SSAList ssaList = imspcb.getSSAList(segmentName); // Create an SSA list to use in a DLI call.

			PathSet ps = imspcb.batchRetrieve(ssaList); 
			
			dliResultSet.add(ps);
			Log.trace(getClass(),"selectAllFromTable","batchRetrieve completed");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void closeConnection() {
		try {
			if (imspsb != null) {
				imspsb.deallocate(); 
				Log.trace(getClass(),"closeConnection"," PSB deallocated");
				imspsb.close(); 
				Log.trace(getClass(),"closeConnection"," PSB closed");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public ResultSet createResultSet(Table table) {
		return new DLIResultSet(imsDB,table);
	}

	@Override
	public Database getDatabase() {		
		return imsDB;
	}
}

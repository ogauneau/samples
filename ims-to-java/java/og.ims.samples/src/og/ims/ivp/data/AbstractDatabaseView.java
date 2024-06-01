package og.ims.ivp.data;

import com.ibm.ims.db.DLIDatabaseView;

import og.ims.common.data.Database;

/*
 * Base class for DLIDatabaseView
 * - Same version 2.0
 * - manage the naming of the PCBs in the PSB
 *  1rst argument javaAlias seems to be only needed for API GSAMPCB.getGSAMPCB(javaAlias);
 *  2nd argument needs to be the name of the PCB (PCBNAME) in the PSB (PSBGEN)
 *  => I use javaAlias=PCBNAME by default
 *  3rd argument is the DLI structure. 
*/
public class AbstractDatabaseView extends DLIDatabaseView {

	String psbName;
	Database database;
	
	public AbstractDatabaseView(String psb,Database db) {
		super("2.0",psb,db.getPcbReference(),db.getPcbReference(),db.getSegments());
		database = db;
	}
		
	public void add(Database db) {
			addDatabase(db.getPcbReference(),db.getPcbReference(),db.getSegments());
	}
}

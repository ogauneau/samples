package og.ims.ivp.program;

import og.ims.ivp.data.AbstractDatabaseView;

/*
see DFSIVP64 for PSBGEN description
class is just for the IMS API 
IMSConnectionSpec.setDatabaseName("class://"+AbstractDatabaseView.class)
*/
public class DFSIVP64View extends AbstractDatabaseView{

	/*
	 * Just adding IVPDB1 db because the 2 GSAM databases for IVPDB5 
	 * have no PCBNAME, alias or EXTERNAL names and cannot be used 
	 * by Java as far as I understand
	*/
	public DFSIVP64View() {		
		super("DFSIVP64",DFSIVP64.db1);
	}
}

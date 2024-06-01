package og.ims.ivp.program;

import og.ims.ivp.data.AbstractDatabaseView;

/*
see DFSIVP67 for PSBGEN description
class is just for the IMS API 
IMSConnectionSpec.setDatabaseName("class://"+AbstractDatabaseView.class)
*/
public class DFSIVP67View extends AbstractDatabaseView {

	public DFSIVP67View() {
		super("DFSIVP67",DFSIVP67.db1);
	}
}

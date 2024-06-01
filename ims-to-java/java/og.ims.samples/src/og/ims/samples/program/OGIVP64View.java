package og.ims.samples.program;

import og.ims.ivp.data.AbstractDatabaseView;

/*
	see OGIVP64 for PSBGEN description
    class is just for the IMS API 
    IMSConnectionSpec.setDatabaseName("class://"+AbstractDatabaseView.class)
 */
public class OGIVP64View extends AbstractDatabaseView{

	public OGIVP64View() {		
		super("OGIVP64",OGIVP64.db1);				
		add(OGIVP64.gsam1);
	}
}

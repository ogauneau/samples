package og.ims.ivp.program;

import og.ims.ivp.data.IVPDB1;

/*
 *  PSB used by Java programs accessing database IVPDB1/IVPDB2
 *  in a JBP region
 */

/*
 * DFS.V15RXM0.SDFSISRC(DFSIVP67)
 * 
PHONEAP  PCB TYPE=DB,DBDNAME=IVPDB2,PROCOPT=A,KEYLEN=10
         SENSEG NAME=A1111111,PARENT=0,PROCOPT=AP
         PSBGEN LANG=JAVA,PSBNAME=DFSIVP67
         END             
 */

public class DFSIVP67 extends IVPDB1PSB {

	//IVPDB2 has the same structure as IVPDB1 so reusing IVPDB1
	//
	// PCB name PHONEAP matters otherwise DLI call GHU fails with
	// AIB return code (AIB RETRN): 104 
	// AIB reason code (AIBREASN): 208 

	public DFSIVP67() {
		//no PCBNAME but alias PHONEAP instead
		IVPDB1PSB.db1 = new IVPDB1("PHONEAP");
	}

	@Override
	public Class<?> getDatabaseView() {
		return DFSIVP67View.class;
	}
}

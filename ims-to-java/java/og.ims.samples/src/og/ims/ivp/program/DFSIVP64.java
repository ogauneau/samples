package og.ims.ivp.program;

import og.ims.ivp.data.IVPDB1;

/*
 *  PSB used by COBOL or Java programs accessing database IVPDB1
 *  in a BMP region
 *  
 *  DFS.V15RXM0.SDFSISRC(DFSIVP64)
 *  
 *       PCB    TYPE=DB,DBDNAME=IVPDB1,PROCOPT=A,KEYLEN=10,SB=COND,    X
               PCBNAME=TELEPCB1
         SENSEG NAME=A1111111,PARENT=0,PROCOPT=A
         PCB    TYPE=GSAM,DBDNAME=IVPDB5,PROCOPT=G
         PCB    TYPE=GSAM,DBDNAME=IVPDB5,PROCOPT=L
         PSBGEN LANG=COBOL,PSBNAME=DFSIVP64,CMPAT=YES,OLIC=YES
         END
 */
public class DFSIVP64 extends IVPDB1PSB {

	/*
	 * Just adding IVPDB1 db because the 2 GSAM databases for IVPDB5 
	 * have no PCBNAME, alias or EXTERNAL names and cannot be used 
	 * by Java as far as I understand
	*/
	public DFSIVP64() {
		//no alias but PCBNAME instead
		IVPDB1PSB.db1 = new IVPDB1("TELEPCB1");
	}
	
	@Override
	public Class<?> getDatabaseView() {
		return DFSIVP64View.class;
	}	
}

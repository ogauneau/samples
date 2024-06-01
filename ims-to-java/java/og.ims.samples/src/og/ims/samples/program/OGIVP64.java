package og.ims.samples.program;

import og.ims.data.common.gsam.GSAMOutputConnection;
import og.ims.ivp.program.DFSIVP64;
import og.ims.samples.data.OGDB5;

/*
 *       PCB    TYPE=DB,DBDNAME=IVPDB1,PROCOPT=A,KEYLEN=10,SB=COND,    X
               PCBNAME=TELEPCB1
         SENSEG NAME=A1111111,PARENT=0,PROCOPT=A
         PCB    TYPE=GSAM,DBDNAME=OGDB5,PROCOPT=L,                     X
               PCBNAME=OGPCB2
         PSBGEN LANG=COBOL,PSBNAME=OGIVP64,CMPAT=YES,OLIC=YES
         END
 */
public class OGIVP64 extends DFSIVP64 {

	// non matching PCB name fails with abend (X’104’/X’208’, decimal: 260/520)
	public static OGDB5 gsam1 = new OGDB5("OGPCB2");
	
	public OGIVP64() {
		
	}

	public GSAMOutputConnection getOutputGSAM() {
		return new GSAMOutputConnection(this,gsam1);
	}

	@Override
	public Class<?> getDatabaseView() {
		return OGIVP64View.class;
	}
}

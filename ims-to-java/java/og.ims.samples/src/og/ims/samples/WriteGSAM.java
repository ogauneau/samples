package og.ims.samples;

import og.ims.data.common.gsam.GSAMOutputConnection;
import og.ims.program.Catalog;
import og.ims.program.ProgramScope;
import og.ims.samples.data.OGDB5.Line;
import og.ims.samples.program.OGIVP64;
import og.ims.util.Log;

public class WriteGSAM {
	/*
	 * can be called through COBOL to Java interoperability in BMP region
	 */

	public static int run(String psbClassName) {

		try {
			Log.enableDebug();
			Log.entering(WriteGSAM.class, "run", "psb=" + psbClassName);

			Log.infoIMS(WriteGSAM.class, "run");

			ProgramScope psb = Catalog.getProgramScope(psbClassName);

			if (psb instanceof OGIVP64) {
				OGIVP64 ogivp64PSB = (OGIVP64) psb;

				GSAMOutputConnection gsamDB = ogivp64PSB.getOutputGSAM();

				gsamDB.openConnection();
				Line rec = new Line("Java says hello to the GSAM world");
				gsamDB.insert(rec);
				gsamDB.close();

				Log.trace(WriteGSAM.class, "run", "end");
				return 0;

			}

			throw new IllegalArgumentException("Only PSB including OGIVP64 are supported by this method");

		} catch (Exception e) {
			System.err.println("WriteGSAM.run, Exception happened");
			e.printStackTrace(System.err);
			return 16;
		}
	}

}

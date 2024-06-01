package og.ims.samples;

import java.io.ByteArrayOutputStream;

import com.ibm.ims.dli.EnvironInfo;
import com.ibm.ims.dli.tm.Application;
import com.ibm.ims.dli.tm.ApplicationFactory;

import og.ims.common.data.Record;
import og.ims.common.data.ResultSet;
import og.ims.data.common.gsam.GSAMOutputConnection;
import og.ims.ivp.data.PhoneBookConnection;
import og.ims.ivp.program.IVPDB1PSB;
import og.ims.program.Catalog;
import og.ims.program.ProgramScope;
import og.ims.samples.data.OGDB5.Line;
import og.ims.samples.program.OGIVP64;
import og.ims.util.Log;

public class ExportIVPDB1 {

	private static int ERROR_RC = 16;
	private static int OK_RC = 0;

	/*
	 * main() is called when run in JBP region
	 */
	public static void main(String[] paramArrayOfString) throws Exception {
		System.out.println("nb arg" + paramArrayOfString.length);
		for (int i = 0; i < paramArrayOfString.length; i++) {
			System.out.println("arg" + i + "=" + paramArrayOfString[i]);
		}
		Application app = ApplicationFactory.createApplication();
		EnvironInfo env = app.inqyEnviron();
		String psbName = env.getPSBName();

		int rc = ExportIVPDB1.exportAllToConsole(psbName, "DLI","INFO");
		if (rc < 0)
			// JBP will fail with an abend 101 if Java return code isn't 0
			System.exit(rc);
	}

	/*
	 * can be called through JNI
	 */
	public static int exportAllToConsole(String psbClassName, String driver, String logLevel) {
		try {
			if(logLevel.equalsIgnoreCase("DEBUG"))
				Log.enableDebug();
			else
				Log.enableInfo();
			Log.entering(ExportIVPDB1.class, "exportAllToConsole", "psb=" + psbClassName);
			Log.infoIMS(ExportIVPDB1.class, "exportAllToConsole");

			int count = 0;
			ProgramScope psb = Catalog.getProgramScope(psbClassName);
			if (psb instanceof IVPDB1PSB) {
				IVPDB1PSB ivpdb1psb = (IVPDB1PSB) psb;

				GSAMOutputConnection gsamDB = null;

				// if PSB OGIVP64 then export to the GSAM db
				if (ivpdb1psb instanceof OGIVP64) {
					OGIVP64 ogivp64PSB = (OGIVP64) ivpdb1psb;

					gsamDB = ogivp64PSB.getOutputGSAM();
					gsamDB.openConnection();
				}

				PhoneBookConnection conn = ivpdb1psb.getPhonebookConnection(driver.toUpperCase().trim());

				ResultSet rs = conn.selectAllFromPerson();
				while (rs.hasNext()) {
					Record rec = rs.next();
					if (gsamDB != null) {
						ByteArrayOutputStream buf = new ByteArrayOutputStream(80);
						rec.export(buf);
						Line gsamRec = new Line(buf.toString());
						gsamDB.insert(gsamRec);

					} else {

						rec.export(System.out);
					}
					count++;
				}
				rs.close();
				conn.closeConnection();
				if (gsamDB != null)
					gsamDB.close();

				Log.info(ExportIVPDB1.class, "exportAllToConsole", "Exported records, count=" + count);
				return OK_RC;
			}

			throw new IllegalArgumentException("Only PSB including OGIVP64 are supported by this method");

		} catch (Exception e) {
			e.printStackTrace(System.err);
			return ERROR_RC;
		}
	}

}

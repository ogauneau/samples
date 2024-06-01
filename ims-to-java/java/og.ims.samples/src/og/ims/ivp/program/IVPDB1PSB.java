package og.ims.ivp.program;

import og.ims.common.data.Connection;
import og.ims.common.data.dli.DLIConnection;
import og.ims.common.data.sql.SQLConnection;
import og.ims.ivp.data.IVPDB1;
import og.ims.ivp.data.PhoneBookConnection;
import og.ims.program.ProgramScope;
import og.ims.util.Log;

/*
 * Base class for PSB using databases like IVPDB1 but with
 * different PCB names.
 * 
 * For example DFSIVP64 or DFSIVP67
 *          
 */

public abstract class IVPDB1PSB implements ProgramScope {

	public static IVPDB1 db1;

	/*
	 * IMS segment in IVPDB2 is called PhoneBook 
	 * and IVPDB2 has the same structure as IVPDB1
	 * So calling the IVPDB1 database PhoneBook as well.
	 */
	public PhoneBookConnection getPhonebookConnection(String driverType) {
		Log.info(getClass(), "getPhonebookDB", "using driver=" + driverType);
		switch (driverType) {
		case Connection.DLI_DIVER:
			return new PhoneBookConnection(new DLIConnection(this,db1));

		case Connection.SQL_DIVER:
			return new PhoneBookConnection(new SQLConnection(this,db1));

		default:
			throw new IllegalArgumentException("Unsupported driver " + driverType + ". Only DLI and SQL are supported");
		}
	}
}

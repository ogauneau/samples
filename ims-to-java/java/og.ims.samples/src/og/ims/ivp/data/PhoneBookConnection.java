package og.ims.ivp.data;

import og.ims.common.data.Connection;
import og.ims.common.data.ResultSet;
import og.ims.ivp.data.IVPDB1.PersonTable;
import og.ims.util.Log;

/*
 * Connection to perform actions against database IVPDB1 or IVPDB2
 */
public class PhoneBookConnection {

	private IVPDB1 ivpdb1;
	private Connection conn;
	
	public PhoneBookConnection(Connection argConn) {
		conn = argConn;
		//connection to IVPDB1 is supported
		ivpdb1 = (IVPDB1) conn.getDatabase();
	}
	
	public ResultSet selectAllFromPerson() {
		Log.entering(getClass(),"selectAllFromPerson","conn="+conn);
		PersonTable tbl = ivpdb1.getPersonTable();
		ResultSet rs = conn.createResultSet(tbl);		
		conn.selectAllFromTable(rs);
		
		return rs;
	}

	public void closeConnection() {
		conn.closeConnection();		
	}
}

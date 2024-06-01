package og.ims.common.data.sql;

import java.sql.SQLException;
import java.sql.Statement;

import com.ibm.ims.jdbc.IMSDataSource;

import og.ims.common.data.Connection;
import og.ims.common.data.Database;
import og.ims.common.data.ResultSet;
import og.ims.common.data.Table;
import og.ims.program.ProgramScope;
import og.ims.util.Log;

public class SQLConnection implements Connection {

	ProgramScope mypsb;
	java.sql.Connection javaconn;
	Database imsDB;

	public SQLConnection(ProgramScope arg, Database db) {
		mypsb = arg;
		imsDB=db;
		try {
			openConnection();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void openConnection() throws SQLException {
		Log.entering(getClass(), "openConnection", "");
		// Setup your DataSource
		IMSDataSource ds = new IMSDataSource();
		// if using IMS catalog
		// ds.setDatabaseName("DFSIVP64");
		// else
		String dbView = mypsb.getDatabaseView().getCanonicalName();
		ds.setDatabaseName("class://" + dbView);
		ds.setDriverType(2);

		// Establish a connection using the data source
		javaconn = ds.getConnection();
		javaconn.setAutoCommit(false);
		Log.trace(getClass(), "openConnection", "Connection opened to "+dbView);

	}
	@Override
	public void closeConnection() {
		if (javaconn != null)
			try {
				javaconn.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}

	}

	@Override
	public void selectAllFromTable(ResultSet rs) {
		try {
			Log.entering(getClass(), "selectAllFromTable", "");

			SQLResultSet sqlResultSet;

			if (rs instanceof SQLResultSet)
				sqlResultSet = (SQLResultSet) rs;
			else
				throw new IllegalArgumentException("Argument is not DLIResultSet");


			Statement st = javaconn.createStatement();
			String schema = imsDB.getPcbReference();
			String tbl = sqlResultSet.getTable().getSegment().getSegmentNameReference();
			String sqlStmt="SELECT * FROM "+schema+"."+tbl;
			Log.trace(getClass(), "selectAllFromTable", "SQL stmt="+sqlStmt);
			java.sql.ResultSet javars = st.executeQuery(sqlStmt);

			sqlResultSet.add(javars);

			Log.trace(getClass(), "selectAllFromTable", "executeQuery completed");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	
	@Override
	public ResultSet createResultSet(Table table) {
		return new SQLResultSet(imsDB,table);
	}

	@Override
	public Database getDatabase() {
		return imsDB;
	}

}

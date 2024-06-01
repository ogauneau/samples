package og.ims.common.data.sql;

import java.sql.SQLException;

import og.ims.common.data.Database;
import og.ims.common.data.Record;
import og.ims.common.data.RecordFactory;
import og.ims.common.data.ResultSet;
import og.ims.common.data.Table;

public class SQLResultSet implements ResultSet {

	private java.sql.ResultSet javaResulteSet;
	private RecordFactory recordFactory;
	private Database database;
	private Table table;

	public SQLResultSet(Database db, Table tbl) {
		database = db;
		table=tbl;
	}

	@Override
	public void close() {
		try {
			javaResulteSet.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean hasNext() {
		try {
			return javaResulteSet.next();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Record next() {
		if(javaResulteSet==null)
			return null;
		if(recordFactory==null)
			recordFactory = table.getRecordFactory(javaResulteSet);
		return recordFactory.create(javaResulteSet);
	}

	@Override
	public Database getDatabase() {
		return database;
	}

	public void add(java.sql.ResultSet javars) {
		javaResulteSet = javars;
	}

	@Override
	public Table getTable() {
		return table;
	}


}

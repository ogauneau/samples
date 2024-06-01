package og.ims.common.data.dli;

import com.ibm.ims.dli.DLIException;
import com.ibm.ims.dli.Path;
import com.ibm.ims.dli.PathSet;

import og.ims.common.data.Database;
import og.ims.common.data.Record;
import og.ims.common.data.RecordFactory;
import og.ims.common.data.ResultSet;
import og.ims.common.data.Table;

public class DLIResultSet implements ResultSet {

	PathSet dliPathSet;
	Database database;
	Table table;
	RecordFactory recordFactory;

	public DLIResultSet(Database db, Table tbl) {
		database = db;
		table = tbl;
		// recordFactory = factory;
	}

	public void add(PathSet resultset) {
		dliPathSet = resultset;
	}

	public void close() {
	
	}

	public boolean hasNext() {
		try {
			if (dliPathSet == null)
				return false;
			return dliPathSet.hasNext();
		} catch (DLIException e) {
			throw new RuntimeException(e);
		}
	}

	public Record next() {
		try {
			if (dliPathSet == null)
				return null;
			Path rec = dliPathSet.next();
			if (recordFactory == null)
				recordFactory = table.getRecordFactory(rec);
			return recordFactory.create(rec);
		} catch (DLIException e) {
			throw new RuntimeException(e);
		}
	}

	public Database getDatabase() {
		return database;
	}

	@Override
	public Table getTable() {		
		return table;
	}
}

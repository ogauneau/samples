package og.ims.common.data;

import com.ibm.ims.db.DLISegment;

public interface Table {

	public RecordFactory getRecordFactory(Object obj);
	public DLISegment getSegment();
	public Database getDatabase();
	
}

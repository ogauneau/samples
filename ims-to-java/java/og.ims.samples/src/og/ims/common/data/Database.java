package og.ims.common.data;

import com.ibm.ims.db.DLISegmentInfo;

public interface Database {


	public DLISegmentInfo[] getSegments();
	public String getPcbReference();
}

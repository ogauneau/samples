package og.ims.samples.data;

import com.ibm.ims.base.DLITypeInfo;
import com.ibm.ims.db.DLIDatabaseView;
import com.ibm.ims.db.DLISegmentInfo;
import com.ibm.ims.dli.DLIException;
import com.ibm.ims.dli.Path;

import og.ims.common.data.Database;
import og.ims.data.common.gsam.GSAMRecord;

/*
 * DBDGEN
 *    
  DBD    NAME=OGDB5,ACCESS=(GSAM,BSAM)
  DATASET DD1=OGIVD5O,RECFM=F,RECORD=80
  DBDGEN
  FINISH
  END
 */
public class OGDB5 implements Database {

	/*
	 * PCB seems unique per database across all PSBs ie 2 PSBs need to have the same
	 * PCB name for the same database so putting it as attribute of the database
	 */
	private String pcbNameInPSB;

	// table and column names can be defined at the Java level
	private static final String columnName1 = "record";
	private static final String tableName = "GSAMTBL1";

	public OGDB5(String pcbName) {
		pcbNameInPSB = pcbName;
	}

	/*
	 * DBD NAME=IVPDB5,ACCESS=(GSAM,BSAM) DATASET
	 * DD1=DFSIVD5I,DD2=DFSIVD5O,RECFM=F,RECORD=80
	 *
	 */
	@Override
	public DLISegmentInfo[] getSegments() {
		/*
		 * GSAM record structure
		 */
		DLITypeInfo[] Columns = { new DLITypeInfo(columnName1, DLITypeInfo.CHAR, 1, 80) };

		com.ibm.ims.db.GSAMRecord PCBGSAMRecord = new com.ibm.ims.db.GSAMRecord(tableName, Columns, 80);

		DLISegmentInfo[] segments = { new DLISegmentInfo(PCBGSAMRecord, DLIDatabaseView.ROOT) };

		return segments;
	}

	@Override
	public String getPcbReference() {
		return pcbNameInPSB;
	}

	/*
	 *  Record that can be inserted in this GSAM
	 */
	public static class Line implements GSAMRecord {

		String newLine;

		public Line(String message) {
			newLine = message;
		}

		@Override
		public void copyTo(Path dliPath) throws DLIException {
			dliPath.setString(columnName1, newLine);
		}
	}
}

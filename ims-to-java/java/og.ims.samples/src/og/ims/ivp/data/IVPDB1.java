package og.ims.ivp.data;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ibm.ims.base.DLITypeInfo;
import com.ibm.ims.db.DLISegment;
import com.ibm.ims.db.DLISegmentInfo;
import com.ibm.ims.dli.DLIException;
import com.ibm.ims.dli.Path;

import og.ims.common.data.Database;
import og.ims.common.data.Record;
import og.ims.common.data.RecordFactory;
import og.ims.common.data.Table;

/*
 * IVPDB1 and IVPDB2 have the same structure 
 * IVPDB2 has external names for Java but I use the segment name instead.
 * 
 * DFS.V15RXM0.SDFSISRC(DFSIVD1)
 *    
  DBD    NAME=IVPDB1,ACCESS=(HIDAM,OSAM)
  DATASET DD1=DFSIVD1,DEVICE=3380,SIZE=2048
  SEGM   NAME=A1111111,PARENT=0,BYTES=40,RULES=(LLV,LAST),PTR=(TB,CTR)
  FIELD  NAME=(A1111111,SEQ,U),BYTES=010,START=00001,TYPE=C
  LCHILD NAME=(A1,IVPDB1I),POINTER=INDX,RULES=LAST
  DBDGEN
  FINISH   

 * DFS.V15RXM0.SDFSISRC(DFSIVD1I)
 *    

  DBD    NAME=IVPDB1I,ACCESS=(INDEX,VSAM,PROT)
  DATASET DD1=DFSIVD1I,DEVICE=3380,SIZE=2048
  SEGM   NAME=A1,PARENT=0,BYTES=10
  FIELD  NAME=(A1,SEQ,U),BYTES=010,START=00001,TYPE=C
  LCHILD NAME=(A1111111,IVPDB1),INDEX=A1111111
  DBDGEN
  FINISH
  END    

 * DFS.V15RXM0.SDFSISRC(DFSIVD2)
 *    

      DBD      NAME=IVPDB2,                                            C
               ENCODING=Cp1047,                                        C
               ACCESS=HDAM,                                            C
               RMNAME=(DFSHDC40,40,100)
   DATASET  DD1=DFSIVD2,                                               C
               DEVICE=3380,                                            C
               SIZE=2048

   SEGM     NAME=A1111111,                                             C
               EXTERNALNAME=PHONEBOOK,                                 C
               PARENT=0,                                               C
               BYTES=40,                                               C
               RULES=(LLL,LAST)

   FIELD    NAME=(A1111111,SEQ,U),                                     C
               EXTERNALNAME=LASTNAME,                                  C
               BYTES=10,                                               C
               START=1,                                                C
               TYPE=C,                                                 C
               DATATYPE=CHAR
 */

public class IVPDB1 implements Database {

	//all instances share the same PCB and structure
	public static String pcbRef;
	private static PersonTable personTable;

	public IVPDB1(String pcbName) {
		pcbRef = pcbName;
		personTable = new PersonTable(this);
	}
	
	public class PersonRecord implements Record {
		private String lastName;
		private String firstName;
		private String extension;
		private String zipCode;

		public PersonRecord(ResultSet rs) throws SQLException {
			lastName = rs.getString("LastName");
			firstName = rs.getString("FirstName");
			extension = rs.getString("Extension");
			zipCode = rs.getString("ZipCode");
		}

		public PersonRecord(Path rec) {
			try {
				lastName = rec.getString("LastName").trim();
				firstName = rec.getString("FirstName").trim();
				extension = rec.getString("Extension").trim();
				zipCode = rec.getString("ZipCode").trim();
			} catch (DLIException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void export(OutputStream ostr) throws IOException {
			String str = "Last=" + lastName + ", first=" + firstName + ", extension=" + extension + ", zipCode="
					+ zipCode + "\n";
			ostr.write(str.getBytes());
		}
	}

	public class PersonTable implements RecordFactory, Table {

		private String segTableName = "PERSON";
		private DLISegmentInfo tableInfo;
		private DLISegment tableIndex;
		private Database database;

		private DLITypeInfo[] personTable = { new DLITypeInfo("LastName", 3, 1, 10, "A1111111", 2201),
				new DLITypeInfo("FirstName", 3, 11, 10), new DLITypeInfo("Extension", 3, 21, 10),
				new DLITypeInfo("ZipCode", 3, 31, 7) };

		public PersonTable(IVPDB1 db) {
			tableIndex = new DLISegment(segTableName, "A1111111", personTable, 40);
			tableInfo = new DLISegmentInfo(tableIndex, -1);
			database = db;
		}

		@Override
		public RecordFactory getRecordFactory(Object obj) {
			if ((obj instanceof Path) || (obj instanceof java.sql.ResultSet))
				return this;
			throw new IllegalArgumentException("Unsupported record type: " + obj.getClass().getCanonicalName());
		}


		@Override
		public Record create(Object rec) {
			if (rec instanceof Path)
				return new PersonRecord((Path) rec);
			else if (rec instanceof java.sql.ResultSet)
				try {
					return new PersonRecord((java.sql.ResultSet) rec);
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			throw new IllegalArgumentException("Unsupported record type: " + rec.getClass().getCanonicalName());
		}

		@Override
		public DLISegment getSegment() {
			return tableIndex;
		}

		@Override
		public Database getDatabase() {
			return database;
		}

	}

	public PersonTable getPersonTable() {
		return personTable;
	}

	public IVPDB1() {
		personTable = new PersonTable(this);
	}

	@Override
	public DLISegmentInfo[] getSegments() {
		DLISegmentInfo[] tables = { personTable.tableInfo };

		return tables;
	}

	@Override
	public String getPcbReference() {
		return pcbRef;
	}

}

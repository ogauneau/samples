package og.ims.common.data;

/*
 * Action that can be performed against an IMS database
 * - selectAllFromTable
 * 
 * Also provide a ResultSet that can hold records of a particular table
 * 
 */
public interface Connection {

	static final String DLI_DIVER="DLI";
	static final String SQL_DIVER="SQL";
	
	public void closeConnection();
	public void selectAllFromTable(ResultSet rs);
	public ResultSet createResultSet(Table table);
	public Database getDatabase();

}

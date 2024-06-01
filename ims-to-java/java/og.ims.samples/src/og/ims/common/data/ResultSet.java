package og.ims.common.data;

public interface ResultSet {

	public void close();

	public boolean hasNext();

	public Record next();
	
	public Database getDatabase();
	
	public Table getTable();

}

package og.ims.common.data;

import java.io.IOException;
import java.io.OutputStream;

public interface Record {
	
	public void export(OutputStream ostr) throws IOException;

}

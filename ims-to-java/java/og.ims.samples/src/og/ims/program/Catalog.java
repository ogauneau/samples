package og.ims.program;

import og.ims.ivp.program.DFSIVP64;
import og.ims.ivp.program.DFSIVP67;
import og.ims.samples.program.OGIVP64;

public class Catalog {

	public static ProgramScope getProgramScope(String psbClassName) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		switch (psbClassName.toUpperCase().trim()) {
		case "DFSIVP64":
			return new DFSIVP64();
		case "DFSIVP67":
			return new DFSIVP67();
		case "OGIVP64":
			return new OGIVP64();			
		default:
			break;
		}
		throw new IllegalArgumentException("Unknown PSB "+psbClassName);
	}

}

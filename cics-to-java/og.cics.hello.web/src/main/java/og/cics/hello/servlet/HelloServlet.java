package og.cics.hello.servlet;

import java.io.IOException;
import java.text.MessageFormat;

import jakarta.security.enterprise.authentication.mechanism.http.BasicAuthenticationMechanismDefinition;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.HttpConstraint;
import jakarta.servlet.annotation.ServletSecurity;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import og.cics.hello.cobol.HelloCobol;

/**
 * Servlet implementation class HelloServlet
 * 
 * doGet() gets called from a Web browser 
 * and in turn calls HelloCobol.execute()
 * 
 */
@BasicAuthenticationMechanismDefinition 
@WebServlet("/hello") 
@ServletSecurity(@HttpConstraint(rolesAllowed = "cicsAllAuthenticated")) 
public class HelloServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HelloServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	@Override
	protected void doGet(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse resp)
			throws jakarta.servlet.ServletException, IOException {
		run(req, resp);
	}


	protected void run(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HelloCobol prog = new HelloCobol();
		prog.setArgument(request.getParameter("username"));
		prog.execute();
		String[] results = prog.getResult();

		String webPage = MessageFormat.format(getWebPage(), (Object[]) results);
		response.getOutputStream().println(webPage.toString());
	}
	
	private String getWebPage() {
		StringBuffer page = new StringBuffer();
		page.append("<div ><TABLE border=\"0\"><TR><TD ><img src=\"images/cics.png\" alt=\"CICS TS\" width=\"100\" height=\"100\"></TD>");
		page.append("<TD width=\"450\"><H1>Welcome to Java and COBOL interoperability in CICS !</H1></TD></TR>");
		page.append("</TABLE><BR><TABLE border=\"0\"><TR>");
		page.append("<TD align=\"center\"><img src=\"images/bluearrow_v.svg\"></TD>");
		page.append("<TD colspan=\"2\">&nbsp</TD></TR><TR><TD>Web project in my bundle</TD>");
		page.append("<TD width=\"20\"><img src=\"images/greenarrow_h.svg\"></TD>");
		page.append("<TD>Java Servlet says hello </TD>");
		page.append("</TR><TR><TD align=\"center\"><img src=\"images/bluearrow_v.svg\"></TD>");
		page.append("<TD colspan=\"2\">&nbsp</TD></TR><TR><TD>Traditional COBOL Program</TD>");
		page.append("<TD width=\"20\"><img src=\"images/greenarrow_h.svg\"></TD>");
		page.append("<TD >{0}</TD></TR><TR>");
		page.append("<TD align=\"center\"><img src=\"images/bluearrow_v.svg\"></TD>");
		page.append("<TD colspan=\"2\">&nbsp</TD></TR><TR><TD>Java project in my bundle</TD>");
		page.append("<TD width=\"20\"><img src=\"images/greenarrow_h.svg\"></TD>");
		page.append("<TD>{1}</TD></TR></TABLE></div>");

		return page.toString();
	}
	
}

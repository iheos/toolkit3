package gov.nist.hit.ds.repository.simple.search;

import gov.nist.hit.ds.repository.api.Asset;
import gov.nist.hit.ds.repository.api.RepositoryException;
import gov.nist.hit.ds.repository.api.RepositorySource.Access;
import gov.nist.hit.ds.repository.simple.Configuration;
import gov.nist.hit.ds.repository.simple.SimpleId;
import gov.nist.hit.ds.repository.simple.SimpleRepository;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Sunil.Bhaskarla 
 */
public class DownloadAssetServlet extends HttpServlet {

	private static final String USAGE_STR = "Usage: ?reposSrc=<Resident|External>&reposId=value&assetId=value";

	private static final long serialVersionUID = -2233759886953787817L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException  {
		
		String reposSrc = request.getParameter("reposSrc");
		String reposId = request.getParameter("reposId");
		String assetId = request.getParameter("assetId");
		Access acs = null; 
		
		SimpleRepository repos = null;
		try {
			repos = new SimpleRepository(new SimpleId(reposId));
			if (reposSrc==null) {
				throw new ServletException("Missing required reposSrc. " +USAGE_STR);
			} else {
				acs = getAccessType(reposSrc);
				if (acs==null) {
					throw new ServletException("Invalid reposSrc. " + USAGE_STR);
				}
			}			
			repos.setSource(Configuration.getRepositorySrc(acs));			
		} catch (RepositoryException e) {			
			throw new ServletException(e.toString());
		}

		
		if (assetId!=null && reposId!=null) {
			try {
				
				Asset a = repos.getAsset(new SimpleId(assetId));
				if (a!=null) {
					  response.setHeader("Cache-Control", "no-cache");
					  response.setDateHeader("Expires", 0);
					  response.setHeader("Pragma", "no-cache");
					  response.setDateHeader("Max-Age", 0);
					  
					  response.setHeader("Content-Disposition", "attachment;filename=\""+ a.getId().getIdString() + "." + a.getContentExtension()[2] + "\"");
					  if (a.getMimeType()!=null) {
						  response.setContentType(a.getMimeType());
					  } else {
						  response.setContentType("application/xml");
					  }
					  
					  byte[] content = a.getContent();
					  if (content==null) {
						  throw new ServletException("The requested content file does not exist or it could not be loaded.");  
					  }
					  
					  
					  OutputStream os = response.getOutputStream();

					  os.write(content);
					  
					  os.close();

				}				
			} catch (RepositoryException re) {
				throw new ServletException("Error: " + re.toString());
			}
			
			
		
		} else {
			throw new ServletException(USAGE_STR);			
		}
	}
	
	private Access getAccessType(String reposSrc) throws RepositoryException {
		for (Access a : Access.values()) {
			if (a.toString().toLowerCase().contains((reposSrc.toLowerCase()))) {
				return a;
			}
		}
		throw new RepositoryException("Access type "+ reposSrc +" not found");
	}

	
	
}

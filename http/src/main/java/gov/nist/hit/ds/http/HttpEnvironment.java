package gov.nist.hit.ds.http;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

public class HttpEnvironment {
	private HttpServletResponse response = null;
	OutputStream os = null;
	
	public HttpServletResponse getResponse() {
		return response;
	}
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	public OutputStream getOutputStream() throws Exception {
		if (os == null) {
			if (response == null)
				throw new Exception("SoapEnvironment: Cannot retrieve outputstream - no HttpServletResponse object is registered");
			os = response.getOutputStream();
		}
		return os;
	}


}

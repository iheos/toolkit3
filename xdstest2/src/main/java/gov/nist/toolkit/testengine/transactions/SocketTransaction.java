package gov.nist.toolkit.testengine.transactions;

import gov.nist.hit.ds.xdsException.MetadataException;
import gov.nist.hit.ds.xdsException.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketTransaction extends BasicTransaction {
    static final Logger logger = Logger.getLogger(SocketTransaction.class);
    String host = null;
    int port = 0;
    Socket socket = null;
    byte[] input;
    byte[] output;

    public SocketTransaction() {
    }

    @Override
    protected void run(OMElement request) throws Exception {
        if (host == null) {
            s_ctx.set_error("Host parameter not set for SocketTransaction");
            return;
        }
        if (port == 0) {
            s_ctx.set_error("Port parameter not set for SocketTransaction");
            return;
        }
        logger.debug("Opening socket to " + host + ":" + port + "...");
        try {
            socket = new Socket(host, port);
        } catch (UnknownHostException e) {
            s_ctx.set_error("Error connecting to " + host + ":" + port + " - " + e.getMessage());
            return;
        } catch (IOException e) {
            s_ctx.set_error("Error connecting to " + host + ":" + port + " - " + e.getMessage());
            return;
        }
        logger.debug("\t...Success");



    }

    void send(byte[] cmd) throws IOException {
        OutputStream os;
        os = socket.getOutputStream();
        os.write(cmd);
        os.flush();	}

    byte[] rcv() throws IOException {
        byte[] msg = null;
        InputStream is = socket.getInputStream();
        return msg;
    }

    @Override
    protected void parseInstruction(OMElement part)
            throws XdsInternalException, MetadataException {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getRequestAction() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getBasicTransactionName() {
        // TODO Auto-generated method stub
        return "Socket";
    }

}
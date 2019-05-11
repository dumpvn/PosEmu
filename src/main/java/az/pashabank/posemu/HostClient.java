package az.pashabank.posemu;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

class HostClient {
    
    private static HostClient client;
    
    private String hostIpAddress;
    private int hostPort;
    private int hostTimeout;
    
    private Socket sock;
    
    private HostClient () { }
    
    static HostClient getInstance () throws Exception {
        if (client == null) {
            client = new HostClient(); 
        }
        Database db = Database.getInstance();
        client.hostIpAddress = db.getParameter(Constants.PARAM_ACQ_HOST_IP_ADDRESS);
        client.hostPort = db.getIntParameter(Constants.PARAM_ACQ_HOST_PORT);
        client.hostTimeout = db.getIntParameter(Constants.PARAM_ACQ_HOST_TIMEOUT);
        return client;
    }
    
    void connect () throws IOException, UnknownHostException {
        this.sock = new Socket(this.hostIpAddress, this.hostPort);
        this.sock.setSoTimeout(this.hostTimeout);
        
    }
    
    void disconnect () throws IOException {
        if (this.sock != null) {
            this.sock.close();
        }
    }
    
    String send (String req) throws IOException {
        String res = null;
        try (DataOutputStream out = new DataOutputStream(this.sock.getOutputStream());
                DataInputStream in = new DataInputStream(this.sock.getInputStream())) {
            out.writeUTF(req);
            res = in.readUTF();
        } catch (IOException e) {
            throw new IOException(e);
        }
        return res;
    }
    
    public static void main(String[] args) throws Exception {
        String msg = "1200701405d820c00200165315357289693858000000000000001111160928192518161251010151334420020005166160928000375315357289693858D16122011985704200000POS0020";
        HostClient c = HostClient.getInstance();
        c.connect();
        String response = c.send(msg);
        c.disconnect();
    }
    
}

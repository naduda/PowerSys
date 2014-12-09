package pr;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import pr.iec104.APDU;

public class Client {
	@SuppressWarnings("null")
	public static void main(String[] args) {
		try (Socket clientSocket = new Socket("10.1.3.3", 4001);
				OutputStream outbound = clientSocket.getOutputStream();
				InputStream is = clientSocket.getInputStream();) {
			
			System.out.println("Client: " + clientSocket);
			
			byte[] startDT = new byte[] { (byte) 0x68, (byte)0x04, (byte)0x07, (byte)0x00, (byte)0x00, (byte)0x00};
			//byte[] stoptDT = new byte[] { (byte) 0x68, (byte)0x04, (byte)0x13, (byte)0x00, (byte)0x00, (byte)0x00};
			//byte[] testFR  = new byte[] { (byte) 0x68, (byte)0x04, (byte)0x43, (byte)0x00, (byte)0x00, (byte)0x00};
			
			outbound.write(startDT);
			System.out.println("S ==> startDT");
			
			int read = 0;
			byte[] bytesAPDU = null;
			int i = 0;
			while (clientSocket.isConnected()) {
				if (is.available() > 0) {
					bytesAPDU = new byte[is.available()];
				}
				while (is.available() > 0) {
					read = is.read();
					bytesAPDU[i++] = (byte) read;
					
					String r = Integer.toHexString(read) + "";
					r = r.length() < 2 ? "0" + r : r;
					if (is.available() > 0) {
						r = r + " - ";
					} else {
						r = r + "\n";
					}
					System.out.print(r.toUpperCase());
				}
				i = 0;
				
				if (bytesAPDU != null) {
					APDU apdu = new APDU(bytesAPDU);
					if (apdu.getApci().getType() == 1) {
						byte[] conf  = new byte[] { (byte) 0x68, (byte)0x04, (byte)0x01, (byte)0x00, 
								apdu.getApci().getField3(), apdu.getApci().getField4()};
						outbound.write(conf);
					}
					bytesAPDU = null;
				}
				
				Thread.sleep(100);
					
//				if (isRequest && (System.currentTimeMillis() - c) > 10000) {
//					outbound.write(testFR);
//					System.out.println("S ==> testFR");
//					isRequest = false;
//					c = System.currentTimeMillis();
//				} else if ((System.currentTimeMillis() - c) > 10000) {
//					outbound.write(startDT);
//					System.out.println("S ==> startDT");
//					c = System.currentTimeMillis();
//				}
				
			}
		} catch (IOException | InterruptedException e) {
			System.out.println("Exception: " + e);
		}
	}
}


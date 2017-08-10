/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Network;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author arman
 */
public class DNS {

    synchronized public String resolveHostIpAddress(String hostName) throws NoDNSRespondException {
        String obtainedIPAddress = null;
        String nameServer = "8.8.8.8";

        System.out.println("Nameserver: " + nameServer);
        System.out.println("Request: " + hostName);
        String dnsID = "db42";
        String dnsHeader = "01000001000000000000";
        String dnsFooter = "0000010001";
        String dnsMessage = "";
        int lastDotExploredIndex = 0;
        hostName += ".-";
        while (true) {
            int index = hostName.indexOf('.', lastDotExploredIndex);
            if (index == -1) {
                break;
            }
            String m = hostName.substring(lastDotExploredIndex, index);
            lastDotExploredIndex = index + 1;
            dnsMessage += Integer.toHexString(m.length()).length() == 1 ? "0" + Integer.toHexString(m.length()) : Integer.toHexString(m.length());

            StringBuilder sb = new StringBuilder();
            for (byte b : m.getBytes()) {
                sb.append(String.format("%02X", b));
            }
            dnsMessage += sb.toString();
        }
        hostName = hostName.substring(0, hostName.length() - 2);
        String message = dnsID + dnsHeader + dnsMessage + dnsFooter;
        byte[] query = DatatypeConverter.parseHexBinary(message);

        try {

            boolean received = false;
            int count = 0;
            DatagramSocket socket = new DatagramSocket();
            System.out.println("loc port:" + socket.getLocalPort());
            socket.setSoTimeout(7000);

            byte[] dnsResponse = null;
            try {
                while (!received) {
                    try {
                        sendQuery(query, socket, InetAddress.getByName(nameServer));
                        dnsResponse = getResponse(socket);
                        received = true;
                    } catch (InterruptedIOException ex) {
                        if (count++ < 3) {
                            System.out.println("resend Query to DNS..");
                        } else {
                            throw new NoDNSRespondException("No response received from nameserver");
                        }
                    }
                }
            } finally {
                socket.close();
            }

            if (received) {
                byte[] iplen = Arrays.copyOfRange(dnsResponse, dnsResponse.length - 6, dnsResponse.length - 4);
                if (4 * iplen[0] + iplen[1] == 4) {
                    obtainedIPAddress = "";
                    byte[] ipByte = Arrays.copyOfRange(dnsResponse, dnsResponse.length - 4, dnsResponse.length);
                    for (int i = 0; i < 4; i++) {
                        int a = ipByte[i] & 0xFF;
                        obtainedIPAddress += (a + ".");
                    }
                    obtainedIPAddress = obtainedIPAddress.substring(0, obtainedIPAddress.length() - 1);
                    System.out.println("ip" + obtainedIPAddress + "for " + hostName + " resolved from DNS");
                } else {
                    obtainedIPAddress = null;
                }
            }

        } catch (SocketException ex) {
            System.out.println(ex);
            obtainedIPAddress = null;
        } catch (IOException ex) {
            System.out.println(ex);
            obtainedIPAddress = null;
        }

        return obtainedIPAddress;
    }

    private void sendQuery(byte[] query, DatagramSocket socket, InetAddress nameServer) throws IOException {
        DatagramPacket packet = new DatagramPacket(query, query.length, nameServer, 53);
        socket.send(packet);
    }

    private byte[] getResponse(DatagramSocket socket) throws IOException {
        byte[] buffer = new byte[512];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        int len = packet.getLength();
        buffer = Arrays.copyOfRange(buffer, 0, len);
        return buffer;
    }
}

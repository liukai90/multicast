package unicast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client implements Runnable{

    public void run(){
        try{
            DatagramSocket client = new DatagramSocket();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            InetAddress inetAddress = InetAddress.getByName("10.4.0.205");
            int port = 8088;
            String line = null;
            while ((line=bufferedReader.readLine()) != null){
                byte [] buf = line.getBytes();
                DatagramPacket datagramPacket = new DatagramPacket(buf,buf.length,inetAddress,port);
                client.send(datagramPacket);
            }



        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

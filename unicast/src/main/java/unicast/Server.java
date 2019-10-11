package unicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Server implements Runnable {

    public void run(){
        try {

            DatagramSocket server = new DatagramSocket(8088);
            byte [] recvBuf = new byte[1024];
            while (true){
                DatagramPacket datagramPacket = new DatagramPacket(recvBuf, recvBuf.length);

                server.receive(datagramPacket);

                String str = new String(datagramPacket.getData(),0,recvBuf.length);
                System.out.println(str);
            }


        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

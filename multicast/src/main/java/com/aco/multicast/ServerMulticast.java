package com.aco.multicast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;

public class ServerMulticast {

    private static Logger log = LoggerFactory.getLogger(ServerMulticast.class);

//    static {
//        Properties prop = new Properties();
//        try {
//            prop.load(ServerMulticast.class.getResourceAsStream("/config.properties"));
//            ServerMulticast.magic = Integer.parseInt(prop.getProperty("magic"));
//            ServerMulticast.port = Integer.parseInt(prop.getProperty("port"));
////            ServerMulticast.host = prop.getProperty("host");
//            ServerMulticast.multicastAddress = prop.getProperty("multicastAddress");
//
//        } catch (IOException e) {
//            log.warn("无法读取到配置文件！");
//            e.printStackTrace();
//        }
//    }

    public int magic = 0x8888;

    public int port = 3001;

    public String host;

    public String multicastAddress = "224.1.1.1";

    private MulticastSocket serverSocket;

    public ServerMulticast(String host){
        try {
            this.host = host;
            this.serverSocket = new MulticastSocket(this.port);
        } catch (IOException e) {
            log.warn("创建组播异常！");
            e.printStackTrace();
        }

    }

    public ServerMulticast(MulticastSocket acceptSocket) {
        this.serverSocket = acceptSocket;
    }

    public ServerMulticast(int port, String host, MulticastSocket acceptSocket) {
        this.port = port;
        this.host = host;
        this.serverSocket = acceptSocket;
    }

    public void start() {

            try {
                this.serverSocket.setInterface(InetAddress.getByName(this.host));
                this.serverSocket.joinGroup(InetAddress.getByName(this.multicastAddress));
                while (true){
                    this.accept();
                }

            } catch (SocketException e) {
                log.warn("组播异常！");
                e.printStackTrace();
            } catch (UnknownHostException e) {
                log.warn("未知的主机！");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void accept(){
        byte[] buff = new byte[65535];
        DatagramPacket datagramPacket = new DatagramPacket(buff, buff.length);
        try {
            this.serverSocket.receive(datagramPacket);
            byte[] data = datagramPacket.getData();
            int magic = (data[1]&0x0ff)+((data[0]&0x0ff)<< 8);
            if (magic != this.magic){
                throw new Exception();
            }
            int len = (data[3]&0x0ff)+((data[2]&0x0ff) << 8);
            String result = new String(data, 4, len);
            String sendIp = datagramPacket.getAddress().getHostAddress();
            if (!sendIp.equals(this.host)){
                this.send();
            }
            log.info(result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void send(){

        String msg = "{\n" +
                "    \"name\":\"router1\",\n" +
                "    \"mac\":\"ec-d2-3d-4e-5c\",\n" +
                "    \"type\":2,\n" +
                "    \"host\":\""+this.host+"\",\n" +
                "    \"protocol\":\"http\",\n" +
                "    \"modules\":{\n" +
                "        \"launch\":{\n" +
                "            \"port\":3010\n" +
                "        },\n" +
                "        \"message\":{\n" +
                "            \"port\":3010\n" +
                "        },\n" +
                "        \"apps\":{\n" +
                "            \"port\":3010\n" +
                "        }\n" +
                "    }\n" +
                "}";

        byte [] data = msg.getBytes();
        byte [] buff = new byte[data.length+4];
        int len = data.length;
        int magic =  0x8888;
        buff[0] = (byte) ((magic >> 8) & 0xff);
        buff[1] = (byte) (magic & 0xff);
        buff[2] = (byte) ((len >> 8) & 0xff);
        buff[3] = (byte) (len & 0xff);
        System.arraycopy(data,0,buff,4,len);
        try {
            DatagramPacket datagramPacket =
                    new DatagramPacket(buff, buff.length,InetAddress.getByName(this.multicastAddress), this.port);
            this.serverSocket.send(datagramPacket);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}

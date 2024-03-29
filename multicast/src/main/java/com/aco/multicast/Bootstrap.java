package com.aco.multicast;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Bootstrap {

    public static void main(String[] args){

        Scanner sc = new Scanner(System.in);
        String ip = "";
        while (true) {
            System.out.println("请输入本机ip:");
            ip = sc.nextLine();
            NetworkInterface networkInterface = null;
            try {
                networkInterface = NetworkInterface.getByInetAddress(InetAddress.getByName(ip));
                if (networkInterface != null) {
                    System.out.println("ok!");
                    break;
                }
                System.out.println("请输入正确的本机ip:");
            } catch (SocketException e) {
                System.out.println("socket异常!");
//                e.printStackTrace();
            } catch (UnknownHostException e) {
                System.out.println("非法地址!");
//                e.printStackTrace();
            }

        }

        new ServerMulticast(ip).start();

    }
}

package com.service.im;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class UDPTest {

    public static void main(String[] args) throws Exception {
        int i = 1;
        for(int o=0; o<1; o++){
            byte[] body = ("Hi! I am client message!" + i).getBytes();
            DatagramPacket packet = new DatagramPacket(body, body.length, new InetSocketAddress("127.0.0.1", 6969));
            final DatagramSocket socket = new DatagramSocket();
            new Thread(){
                @Override
                public void run() {
                    while (true){
                        byte[] buffer = new byte[1024];
                        DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                        try {
                            socket.receive(dp);
                            System.out.println(new String(dp.getData(), 0, dp.getLength()));
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                }
            }.start();
            socket.send(packet);
//            socket.close();
            i++;
        }
    }

}

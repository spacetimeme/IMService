package com.service.im;

import java.io.InputStream;
import java.net.Socket;

public class TestClient {

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 10; i++) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        Socket socket = new Socket("localhost", 6969);
                        InputStream in = socket.getInputStream();
                        while (in.read() > 0) {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

}

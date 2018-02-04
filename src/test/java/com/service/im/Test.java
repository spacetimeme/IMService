package com.service.im;

import com.dream.socket.DreamSocket;
import com.dream.socket.DreamTCPSocket;
import com.dream.socket.codec.Message;
import com.dream.socket.codec.MessageCodec;
import com.dream.socket.codec.MessageHandle;
import com.service.im.protocol.Protocol;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

public class Test {

    public static void main(String[] args) {
        DreamSocket socket = new DreamTCPSocket("localhost", 6969);
        socket.codec(new MessageCodec<Body>() {
            @Override
            public Body decode(SocketAddress address, ByteBuffer buffer) {
                int remaining = buffer.remaining();
                if (remaining < Protocol.HEADER_LENGTH) {
                    return null;
                }
                byte start = buffer.get();
                int length = buffer.getInt();
                if (length > remaining) {
                    return null;
                }
                short type = buffer.getShort();
                byte[] body = new byte[length - Protocol.HEADER_LENGTH];
                buffer.get(body);
                Body b = new TextBody(body);
                byte end = buffer.get();
                return b;
            }

            @Override
            public void encode(Body body, ByteBuffer buffer) {
                buffer.put(Protocol.START_TAG);
                buffer.putInt(Protocol.HEADER_LENGTH + body.getBody().length);
                buffer.putShort(body.type);
                buffer.put(body.getBody());
                buffer.put(Protocol.END_TAG);
            }
        });
        socket.handle(new MessageHandle() {
            @Override
            public void onStatus(int status) {

            }

            @Override
            public void onMessage(Message message) {
                if (message instanceof TextBody) {
                    System.out.println(((TextBody) message).getString());
                }
            }
        });
        socket.start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 1; i <= 10000000; i++) {
            socket.send(new TextBody(("client message -> " + i).getBytes()));
        }
    }

}

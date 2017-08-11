package com.service.im;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Future;

public class AsynchronousClient {

    public static void main(String[] args) throws Exception {
        AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
        Future<Void> future = channel.connect(new InetSocketAddress("127.0.0.1", 6969));
        future.get();
        byte[] buffer = "I am is client message".getBytes();
        ByteBuffer byteBuffer = ByteBuffer.allocate(buffer.length);
        byteBuffer.put(buffer);
        byteBuffer.flip();
        channel.write(byteBuffer);
        ByteBuffer inBuffer = ByteBuffer.allocate(1024 * 1024);
        channel.read(inBuffer, null, new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer result, Void attachment) {

                System.out.println("completed -> " + new String(inBuffer.array(), 0, inBuffer.position()));
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                System.out.println("failed");
            }
        });
        Thread.sleep(1000000);
    }

}

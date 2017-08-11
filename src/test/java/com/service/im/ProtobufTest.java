package com.service.im;

import com.service.im.protobuf.Protobuf;

public class ProtobufTest {

    public static void main(String[] args) throws Exception {
        Protobuf.Text text = Protobuf.Text.newBuilder().setText("test").build();

        Protobuf.Message.Builder builder = Protobuf.Message.newBuilder();
        builder.setContent(text.toByteString());//关键方法 toByteString()
        Protobuf.Message message = builder.build();

        text = Protobuf.Text.parseFrom(message.getContent());
        System.out.println(text.getText());
    }

}

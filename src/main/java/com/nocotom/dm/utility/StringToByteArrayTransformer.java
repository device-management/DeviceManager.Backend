package com.nocotom.dm.utility;

import org.springframework.integration.transformer.MessageTransformationException;
import org.springframework.integration.transformer.Transformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.Assert;

import java.nio.charset.Charset;

public class StringToByteArrayTransformer implements Transformer {

    private final Charset charset;

    public StringToByteArrayTransformer(Charset charset) {
        Assert.notNull(charset, "'charset' must not be null");
        this.charset = charset;
    }

    public StringToByteArrayTransformer() {
        this(Charset.defaultCharset());
    }

    @Override
    public Message<?> transform(Message<?> message) {
        if (message.getPayload() instanceof String) {
            return MessageBuilder.withPayload(((String) message.getPayload()).getBytes(charset)).copyHeaders(message.getHeaders()).build();
        } else {
            throw new MessageTransformationException(message, "The payload is not a String instance.");
        }
    }
}

package com.laterna.connexemain.v1._shared.converter;

import com.google.protobuf.MessageLite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.util.MimeType;

import java.lang.reflect.Method;

public class ProtobufMessageConverter extends AbstractMessageConverter {

    private static final Logger log = LoggerFactory.getLogger(ProtobufMessageConverter.class);

    public ProtobufMessageConverter() {
        super(MimeType.valueOf("application/protobuf"));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return MessageLite.class.isAssignableFrom(clazz);
    }

    @Override
    protected Object convertFromInternal(org.springframework.messaging.Message<?> message,
                                         Class<?> targetClass, Object conversionHint) {
        Object payload = message.getPayload();

        if (payload instanceof byte[] && MessageLite.class.isAssignableFrom(targetClass)) {
            byte[] bytes = (byte[]) payload;

            log.debug("Trying to convert {} bytes to {}", bytes.length, targetClass.getName());

            try {
                Method parseFrom = targetClass.getMethod("parseFrom", byte[].class);
                return parseFrom.invoke(null, bytes);
            } catch (Exception e) {
                log.error("Converting error: {}", e.getMessage());
                if (e.getCause() != null) {
                    log.error("Cause: {}", e.getCause().getMessage());
                }
                return null;
            }
        }
        return null;
    }

    @Override
    protected Object convertToInternal(Object payload, MessageHeaders headers, Object conversionHint) {
        if (payload instanceof MessageLite) {
            return ((MessageLite) payload).toByteArray();
        }
        return null;
    }
}
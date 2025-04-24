package com.laterna.connexemain.v1._shared.websocket.error;

import com.laterna.connexemain.v1._shared.exception.enumeration.ErrorType;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;

@Component
public class StompSubProtocolErrorHandlerImpl extends StompSubProtocolErrorHandler {

    @Override
    protected Message<byte[]> handleInternal(
            StompHeaderAccessor errorHeaderAccessor,
            byte[] errorPayload,
            Throwable cause,
            StompHeaderAccessor clientHeaderAccessor) {

        String errorMessage = "";

        if (cause != null) {
            Throwable rootCause = cause.getCause() != null ? cause.getCause() : cause;

            if (rootCause instanceof AccessDeniedException) {
                errorMessage = rootCause.getMessage();
                errorHeaderAccessor.setMessage(ErrorType.ACCESS_DENIED.name());
            } else {
                errorMessage = rootCause.getMessage();
                errorHeaderAccessor.setMessage(ErrorType.INTERNAL_SERVER_ERROR.name());
            }
        }

        if (clientHeaderAccessor != null) {
            errorHeaderAccessor.setSessionId(clientHeaderAccessor.getSessionId());
            String receiptId = clientHeaderAccessor.getReceipt();
            if (receiptId != null) {
                errorHeaderAccessor.setReceiptId(receiptId);
            }
        }

        byte[] payload = errorMessage.isEmpty() ?
                errorPayload :
                errorMessage.getBytes(StandardCharsets.UTF_8);

        return MessageBuilder.createMessage(payload, errorHeaderAccessor.getMessageHeaders());
    }
}
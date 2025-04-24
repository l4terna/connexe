package com.laterna.connexemain.v1._shared.security;

import com.google.protobuf.util.JsonFormat;
import com.laterna.connexemain.v1._shared.exception.enumeration.ErrorType;
import com.laterna.proto.v1.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

   @Override
   public void commence(HttpServletRequest request,
                        HttpServletResponse response,
                        AuthenticationException ex) throws IOException {

       response.setStatus(HttpStatus.UNAUTHORIZED.value());
       response.setContentType("application/json");
       response.setCharacterEncoding("UTF-8");

       ErrorResponse errorResponse = ErrorResponse.newBuilder()
           .setStatusCode(HttpStatus.UNAUTHORIZED.value())
           .setMessage("Invalid credentials")
           .setType(ErrorType.UNAUTHORIZED.name())
           .setPath(request.getRequestURI())
           .setTimestamp(Instant.now().toEpochMilli())
           .build();

       response.getWriter().write(JsonFormat.printer().preservingProtoFieldNames().print(errorResponse));
   }
}
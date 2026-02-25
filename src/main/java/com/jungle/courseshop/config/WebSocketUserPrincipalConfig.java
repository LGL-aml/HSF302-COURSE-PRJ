package com.jungle.courseshop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.Principal;

@Configuration
public class WebSocketUserPrincipalConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    Principal current = accessor.getUser();

                    if (current instanceof Authentication auth) {
                        Object principal = auth.getPrincipal();
                        if (principal instanceof com.jungle.courseshop.entity.User u) {
                            accessor.setUser(new Principal() {
                                @Override
                                public String getName() {
                                    return String.valueOf(u.getId());
                                }
                            });
                        }
                    } else if (current instanceof UsernamePasswordAuthenticationToken token) {
                        Object principal = token.getPrincipal();
                        if (principal instanceof com.jungle.courseshop.entity.User u) {
                            accessor.setUser(new Principal() {
                                @Override
                                public String getName() {
                                    return String.valueOf(u.getId());
                                }
                            });
                        }
                    }
                }

                return message;
            }
        });
    }
}

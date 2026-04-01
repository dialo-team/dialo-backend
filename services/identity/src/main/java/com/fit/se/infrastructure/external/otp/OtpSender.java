package com.fit.se.infrastructure.external.otp;

import com.fit.se.domain.otp.OtpType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OtpSender {

    private final List<OtpChannelState> states;

    public void send(String destination, OtpType type, String otp) {

        OtpChannelState state = states.stream()
                .filter(s -> s.getType() == type)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unsupported OTP type"));

        System.out.println("OTP: " + otp);

        state.send(destination, otp);
    }
}

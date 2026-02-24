package com.fit.se.service;

import com.fit.se.dto.request.SignUpRequest;
import com.fit.se.dto.request.UpEmailRequest;
import com.fit.se.dto.request.UpPassRequest;
import org.springframework.stereotype.Service;

public interface AccountService {
    public void create(SignUpRequest request);

    public void updateEmail(String id, UpEmailRequest request);

    public void updatePassword(String id, UpPassRequest request);

    public void activate(String phone);
}

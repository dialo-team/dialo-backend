package com.fit.se.application.friendship.query.check;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckFriendQueryHandler {

    public CheckFriendResult execute(CheckFriendQuery query) {
        return CheckFriendResult.builder().build();
    }
}

package com.fit.se.call.domain;

import com.fit.se.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CallInfo {
    private String type;
    private String status;
    private List<User> users;
    private Instant startedTime;
    private Instant endedTime;
}

package com.fit.se.user.application.query.lookup;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LookupQueryHandler {

    public LookupQueryResult execute(LookupByPhoneQuery query) {
        return LookupQueryResult.builder().build();
    }

    public LookupQueryResult execute(LookupByQrQuery query) {
        return LookupQueryResult.builder().build();
    }
}

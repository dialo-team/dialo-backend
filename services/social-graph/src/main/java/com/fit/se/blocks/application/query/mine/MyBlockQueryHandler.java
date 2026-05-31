package com.fit.se.blocks.application.query.mine;

import com.fit.se.blocks.domain.BlockRelationRepository;
import com.fit.se.blocks.domain.aggregate.BlockRelation;
import com.fit.se.user.domain.UserRepository;
import com.fit.se.user.domain.aggregate.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyBlockQueryHandler {

    private final BlockRelationRepository blockRepository;
    private final UserRepository userRepository;

    public MyBlockResult execute(MyBlockQuery query) {
        if (query == null || query.current() == null || query.current().isBlank()) {
            throw new IllegalArgumentException("Current user id must not be blank");
        }

        List<BlockRelation> blocks = blockRepository.findActiveByBlocker(query.current());

        List<MyBlockResult.Item> items = blocks.stream()
                .map(this::toItem)
                .toList();

        return MyBlockResult.builder()
                .blocks(items)
                .build();
    }

    private MyBlockResult.Item toItem(BlockRelation block) {
        User blockedUser = userRepository.findById(block.getBlockedId())
                .orElseThrow(() -> new IllegalArgumentException("Blocked user not found: " + block.getBlockedId()));

        return MyBlockResult.Item.builder()
                .blockId(block.getId())
                .blockedUserId(blockedUser.getId())
                .blockedUserName(blockedUser.getUserName())
                .blockedAvatar(blockedUser.getAppearance() != null ? blockedUser.getAppearance().getAvatar() : null)
                .reason(block.getReason())
                .createdAt(block.getCreatedAt())
                .build();
    }
}
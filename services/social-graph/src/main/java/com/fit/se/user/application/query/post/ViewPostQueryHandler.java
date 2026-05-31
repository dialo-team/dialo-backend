package com.fit.se.user.application.query.post;

import com.fit.se.blocks.domain.BlockRelationRepository;
import com.fit.se.friendship.domain.FriendshipRepository;
import com.fit.se.user.domain.policy.PostVisibilityPolicy;
import com.fit.se.user.domain.policy.RelationContext;
import com.fit.se.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ViewPostQueryHandler {
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final BlockRelationRepository blockRelationRepository;
    private final PostVisibilityPolicy postVisibilityPolicy;

    public boolean execute(ViewPostQuery query) {
        var viewer = userRepository.findById(query.viewerId())
                .orElseThrow(() -> new IllegalArgumentException("Viewer not found"));

        var owner = userRepository.findById(query.ownerId())
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));

        var friendship = friendshipRepository.findBetween(query.viewerId(), query.ownerId()).orElse(null);
        var blockRelation = blockRelationRepository.findActiveBetween(query.viewerId(), query.ownerId()).orElse(null);

        var relationContext = new RelationContext(friendship, blockRelation);

        return postVisibilityPolicy.canViewPost(viewer, owner, relationContext);
    }
}

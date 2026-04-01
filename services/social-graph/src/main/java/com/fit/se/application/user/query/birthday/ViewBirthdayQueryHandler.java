package com.fit.se.application.user.query.birthday;

import com.fit.se.domain.block.BlockRelationRepository;
import com.fit.se.domain.friendship.FriendshipRepository;
import com.fit.se.domain.policy.BirthdayVisibilityPolicy;
import com.fit.se.domain.policy.RelationContext;
import com.fit.se.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ViewBirthdayQueryHandler {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final BlockRelationRepository blockRelationRepository;
    private final BirthdayVisibilityPolicy birthdayVisibilityPolicy;

    public ViewBirthdayResult execute(ViewBirthdayQuery query) {
        var viewer = userRepository.findById(query.viewerId())
                .orElseThrow(() -> new IllegalArgumentException("Viewer not found"));

        var owner = userRepository.findById(query.ownerId())
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));

        var friendship = friendshipRepository.findBetween(query.viewerId(), query.ownerId()).orElse(null);
        var blockRelation = blockRelationRepository.findActiveBetween(query.viewerId(), query.ownerId()).orElse(null);

        var relationContext = new RelationContext(friendship, blockRelation);

        var visibility = birthdayVisibilityPolicy.resolveVisibility(viewer, owner, relationContext);

        return new ViewBirthdayResult(
                visibility,
                owner.getProfile().getDob()
        );
    }
}

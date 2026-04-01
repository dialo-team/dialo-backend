package com.fit.se.application.friendship.command.request;

public record RequestFriendByPhoneCommand(
   String current,
   String phone
) {}

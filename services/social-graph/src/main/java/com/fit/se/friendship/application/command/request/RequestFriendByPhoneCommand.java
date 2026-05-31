package com.fit.se.friendship.application.command.request;

public record RequestFriendByPhoneCommand(
   String current,
   String phone
) {}

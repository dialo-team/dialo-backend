package com.fit.se.message.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Reaction {
    private int count;
    private String emoji;
    private Integer emojiCode;
    private boolean me;

    public Reaction(int count, int emojiCode, boolean me) {
        this.count = count;
        this.emojiCode = emojiCode;
        this.emoji = new String(Character.toChars(emojiCode));
        this.me = me;
    }
}

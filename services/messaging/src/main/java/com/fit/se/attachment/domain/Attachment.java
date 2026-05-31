package com.fit.se.attachment.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Attachment {
    private String id;
    private String fileName;
    private String title;
    private String description;
    private String contentType;
    private int size;
    private String url;

    // if image or video
    private double height;
    private double width;
    private String placeHolder;
    private String placeHolderVersion;

    // for voice message
    private float durationSecs;
}

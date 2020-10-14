package com.springboot.elasticsearch.musicstreaming.document;

import lombok.Data;

import java.util.Date;

// this is the model

@Data
public class SongDocument {
    private String id;
    private String name;
    private String artist;
    private String image;
    private String albumType;
    private String releaseDate;
    private String releaseDatePrecision;
    private String path;
}

package com.springboot.elasticsearch.musicstreaming.document;

import lombok.Data;

import java.util.Date;

// this is the model

@Data
public class SongDocument {
    private String id;
    private String name;
    //    private List<Artist> artists;
//    private List<Image> images;
    private String albumType;
    private Date releaseDate;
    private String releaseDatePrecision;
    private String path;
}

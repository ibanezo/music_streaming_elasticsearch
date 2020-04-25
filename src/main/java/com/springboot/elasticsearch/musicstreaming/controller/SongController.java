package com.springboot.elasticsearch.musicstreaming.controller;

import com.springboot.elasticsearch.musicstreaming.service.SongService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class SongController {

    private SongService service;

    public SongController(SongService service) {
        this.service = service;
    }

    @GetMapping("/test")
    public String test(){
        return "Success!";
    }

}

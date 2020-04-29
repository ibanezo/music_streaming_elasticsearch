package com.springboot.elasticsearch.musicstreaming.controller;

import com.springboot.elasticsearch.musicstreaming.document.SongDocument;
import com.springboot.elasticsearch.musicstreaming.service.SongService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
public class SongController {

    private SongService service;

    public SongController(SongService service) {
        this.service = service;
    }

    @GetMapping("/test")
    public String test() {
        return "Success!";
    }

    // creating new record
    @PostMapping("/songs")
    public ResponseEntity createSong(@RequestBody SongDocument document) throws Exception {
        return new ResponseEntity(service.createSongDocument(document), HttpStatus.CREATED);
    }

    // updating record
    @PutMapping
    public ResponseEntity updateSong(@RequestBody SongDocument document) throws Exception {
        return new ResponseEntity(service.updateSong(document), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public SongDocument findById(@PathVariable String id) throws Exception {
        return service.findById(id);
    }

    @GetMapping
    public List<SongDocument> findAll() throws Exception {
        return service.findAll();
    }

//    @GetMapping(value = "/search")
//    public List<SongDocument> search(@RequestParam(value = "Black Pumas - Colors (Official Live Session)") String name) throws Exception {
//        return service.searchByName(name);
//    }

    @GetMapping(value = "/api/songs/name-search")
    public List<SongDocument> searchByName(@RequestParam(value = "name") String name) throws Exception {
        return service.findSongByName(name);
    }


    @DeleteMapping("/{id}")
    public String deleteSongDocument(@PathVariable String id) throws Exception {
        return service.deleteSongDocument(id);
    }
}

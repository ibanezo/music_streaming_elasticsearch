package com.springboot.elasticsearch.musicstreaming.controller;
import com.springboot.elasticsearch.musicstreaming.document.SongDocument;
import com.springboot.elasticsearch.musicstreaming.service.SongService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController()
public class SongController {
    private static final Logger logger = LoggerFactory.getLogger(SongController.class);
    private SongService songService;

    public SongController(SongService service) {
        this.songService = service;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/test")
    public String test() {
        return "Success!";
    }

    // creating new record
    @PostMapping("/create/song")
    public ResponseEntity createSong(@RequestBody SongDocument document) throws Exception {
        return new ResponseEntity(songService.createSongDocument(document), HttpStatus.CREATED);
    }

    // updating record
    @PutMapping
    public ResponseEntity updateSong(@RequestBody SongDocument document) throws Exception {
        return new ResponseEntity(songService.updateSong(document), HttpStatus.CREATED);
    }

    // find song by id
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/{id}")
    public SongDocument findById(@PathVariable String id) throws Exception {
        return songService.findById(id);
    }

    // find all songs
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/songs")
    public List<SongDocument> findAll() throws Exception {
        return songService.findAll();
    }

    // search song by name
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping(value = "/api/songs/name-search")
    public List<SongDocument> searchByName(@RequestParam(value = "name") String name) throws Exception {
        return songService.findSongByName(name);
    }

    // delete a song by id
    @DeleteMapping("/delete/{id}")
    public String deleteSong(@PathVariable String id) throws Exception {
        return songService.deleteSong(id);
    }

    // play song
    @CrossOrigin(origins = "http://localhost:3000")
    @RequestMapping(value = "/play/song/{id}", method = RequestMethod.GET)
    public ResponseEntity<StreamingResponseBody> play(@PathVariable("id") String songId, HttpServletResponse response ) throws Exception {
        return songService.playSongDocument(songId, response);
    }

}

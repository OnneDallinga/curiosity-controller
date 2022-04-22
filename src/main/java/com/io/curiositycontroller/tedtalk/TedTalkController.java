package com.io.curiositycontroller.tedtalk;

import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(value = "/ted-talks", produces = MediaType.APPLICATION_JSON_VALUE)
public class TedTalkController {

    private final TedTalkService tedTalkService;

    public TedTalkController(TedTalkService tedTalkService) {
        this.tedTalkService = tedTalkService;
    }

    @GetMapping("/{id}")
    public TedTalk getTedTalk(@PathVariable @NotNull String id) {
        return tedTalkService.getTedTalk(id);
    }

    @GetMapping
    public List<TedTalk> searchTedTalks(@RequestParam MultiValueMap<String, String> params) {
        return tedTalkService.searchTedTalks(params);
    }

    @GetMapping("/author/{author}")
    public List<TedTalk> searchByAuthor(@PathVariable @NotNull String author) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("author", author);
        return tedTalkService.searchTedTalks(params);
    }

    @GetMapping("/title/{title}")
    public List<TedTalk> searchByTitle(@PathVariable @NotNull String title) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("title", title);
        return tedTalkService.searchTedTalks(params);
    }

    @GetMapping("/views/{views}")
    public List<TedTalk> searchByViews(@PathVariable @NotNull String views) {
        try {
            Long.parseLong(views);
        } catch (NumberFormatException e) {
            throw new TedTalkException("Please enter a valid number");
        }
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("views", views);
        return tedTalkService.searchTedTalks(params);
    }

    @GetMapping("/likes/{likes}")
    public List<TedTalk> searchByLikes(@PathVariable @NotNull String likes) {
        try {
            Long.parseLong(likes);
        } catch (NumberFormatException e) {
            throw new TedTalkException("Please enter a valid number");
        }
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("likes", likes);
        return tedTalkService.searchTedTalks(params);
    }

    @PostMapping
    public String createTedTalk(@RequestBody @Valid TedTalkDto tedTalk) {
        return tedTalkService.createTedTalk(tedTalk.getTitle(), tedTalk.getAuthor(), tedTalk.getDate(), tedTalk.getLink());
    }

    @PutMapping("/{id}")
    public void updateTedTalk(@PathVariable @NotNull String id, @RequestBody @Valid TedTalkDto tedTalk) {
        tedTalkService.updateTedTalk(id, tedTalk.getTitle(), tedTalk.getAuthor(), tedTalk.getDate(), tedTalk.getLink());
    }

    @DeleteMapping("/{id}")
    public void deleteTedTalk(@PathVariable @NotNull String id) {
        tedTalkService.deleteTedTalk(id);
    }

}

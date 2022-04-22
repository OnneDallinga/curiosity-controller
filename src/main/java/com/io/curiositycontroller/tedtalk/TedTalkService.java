package com.io.curiositycontroller.tedtalk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TedTalkService {

    private final TedTalkRepository tedTalkRepository;

    @Autowired
    public TedTalkService(TedTalkRepository tedTalkRepository) {
        this.tedTalkRepository = tedTalkRepository;
    }

    public void deleteTedTalk(String id) {
        if (tedTalkRepository.getById(id) == null) {
            throw new TedTalkException("No existing Ted Talk found to delete");
        } else {
            tedTalkRepository.delete(id);
        }
    }

    public List<TedTalk> searchTedTalks(MultiValueMap<String, String> params) {
        return tedTalkRepository.search(params).stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    public void updateTedTalk(String id, String title, String author, String date, String link) {
        TedTalk existingTedTalk = tedTalkRepository.getById(id);
        if (existingTedTalk == null) {
            throw new TedTalkException("No existing Ted Talk found to update");
        } else {
            tedTalkRepository.save(existingTedTalk.toBuilder()
                    .title(title)
                    .author(author)
                    .date(date)
                    .link(link)
                    .build());
        }
    }

    public String createTedTalk(String title, String author, String date, String link) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("title", title);
        params.add("author", author);

        if (tedTalkRepository.search(params).stream()
                .map(SearchHit::getContent)
                .anyMatch(tedTalk -> author.equals(tedTalk.getAuthor()) && title.equals(tedTalk.getTitle()) && date.equals(tedTalk.getDate()) && link.equals(tedTalk.getLink()))) {
            throw new TedTalkException("Identical Ted Talk already exists");
        }

        String id = UUID.randomUUID().toString();
        tedTalkRepository.save(TedTalk.builder()
                .id(id)
                .title(title)
                .author(author)
                .date(date)
                .likes(0)
                .views(0)
                .link(link)
                .build());
        return id;
    }

    public TedTalk getTedTalk(String id) {
        return tedTalkRepository.getById(id);
    }
}

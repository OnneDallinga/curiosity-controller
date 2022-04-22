package com.io.curiositycontroller.tedtalk;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = TedTalkService.class)
class TedTalkServiceTest {

    @Autowired
    private TedTalkService tedTalkService;

    @MockBean
    private TedTalkRepository tedTalkRepository;

    @Test
    void deleteTedTalk_shouldThrowOnNoTedTalkExists() {
        when(tedTalkRepository.getById("id")).thenReturn(null);
        TedTalkException tedTalkException = assertThrows(TedTalkException.class, () -> tedTalkService.deleteTedTalk("id"));
        assertThat(tedTalkException.getMessage(), is("No existing Ted Talk found to delete"));
    }

    @Test
    void deleteTedTalk_shouldDelete() {
        String id = "id";
        when(tedTalkRepository.getById(id)).thenReturn(TedTalk.builder().build());
        tedTalkService.deleteTedTalk(id);
        verify(tedTalkRepository, times(1)).delete(eq(id));
    }

    @Test
    void searchTedTalks_shouldSearchAndMap() {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        TedTalk tedTalk1 = TedTalk.builder()
                .id("1")
                .author("author1")
                .title("title1")
                .date("date1")
                .views(1)
                .likes(1)
                .link("link1")
                .build();
        TedTalk tedTalk2 = TedTalk.builder()
                .id("2")
                .author("author2")
                .title("title2")
                .date("date2")
                .views(2)
                .likes(2)
                .link("link2")
                .build();

        SearchHit<TedTalk> mockTalk1 = mock(SearchHit.class);
        SearchHit<TedTalk> mockTalk2 = mock(SearchHit.class);
        List<SearchHit<TedTalk>> listOfSearchHits = List.of(mockTalk1, mockTalk2);
        SearchHits<TedTalk> searchHits = mock(SearchHits.class);

        when(mockTalk1.getContent()).thenReturn(tedTalk1);
        when(mockTalk2.getContent()).thenReturn(tedTalk2);
        when(searchHits.stream()).thenReturn(listOfSearchHits.stream());
        when(tedTalkRepository.search(params)).thenReturn(searchHits);

        List<TedTalk> tedTalks = tedTalkService.searchTedTalks(params);
        assertThat(tedTalks, is(notNullValue()));
        assertThat(tedTalks.size(), is(2));
        assertTrue(tedTalks.contains(tedTalk1));
        assertTrue(tedTalks.contains(tedTalk2));
    }

    @Test
    void updateTedTalk_shouldThrowOnNoExistingTedTalkExists() {
        when(tedTalkRepository.getById("id")).thenReturn(null);
        TedTalkException tedTalkException = assertThrows(TedTalkException.class, () -> tedTalkService.updateTedTalk("id", "newTitle", "newAuthor", "date", "link"));
        assertThat(tedTalkException.getMessage(), is("No existing Ted Talk found to update"));
    }

    @Test
    void updateTedTalk_shouldUpdate() {
        String id = "id";
        TedTalk originalTedTalk = TedTalk.builder()
                .id(id)
                .title("oldTitle")
                .author("oldAuthor")
                .date("oldDate")
                .likes(5)
                .views(10)
                .link("oldLink")
                .build();
        when(tedTalkRepository.getById(id)).thenReturn(originalTedTalk);

        String newTitle = "newTitle";
        String newAuthor = "newAuthor";
        String newDate = "newDate";
        String newLink = "newLink";
        tedTalkService.updateTedTalk(id, newTitle, newAuthor, newDate, newLink);
        TedTalk updatedTedTalk = TedTalk.builder()
                .id(id)
                .title(newTitle)
                .author(newAuthor)
                .date(newDate)
                .likes(5)
                .views(10)
                .link(newLink)
                .build();
        verify(tedTalkRepository, times(1)).save(eq(updatedTedTalk));
    }

    @Test
    void createTedTalk_shouldThrowOnTedTalkAlreadyExists() {
        String author = "author";
        String title = "title";
        String date = "date";
        String link = "link";
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(author, author);
        params.add(title, title);
        TedTalk tedTalk = TedTalk.builder()
                .id("1")
                .author(author)
                .title(title)
                .date(date)
                .views(1)
                .likes(1)
                .link(link)
                .build();

        SearchHit<TedTalk> mockTalk = mock(SearchHit.class);
        List<SearchHit<TedTalk>> listOfSearchHits = List.of(mockTalk);
        SearchHits<TedTalk> searchHits = mock(SearchHits.class);

        when(mockTalk.getContent()).thenReturn(tedTalk);
        when(searchHits.stream()).thenReturn(listOfSearchHits.stream());
        when(tedTalkRepository.search(eq(params))).thenReturn(searchHits);

        TedTalkException tedTalkException = assertThrows(TedTalkException.class, () -> tedTalkService.createTedTalk(title, author, date, link));
        assertThat(tedTalkException.getMessage(), is("Identical Ted Talk already exists"));
        verify(tedTalkRepository, times(1)).search(eq(params));
        verify(tedTalkRepository, never()).save(any());
    }

    @Test
    void createTedTalk_shouldCreateNewTedTalk() {
        SearchHits<TedTalk> searchHits = mock(SearchHits.class);
        when(searchHits.stream()).thenReturn(Stream.empty());
        when(tedTalkRepository.search(any())).thenReturn(searchHits);

        String author = "author";
        String title = "title";
        String date = "date";
        String link = "link";
        String id = tedTalkService.createTedTalk(title, author, date, link);
        assertThat(id, is(notNullValue()));
        verify(tedTalkRepository, times(1)).save(eq(TedTalk.builder()
                .id(id)
                .author(author)
                .title(title)
                .date(date)
                .likes(0)
                .views(0)
                .link(link)
                .build()));
    }

    @Test
    void getTedTalk_shouldGetById() {
        String id = "id";
        tedTalkService.getTedTalk(id);
        verify(tedTalkRepository, times(1)).getById(eq(id));
    }


}

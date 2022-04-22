package com.io.curiositycontroller.tedtalk;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class TedTalkDto {

    @NotNull
    @Size(min = 1, max = 100)
    private String title;

    @NotNull
    @Size(min = 1, max = 100)
    private String author;

    @NotNull
    @Size(min = 1, max = 100)
    private String date;

    @NotNull
    @Size(min = 1, max = 500)
    private String link;

}

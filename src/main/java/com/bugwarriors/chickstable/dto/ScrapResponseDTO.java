package com.bugwarriors.chickstable.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScrapResponseDTO {
    private long id;
    private String title;
    private String contents;
}

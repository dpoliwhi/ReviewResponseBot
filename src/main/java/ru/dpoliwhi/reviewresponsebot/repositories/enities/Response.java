package ru.dpoliwhi.reviewresponsebot.repositories.enities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "response")
@Getter
@Setter
@RequiredArgsConstructor
public class Response {
    @Id
    @Column(name = "id")
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "response_text")
    private String responseText;
}

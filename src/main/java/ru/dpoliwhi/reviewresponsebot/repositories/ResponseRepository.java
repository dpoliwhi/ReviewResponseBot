package ru.dpoliwhi.reviewresponsebot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.dpoliwhi.reviewresponsebot.repositories.enities.Response;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {
}

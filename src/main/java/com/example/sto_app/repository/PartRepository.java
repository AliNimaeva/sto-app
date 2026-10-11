package com.example.sto_app.repository;

import com.example.sto_app.model.Part;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartRepository extends JpaRepository<Part, Long> {

    boolean existsByArticle(String article);
    boolean existsByArticleAndIdNot(String article, Long id);

}
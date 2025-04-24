package com.laterna.connexemain.v1.media;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

interface MediaRepository extends JpaRepository<Media, Long> {
    @Query("SELECT m FROM Media m ORDER BY m.id ASC LIMIT 1")
    Optional<Media> findByFirstStorageKey(String storageKey);

}
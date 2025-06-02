package com.laterna.connexemain.v1.media;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
interface MediaRepository extends JpaRepository<Media, Long> {
    Optional<Media> findFirstByStorageKey(String storageKey);

    List<Media> findByStorageKeyIn(Iterable<String> storageKeys);
}
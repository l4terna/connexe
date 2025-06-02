package com.laterna.connexemain.v1.message.hidden;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface HiddenMessageRepository extends JpaRepository<HiddenMessage, Long> {
}

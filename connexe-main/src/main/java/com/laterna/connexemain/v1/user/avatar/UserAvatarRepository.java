package com.laterna.connexemain.v1.user.avatar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface UserAvatarRepository extends JpaRepository<UserAvatar, Long> {
}

package com.laterna.connexemain.v1.hub.member;

import com.laterna.connexemain.v1.hub.Hub;
import com.laterna.connexemain.v1.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
interface HubMemberRepository extends JpaRepository<HubMember, Long> {
    Optional<HubMember> findByHubAndUser(Hub hub, User user);

    @EntityGraph(attributePaths = {"hub", "hub.owner"})
    Optional<HubMember> findByUserIdAndHubId(Long userId, Long hubId);

    @Query("""
    SELECT hm FROM HubMember hm
    WHERE hm.hub.id = :hubId AND (:after IS NULL OR hm.user.id > :after)
    ORDER BY
        CASE WHEN hm.user.id IN :userIds THEN hm.user.id ELSE hm.user.id END,
        hm.user.id ASC
""")
    List<HubMember> findAllByHubIdAndSortByOnlineUserIds(Long hubId, Set<Long> userIds, Long after);

    // ДЛЯ ПОЛУЧЕНИЯ ЮЗЕРОВ ПО РОЛЯМ
    /**
     * Находит все ID Хабов, где у пользователя есть роль с запрошенными правами (битовой маской).
     * Алгоритм проверки прав:
     * 1. Сдвигаем проверяемую маску влево, чтобы значащие биты совпали позициями с маской роли
     *    Пример: если маска роли "11000", а проверяем "11", то "11" сдвигается влево на 3 позиции -> "11000"
     * 2. Выполняем побитовое AND между маской роли и сдвинутой проверяемой маской
     * 3. Если результат равен сдвинутой проверяемой маске, значит все запрошенные права присутствуют
     */
    @Query(value = """
    SELECT hm.* FROM hubs h
    JOIN hub_members hm ON h.id = hm.hub_id
    JOIN hub_member_roles hmr ON hmr.hub_member_id = hm.id
    JOIN roles r ON r.id = hmr.role_id
    WHERE hm.user_id = :userId
      AND (h.owner_id = :ownerId OR
           length(r.permissions_mask) >= length(:permissionsMask::bit varying)
           AND r.permissions_mask & lpad(:permissionsMask::bit varying::text, length(r.permissions_mask), '0')::bit varying << length(r.permissions_mask) - length(:permissionsMask) =
               lpad(:permissionsMask::bit varying::text, length(r.permissions_mask), '0')::bit varying << length(r.permissions_mask) - length(:permissionsMask)
      );
""", nativeQuery = true)
    Page<HubMember> findAllByChannelIdAndSortByUserIdsWithPermissions(Long channelId, Iterable<Long> userIds, String permissionsMask, Pageable pageable);
}

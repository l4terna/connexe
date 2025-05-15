package com.laterna.connexemain.v1.role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("""
                    SELECT r FROM Role r
                    WHERE r.hub.id = :hubId AND r.name LIKE :search AND r.id NOT IN 
                                (SELECT hmr.role.id FROM HubMemberRole hmr WHERE hmr.hubMember.id = :memberId)
            """)
    Page<Role> findByHubIdWithoutMemberIdRoles(Long hubId, Long memberId, String search, Pageable pageable);

    boolean existsByHubIdAndName(Long hubId, String name);

    Page<Role> findByHubIdAndNameLikeIgnoreCase(Long hubId, String name, Pageable pageable);

    Page<Role> findByHubId(Long hubId, Pageable pageable);

    @Query(value = "SELECT r.* FROM roles r " +
            "JOIN hub_member_roles hmr ON r.id = hmr.role_id " +
            "WHERE hmr.hub_member_id = :memberId",
            nativeQuery = true)
    List<Role> findAllMemberRoles(Long memberId);

    @Modifying
    @Query("""
    DELETE FROM HubMemberRole hmr WHERE hmr.role.id = :roleId
""")
    void deleteById(Long roleId);
}

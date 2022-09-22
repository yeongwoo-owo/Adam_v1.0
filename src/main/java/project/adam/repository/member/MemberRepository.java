package project.adam.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.adam.entity.member.Authority;
import project.adam.entity.member.Member;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {

    @Query("select m from Member m where m.isLogin = true and m.authority = project.adam.entity.member.Authority.ROLE_USER")
    List<Member> findLoginUsers();

    Optional<Member> findMemberByEmail(String email);

    boolean existsByEmail(String email);
}

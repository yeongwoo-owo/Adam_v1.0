package project.adam.repository.member;

import project.adam.entity.member.Member;

public interface MemberRepositoryCustom {

    void remove(Member member);

    Member ban(Member writer, int days);
}

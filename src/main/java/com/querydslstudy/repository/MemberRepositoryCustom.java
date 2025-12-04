package com.querydslstudy.repository;

import com.querydslstudy.dto.request.MemberSearchRequest;
import com.querydslstudy.dto.response.MemberTeamResponse;
import com.querydslstudy.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberRepositoryCustom {
    List<MemberTeamResponse> searchByCondition(MemberSearchRequest request);
    Page<MemberTeamResponse> searchByConditionWithPaging(MemberSearchRequest request, Pageable pageable);
    List<Member> findMembersWithTeam();
    List<Member> findMembersOlderThanAverage();
    List<Member> findMembersWithPaging(int offset, int limit);
    Long countMembersByTeam(Long teamId);
}

package com.querydslstudy.repository;

import com.querydslstudy.dto.request.MemberSearchRequest;
import com.querydslstudy.dto.response.MemberTeamResponse;
import com.querydslstudy.entity.Member;
import com.querydslstudy.entity.MemberStatus;
import com.querydslstudy.entity.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    private Long teamAId;
    private Long teamBId;

    @BeforeEach
    void setUp() {

        Team teamA = Team.of("teamA");
        Team teamB = Team.of("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        
        teamAId = teamA.getId();
        teamBId = teamB.getId();

        Member member1 = Member.of("member1", 10);
        Member member2 = Member.of("member2", 20);
        Member member3 = Member.of("member3", 30);
        Member member4 = Member.of("member4", 40);

        teamA.addMember(member1);
        teamA.addMember(member2);
        teamB.addMember(member3);
        teamB.addMember(member4);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
    }

    @Test
    void 기본_조회_테스트() {
        List<Member> members = memberRepository.findAll();
        assertThat(members).hasSize(4);
    }

    @Test
    void 동적_쿼리_테스트() {
        MemberSearchRequest request = new MemberSearchRequest(
                "member1",
                null,
                null,
                null,
                null
        );

        List<MemberTeamResponse> result = memberRepository.searchByCondition(request);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).username()).isEqualTo("member1");
    }

    @Test
    void 나이_조건_검색_테스트() {
        MemberSearchRequest request = new MemberSearchRequest(
                null,
                20,
                40,
                null,
                null
        );

        List<MemberTeamResponse> result = memberRepository.searchByCondition(request);
        assertThat(result).hasSize(3);
    }

    @Test
    void 팀명_조건_검색_테스트() {
        MemberSearchRequest request = new MemberSearchRequest(
                null,
                null,
                null,
                "teamA",
                null
        );

        List<MemberTeamResponse> result = memberRepository.searchByCondition(request);
        assertThat(result).hasSize(2);
        assertThat(result).extracting("teamName").containsOnly("teamA");
    }

    @Test
    void 상태_조건_검색_테스트() {
        MemberSearchRequest request = new MemberSearchRequest(
                null,
                null,
                null,
                null,
                MemberStatus.ACTIVE
        );

        List<MemberTeamResponse> result = memberRepository.searchByCondition(request);
        assertThat(result).hasSize(4);
    }

    @Test
    void 복합_조건_검색_테스트() {
        MemberSearchRequest request = new MemberSearchRequest(
                null,
                20,
                30,
                "teamA",
                MemberStatus.ACTIVE
        );

        List<MemberTeamResponse> result = memberRepository.searchByCondition(request);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).username()).isEqualTo("member2");
    }

    @Test
    void 조인_테스트() {
        List<Member> members = memberRepository.findMembersWithTeam();
        assertThat(members).hasSize(4);
    }

    @Test
    void 서브쿼리_테스트() {
        List<Member> members = memberRepository.findMembersOlderThanAverage();
        // 평균 나이는 25이므로 30, 40인 멤버만 조회됨
        assertThat(members).hasSize(2);
    }

    @Test
    void 페이징_테스트() {
        List<Member> members = memberRepository.findMembersWithPaging(0, 2);
        assertThat(members).hasSize(2);
    }

    @Test
    void Pageable_페이징_테스트() {
        MemberSearchRequest request = new MemberSearchRequest(
                null,
                null,
                null,
                null,
                null
        );

        Pageable pageable = PageRequest.of(0, 2);
        Page<MemberTeamResponse> result = memberRepository.searchByConditionWithPaging(request, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(4);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(2);
    }

    @Test
    void Pageable_페이징_두번째_페이지_테스트() {
        MemberSearchRequest request = new MemberSearchRequest(
                null,
                null,
                null,
                null,
                null
        );

        Pageable pageable = PageRequest.of(1, 2);
        Page<MemberTeamResponse> result = memberRepository.searchByConditionWithPaging(request, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(4);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getNumber()).isEqualTo(1);
    }

    @Test
    void Pageable_조건_검색_페이징_테스트() {
        MemberSearchRequest request = new MemberSearchRequest(
                null,
                20,
                40,
                null,
                null
        );

        Pageable pageable = PageRequest.of(0, 2);
        Page<MemberTeamResponse> result = memberRepository.searchByConditionWithPaging(request, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(3); // member2, member3, member4
    }

    @Test
    void 집계_함수_테스트() {
        Long count = memberRepository.countMembersByTeam(teamAId);
        assertThat(count).isEqualTo(2);
    }

    @Test
    void 집계_함수_다른팀_테스트() {
        Long count = memberRepository.countMembersByTeam(teamBId);
        assertThat(count).isEqualTo(2);
    }
}

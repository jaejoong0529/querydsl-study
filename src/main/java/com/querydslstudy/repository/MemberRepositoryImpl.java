package com.querydslstudy.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydslstudy.dto.request.MemberSearchRequest;
import com.querydslstudy.dto.response.MemberTeamResponse;
import com.querydslstudy.entity.Member;
import com.querydslstudy.entity.QMember;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;
import static com.querydsl.core.types.Projections.constructor;
import static com.querydslstudy.entity.QMember.member;
import static com.querydslstudy.entity.QTeam.team;
import static com.querydslstudy.repository.MemberSearchCondition.buildCondition;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private static final Logger log = LoggerFactory.getLogger(MemberRepositoryImpl.class);
    private final JPAQueryFactory queryFactory;

    @Override
    public List<MemberTeamResponse> searchByCondition(MemberSearchRequest request) {
        log.debug("🔍 [QueryDSL] searchByCondition 호출 - 조건: username={}, ageGoe={}, ageLoe={}, teamName={}, status={}",
                request.username(), request.ageGoe(), request.ageLoe(), request.teamName(), request.status());
        
        List<MemberTeamResponse> result = queryFactory
                .select(constructor(MemberTeamResponse.class,
                        member.id,
                        member.username,
                        member.age,
                        team.id,
                        team.name,
                        member.status.stringValue()))
                .from(member)
                .leftJoin(member.team, team)
                .where(buildCondition(request))
                .fetch();
        
        log.debug("✅ [QueryDSL] searchByCondition 완료 - 조회된 결과 수: {}", result.size());
        return result;
    }

    @Override
    public List<Member> findMembersWithTeam() {
        log.debug("🔍 [QueryDSL] findMembersWithTeam 호출 - Team과 조인");
        List<Member> result = queryFactory
                .selectFrom(member)
                .join(member.team, team)
                .fetchJoin()
                .fetch();
        log.debug("✅ [QueryDSL] findMembersWithTeam 완료 - 조회된 회원 수: {}", result.size());
        return result;
    }

    @Override
    public List<Member> findMembersOlderThanAverage() {
        log.debug("🔍 [QueryDSL] findMembersOlderThanAverage 호출 - 평균 나이보다 많은 회원 조회 (서브쿼리 사용)");
        QMember subMember = new QMember("subMember");
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.gt(
                        queryFactory
                                .select(subMember.age.avg())
                                .from(subMember)
                ))
                .fetch();
        log.debug("✅ [QueryDSL] findMembersOlderThanAverage 완료 - 조회된 회원 수: {}", result.size());
        return result;
    }

    @Override
    public List<Member> findMembersWithPaging(int offset, int limit) {
        log.debug("🔍 [QueryDSL] findMembersWithPaging 호출 - offset: {}, limit: {}", offset, limit);
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.id.desc())
                .offset(offset)
                .limit(limit)
                .fetch();
        log.debug("✅ [QueryDSL] findMembersWithPaging 완료 - 조회된 회원 수: {}", result.size());
        return result;
    }

    @Override
    public Page<MemberTeamResponse> searchByConditionWithPaging(MemberSearchRequest request, Pageable pageable) {
        log.debug("🔍 [QueryDSL] searchByConditionWithPaging 호출 - page: {}, size: {}", 
                pageable.getPageNumber(), pageable.getPageSize());

        List<MemberTeamResponse> content = queryFactory
                .select(constructor(MemberTeamResponse.class,
                        member.id,
                        member.username,
                        member.age,
                        team.id,
                        team.name,
                        member.status.stringValue()))
                .from(member)
                .leftJoin(member.team, team)
                .where(buildCondition(request))
                .orderBy(member.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(member.count())
                .from(member)
                .leftJoin(member.team, team)
                .where(buildCondition(request))
                .fetchOne();

        log.debug("✅ [QueryDSL] searchByConditionWithPaging 완료 - 조회된 결과 수: {}, 전체: {}", 
                content.size(), total);

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    @Override
    public Long countMembersByTeam(Long teamId) {
        log.debug("🔍 [QueryDSL] countMembersByTeam 호출 - teamId: {} (집계 함수 사용)", teamId);
        Long result = queryFactory
                .select(member.count())
                .from(member)
                .where(member.team.id.eq(teamId))
                .fetchOne();
        log.debug("✅ [QueryDSL] countMembersByTeam 완료 - 팀별 회원 수: {}", result);
        return result;
    }
}

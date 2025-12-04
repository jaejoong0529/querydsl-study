package com.querydslstudy.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydslstudy.dto.request.MemberSearchRequest;
import com.querydslstudy.entity.MemberStatus;

import static com.querydsl.core.types.dsl.Expressions.allOf;
import static com.querydslstudy.entity.QMember.member;
import static com.querydslstudy.entity.QTeam.team;

public class MemberSearchCondition {


    public static BooleanExpression buildCondition(MemberSearchRequest request) {
        return allOf(
                usernameEq(request.username()),
                ageGoe(request.ageGoe()),
                ageLoe(request.ageLoe()),
                teamNameEq(request.teamName()),
                statusEq(request.status())
        );
    }

    private static BooleanExpression usernameEq(String username) {
        return username != null && !username.isBlank() 
                ? member.username.eq(username) 
                : null;
    }

    private static BooleanExpression ageGoe(Integer ageGoe) {
        return ageGoe != null ? member.age.goe(ageGoe) : null;
    }

    private static BooleanExpression ageLoe(Integer ageLoe) {
        return ageLoe != null ? member.age.loe(ageLoe) : null;
    }

    private static BooleanExpression teamNameEq(String teamName) {
        return teamName != null && !teamName.isBlank() 
                ? team.name.eq(teamName) 
                : null;
    }

    private static BooleanExpression statusEq(MemberStatus status) {
        return status != null ? member.status.eq(status) : null;
    }
}

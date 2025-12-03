package com.querydslstudy.dto.request;

import com.querydslstudy.entity.MemberStatus;

public record MemberSearchRequest(
        String username,
        Integer ageGoe,
        Integer ageLoe,
        String teamName,
        MemberStatus status
) {
}

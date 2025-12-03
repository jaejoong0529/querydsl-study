package com.querydslstudy.dto.response;

public record MemberTeamResponse(
        Long memberId,
        String username,
        Integer age,
        Long teamId,
        String teamName,
        String status
) {
}

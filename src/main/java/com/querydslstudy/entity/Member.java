package com.querydslstudy.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private Integer age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    private Member(String username, Integer age, MemberStatus status) {
        this.username = username;
        this.age = age;
        this.status = status;
    }

    public static Member of(String username, Integer age) {
        return new Member(username, age, MemberStatus.ACTIVE);
    }

    public void changeTeam(Team team) {
        this.team = team;
    }
}

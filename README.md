# QueryDSL 학습용 프로젝트

QueryDSL을 학습하며 실무에서 자주 사용되는 Custom Repository 패턴과 동적 쿼리 처리 방식을 정리한 프로젝트입니다.
JPA만으로 해결하기 어려운 복잡한 쿼리를 QueryDSL로 어떻게 깔끔하게 풀어낼 수 있는지에 초점을 맞췄습니다.

## 주요 특징

*Custom Repository 구조: JpaRepository와 QueryDSL 구현체를 함께 사용하는 표준 패턴 적용

* Entity 설계: 정적 팩토리 메서드(of)와 private 생성자를 통해 불변성 및 객체 생성 제약 강화

* DTO 관리: Java 17 Record를 활용하여 간결한 DTO 구성 및 프로젝션 매핑

* 동적 쿼리 최적화: BooleanExpression을 별도 클래스로 분리하여 WHERE 절의 가독성과 재사용성 확보

* Paging: Pageable 인터페이스를 지원하는 페이징 처리 및 Count 쿼리 최적화

* SQL 로깅: P6Spy를 적용하여 실행된 쿼리 파라미터 바인딩 확인 용이

## 프로젝트 구조

```
src/main/java/com/querydslstudy/
├── entity/              # Member, Team (정적 팩토리 메서드 적용)
├── dto/                 # Java Record 활용 (Request/Response)
├── repository/          
│   ├── MemberRepository.java          # JPA + Custom Interface
│   ├── MemberRepositoryImpl.java     # QueryDSL 실제 구현부
│   └── MemberSearchCondition.java    # 동적 쿼리 조건(BooleanExpression) 분리
└── config/              # QuerydslConfig (JPAQueryFactory 빈 등록)
```

## 핵심 패턴

### 1. Custom Repository (JPA + QueryDSL)
실무에서 가장 많이 쓰는 방식으로, 기본 CRUD는 JPA에게 맡기고 복잡한 조회만 QueryDSL로 분리했습니다.

```
// 사용처에서는 MemberRepository 하나만 주입받아서 사용
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
}
```

### 2. 동적 쿼리 가독성 개선 (BooleanExpression)
BooleanBuilder 대신 BooleanExpression을 사용하여 WHERE 절이 한눈에 들어오도록 구성했습니다. 
null 반환 시 해당 조건이 무시되는 특성을 활용합니다.

```
// MemberRepositoryImpl.java
.where(
    MemberSearchCondition.usernameEq(request.username()),
    MemberSearchCondition.ageGoe(request.ageGoe()),
    // ... 조건을 콤마(,)로 연결하여 가독성 확보
)
```
### 3. Record를 활용한 DTO 프로젝션

```
public record MemberTeamResponse(Long memberId, String username, ...) {}
// QueryDSL이 생성자를 통해 바로 매핑
```

## 구현된 예제 (테스트 코드 참조)
MemberRepositoryTest에서 아래 기능들의 실제 동작을 확인할 수 있습니다.

* 기본 조회: 단순 필터링, 정렬

* 동적 검색: 검색 조건이 있거나 없을 때(null) 동적으로 쿼리 생성

* 페이징: Pageable을 통한 페이징 및 Total Count 쿼리

* 연관관계 조회: Fetch Join을 통한 N+1 문제 해결

* 집합: GroupBy, Having 등을 활용한 통계 쿼리

## 📚 Tech Stack
* Java 17

* Spring Boot 3.5.3

* QueryDSL 5.0.0

* MySQL 8.0

* P6Spy (Dev profile)

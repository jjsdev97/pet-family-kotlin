# Pet Family Kotlin

Kotlin으로 반려동물과 보호자의 관계를 모델링한 연습 프로젝트입니다.

## 주요 기능

- 사람 생성
- 강아지/고양이 입양
- 반려동물 소유자 변경
- 부모 반려동물을 통한 자식 생성
- 순종/믹스 품종 비율 계산
- 인메모리 저장소와 JPA/H2 저장소 예제

## 기술 스택

- Kotlin JVM 2.0.21
- Gradle
- Jakarta Persistence
- Hibernate ORM
- H2 Database
- Kotlin Test

## 실행

```bash
./gradlew run
```

JPA/H2 예제 실행:

```bash
./gradlew runJpa
```

테스트 실행:

```bash
./gradlew test
```

JPA 테스트 실행:

```bash
./gradlew jpaTest
```

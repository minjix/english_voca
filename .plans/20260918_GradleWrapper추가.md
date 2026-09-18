# Gradle Wrapper 추가 (맥 배포 대비)

## Context
프로젝트에 Gradle Wrapper가 없어서, 맥에 풀 받으면 Gradle이 별도로 설치돼 있어야 빌드가 가능함.
Wrapper를 추가하면 OS/설치 여부와 무관하게 항상 같은 Gradle 버전으로 빌드/실행 가능.

## 작업 목록
- [x] `gradle wrapper --gradle-version 8.7` 실행 (gradlew, gradlew.bat, gradle/wrapper/gradle-wrapper.jar, gradle-wrapper.properties 생성)
- [x] `./gradlew compileJava`로 정상 동작 확인 (BUILD SUCCESSFUL, Gradle 8.7 자동 다운로드까지 확인)
- [x] 생성된 파일 목록 확인 (커밋 대상)

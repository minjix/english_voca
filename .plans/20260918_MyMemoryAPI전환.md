# 영단어 뜻 조회: Free Dictionary API → MyMemory Translation API 완전 교체

## Context
기존 파이프라인은 DictionaryClient(Free Dictionary API로 뜻+예문 조회) → TranslationClient.translate()(뜻 텍스트 영→한 번역)의 2단계였음.
DictionaryClient(Free Dictionary API)를 완전히 제거하고, 단어 자체를 MyMemory Translation API로 직접 영→한 번역해 뜻으로 사용. 예문(example)은 더 이상 사용하지 않음.

## 작업 목록
- [x] WordEntry.java: definitions(List)+example → meaning(단일 String)으로 교체
- [x] TranslationClient.java: translate(String) → lookup(String) -> Optional<WordEntry>로 재구성
- [x] DictionaryClient.java 삭제
- [x] WordSelector.java: DictionaryClient 파라미터 → TranslationClient로 변경
- [x] Main.java: DictionaryClient 생성/주입 코드 제거
- [x] KakaoClient.java: 예문 라인 제거, meaning 단일 출력으로 변경
- [x] 프로젝트 CLAUDE.md "단어 소스" 섹션 등 예문 관련 문구 갱신
- [x] gradle compileJava로 컴파일 확인 (BUILD SUCCESSFUL)

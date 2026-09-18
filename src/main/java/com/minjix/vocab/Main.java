package com.minjix.vocab;

import com.minjix.vocab.model.AppState;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("영단어 알림 시작...");

            StateManager stateManager = new StateManager();
            AppState state = stateManager.load();

            TranslationClient translationClient = new TranslationClient();
            WordSelector selector = new WordSelector();
            WordSelector.Result result = selector.selectWords(state.getNextIndex(), 5, translationClient);

            if (result.words().isEmpty()) {
                throw new RuntimeException("번역에 성공한 유효한 단어를 찾지 못했습니다.");
            }

            TokenManager tokenManager = new TokenManager();
            String accessToken = tokenManager.refreshAccessToken();

            KakaoClient kakaoClient = new KakaoClient();
            kakaoClient.sendToMe(accessToken, result.words());

            state.setNextIndex(result.nextIndex());
            stateManager.save(state);

            System.out.println("완료. 전송 단어 수: " + result.words().size() + ", 다음 인덱스: " + result.nextIndex());
        } catch (Exception e) {
            System.err.println("[ERROR] 실행 실패: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

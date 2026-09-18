package com.minjix.vocab;

import com.minjix.vocab.model.WordEntry;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WordSelector {
    private final List<String> wordList;

    public WordSelector() throws Exception {
        this.wordList = loadWords();
    }

    // startIndex부터 순환하며 번역에 성공한 단어 count개를 선택
    public Result selectWords(int startIndex, int count, TranslationClient translationClient) {
        List<WordEntry> selected = new ArrayList<>();
        int index = startIndex % wordList.size();
        int checked = 0;

        while (selected.size() < count && checked < wordList.size()) {
            String word = wordList.get(index);
            Optional<WordEntry> entry = translationClient.lookup(word);
            entry.ifPresent(selected::add);
            index = (index + 1) % wordList.size();
            checked++;
        }

        return new Result(selected, index);
    }

    private List<String> loadWords() throws Exception {
        List<String> words = new ArrayList<>();
        try (InputStream is = getClass().getResourceAsStream("/words.csv");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) { header = false; continue; }
                line = line.trim();
                if (!line.isEmpty()) words.add(line);
            }
        }
        return words;
    }

    public record Result(List<WordEntry> words, int nextIndex) {}
}

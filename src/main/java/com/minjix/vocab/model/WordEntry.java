package com.minjix.vocab.model;

public class WordEntry {
    private final String word;
    private final String meaning;

    public WordEntry(String word, String meaning) {
        this.word = word;
        this.meaning = meaning;
    }

    public String getWord() { return word; }
    public String getMeaning() { return meaning; }
}

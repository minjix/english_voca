package com.minjix.vocab;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minjix.vocab.model.AppState;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

public class StateManager {
    private final File stateFile;
    private final ObjectMapper mapper = new ObjectMapper();

    public StateManager() throws Exception {
        Properties config = loadConfig();
        this.stateFile = new File(config.getProperty("state.file.path"));
    }

    public AppState load() {
        if (!stateFile.exists()) {
            return new AppState(); // 최초 실행
        }
        try {
            return mapper.readValue(stateFile, AppState.class);
        } catch (Exception e) {
            System.err.println("[StateManager] 상태 로드 실패, 초기 상태로 시작: " + e.getMessage());
            return new AppState();
        }
    }

    public void save(AppState state) throws Exception {
        mapper.writerWithDefaultPrettyPrinter().writeValue(stateFile, state);
    }

    private Properties loadConfig() throws Exception {
        Properties props = new Properties();
        try (InputStream is = getClass().getResourceAsStream("/config.properties")) {
            props.load(is);
        }
        return props;
    }
}

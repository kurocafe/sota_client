package jp.ha.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import jp.ha.JSON.JSONMapper;

public class JsonIo {
    public static <T> T load(Path filePath, Class<T> klass) {
        T jsonData = null;
        if (!Files.exists(filePath)) {
            return jsonData;
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(filePath);
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                sb.append(line);
            }
            jsonData = JSONMapper.mapper.readValue(sb.toString(), klass);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonData;
    }

    public static void dump(Path outPath, Object jsonData) {
        String content;
        try {
            content = JSONMapper.mapper.writeValueAsString(jsonData);
            Files.write(outPath, content.getBytes(), StandardOpenOption.CREATE);
            System.out.println("ファイルへの書き込みが完了しました。");
        } catch (JsonProcessingException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
    }
}

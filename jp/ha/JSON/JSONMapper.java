package jp.ha.JSON;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

final public class JSONMapper {
    public static final ObjectMapper mapper = new ObjectMapper();

    /**
     * JSON文字列をMapに
     *
     * @param json json文字列
     * @return json文字列を読み込んだMapオブジェクト。失敗した場合はnull
     */
    public static Map<String, Object> jsonStringToMap(String json) {
        Map<String, Object> map = null;

        try {
            // キーがString、値がObjectのマップに読み込みます。
            map = JSONMapper.mapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            // エラー！
            e.printStackTrace();
        }

        return map;
    }
}

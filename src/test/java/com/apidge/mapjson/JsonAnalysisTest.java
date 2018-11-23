package com.apidge.mapjson;

import org.junit.Test;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class JsonAnalysisTest {
    @Test
    public void testParseJsonFields1() {
        String jsonStr = "{\"store\":{\"book\":[{\"category\":\"reference\",\"author\":\"NigelRees\",\"title\":\"SayingsoftheCentury\",\"price\":\"8.95\"},{\"category\":\"fiction\",\"author\":\"EvelynWaugh\",\"title\":\"SwordofHonour\",\"price\":\"12.99\"},{\"category\":\"fiction\",\"author\":\"HermanMelville\",\"title\":\"MobyDick\",\"isbn\":\"0-553-21311-3\",\"price\":\"8.99\"},{\"category\":\"fiction\",\"author\":\"J.R.R.Tolkien\",\"title\":\"TheLordoftheRings\",\"isbn\":\"0-395-19395-8\",\"price\":\"22.99\"}],\"bicycle\":{\"color\":\"red\",\"price\":\"19.95\"}},\"expensive\":\"10\"}";
        Set<String> fields = JsonAnalysis.parseJsonFields(jsonStr);
        String idField = ".store.book:.title";
        Map<String, JsonDataMap> jsonDataMap = JsonDataMap.parseFromJson(jsonStr, idField, fields);
        assert JsonBuilder.buildJson(jsonDataMap).toString().length() == jsonStr.length();
    }

    @Test
    public void testParseFullJson() {
        String jsonStr = "{\"store\": {\"book\": [{\"category\": \"reference\",\"author\": \"Nigel Rees\",\"title\": \"Sayings of the Century\",\"price\": 8.95},{\"category\": \"fiction\",\"author\": \"Evelyn Waugh\",\"title\": \"Sword of Honour\",\"price\": 12.99},{\"category\": \"fiction\",\"author\": \"Herman Melville\",\"title\": \"Moby Dick\",\"isbn\": \"0-553-21311-3\",\"price\": 8.99},{\"category\": \"fiction\",\"author\": \"J. R. R. Tolkien\",\"title\": \"The Lord of the Rings\",\"isbn\": \"0-395-19395-8\",\"price\": 22.99}],\"bicycle\": {\"color\": \"red\",\"price\": 19.95}},\"expensive\": 10}";
        Set<String> fields1 = JsonAnalysis.parseJsonFields(jsonStr);
        Set<String> fields2 = JsonAnalysis.parseJsonFields(JsonAnalysis.parseFullJson(jsonStr, true).toString());
        for (String f1 : fields1) {
            assert fields2.contains(f1);
        }
    }

    @Test
    public void testBuildJson() {
        String jsonStr = "{\"id\":\"1\"}";
        Set<String> fields = JsonAnalysis.parseJsonFields(jsonStr);
        String idField = ".id";
        Map<String, JsonDataMap> jsonDataMap = JsonDataMap.parseFromJson(jsonStr, idField, fields);
        assert Objects.equals(jsonStr, JsonBuilder.buildJson(jsonDataMap).toString());
    }

    @Test
    public void testParseJsonFields() {
        String jsonStr = "[{\"id\":1},{\"id\":2},{\"id\":3},{\"id\":4}]";
        Set<String> fields = JsonAnalysis.parseJsonFields(jsonStr);
        assert fields.size() == 1;
        for (String f : fields) {
            assert f.equals(":.id");
        }
    }
}
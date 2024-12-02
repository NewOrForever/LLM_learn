package org.example.advancedrag;

import dev.langchain4j.rag.content.Content;

import java.util.*;

import static dev.langchain4j.internal.ValidationUtils.ensureBetween;

/**
 * ClassName:Test
 * Package:org.example.advancedrag
 * Description:
 *
 * @Date:2024/11/28 15:32
 * @Author:qs@1.com
 */
public class TestDefaultContentAggregator {
    public static void main(String[] args) {
        Collection<List<Content>> listsOfContents = new ArrayList<>();

        List<Content> list1 = new ArrayList<>();
        list1.add(Content.from("cat"));
        list1.add(Content.from("dog"));
        list1.add(Content.from("hamster"));
        List<Content> list2 = new ArrayList<>();
        list2.add(Content.from("cat"));
        list2.add(Content.from("parrot"));

        listsOfContents.add(list1);
        listsOfContents.add(list2);

        fuse(listsOfContents, 60).forEach(System.out::println);
    }

    public static List<Content> fuse(Collection<List<Content>> listsOfContents, int k) {
        ensureBetween(k, 1, Integer.MAX_VALUE, "k");

        Map<Content, Double> scores = new LinkedHashMap<>();
        for (List<Content> singleListOfContent : listsOfContents) {
            for (int i = 0; i < singleListOfContent.size(); i++) {
                Content content = singleListOfContent.get(i);
                double currentScore = scores.getOrDefault(content, 0.0);
                int rank = i + 1;
                double newScore = currentScore + 1.0 / (k + rank);
                scores.put(content, newScore);
            }
        }

        List<Content> fused = new ArrayList<>(scores.keySet());
        // 根据分数降序排序
        fused.sort(Comparator.comparingDouble(scores::get).reversed());
        return fused;
    }

}

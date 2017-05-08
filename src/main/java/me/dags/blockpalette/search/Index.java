package me.dags.blockpalette.search;

import com.google.common.collect.ImmutableList;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dags <dags@dags.me>
 */
public class Index<T> {

    private final List<Entry<T>> entries;

    private Index(List<Entry<T>> entries) {
        this.entries = entries;
    }

    public List<T> search(String input, int limit) {
        return parallelSearch(input, limit).collect(Collectors.toList());
    }

    public Stream<T> parallelSearch(String input, int limit) {
        if (input.isEmpty()) {
            return Stream.empty();
        }

        String[] split = input.split(" ");

        StringBuilder builder = new StringBuilder();
        for (String token : split) {
            builder.append("(?=.*").append(Pattern.quote(token)).append(")");
        }

        Pattern pattern = Pattern.compile(builder.toString());
        Predicate<Entry> filter = entry -> pattern.matcher(entry.key).find();
        Comparator<Entry> sorter = sorter(input.toLowerCase());

        return entries.parallelStream()
                .filter(filter)
                .sorted(sorter)
                .limit(limit)
                .map(Entry::getValue);
    }

    private Comparator<Entry> sorter(String input) {
        return (e1, e2) -> {
            if (e1.getKey().startsWith(input)) {
                if (e2.getKey().startsWith(input)) {
                    int l1 = e1.getKey().length();
                    int l2 = e2.getKey().length();
                    return l1 == l2 ? 0 : l1 > l2 ? 1 : -1;
                }
                return -1;
            }
            return 0;
        };
    }

    private static class Entry<T> implements Comparable<Entry<T>> {

        private final T value;
        private final String key;

        private Entry(String key, T value) {
            this.value = value;
            this.key = key;
        }

        private String getKey() {
            return key;
        }

        private T getValue() {
            return value;
        }

        @Override
        public int compareTo(Entry<T> t) {
            return key.compareTo(t.key);
        }
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {

        private final Map<String, T> mappings = new HashMap<>();

        public Builder<T> with(T value) {
            return with(value.toString(), value);
        }

        public Builder<T> with(String key, T value) {
            mappings.put(key.toLowerCase(), value);
            return this;
        }

        public Index<T> build() {
            List<Entry<T>> list = new LinkedList<>();
            for (Map.Entry<String, T> entry : mappings.entrySet()) {
                list.add(new Entry<>(entry.getKey(), entry.getValue()));
            }
            return new Index<>(ImmutableList.copyOf(list));
        }
    }
}
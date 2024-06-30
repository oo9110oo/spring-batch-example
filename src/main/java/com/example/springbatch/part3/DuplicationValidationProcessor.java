package com.example.springbatch.part3;

import org.springframework.batch.item.ItemProcessor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class DuplicationValidationProcessor<T> implements ItemProcessor<T, T> {

    private final Map<String, Object> keyPool = new ConcurrentHashMap<>();
    private final Function<T, String> keyExtractor;
    private final boolean allowDuplicate;

    public DuplicationValidationProcessor(Function<T, String> keyExtractor, boolean allowDuplicate) {
        this.keyExtractor = keyExtractor;
        this.allowDuplicate = allowDuplicate;
    }

    @Override
    public T process(T item) throws Exception {
        if (allowDuplicate) {
            return item;
        }

        String key = keyExtractor.apply(item);      // Function에 값을 넣어준다.

        if (keyPool.containsKey(key)) {     // 중복된 값을 제거한다.
            return null;
        }

        keyPool.put(key, key);

        return item;
    }
}

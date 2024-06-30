package com.example.springbatch.part3;

import org.springframework.aop.support.AopUtils;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.ArrayList;
import java.util.List;

public class CustomItemReader<T> implements ItemReader<T> {

    private final List<T> items;

    public CustomItemReader(List<T> items) {
        if (AopUtils.isAopProxy(items)) {
            this.items = items;
        } else {
            this.items = new ArrayList<>(items);
        }
    }

    @Override
    public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (!items.isEmpty()) {
            return items.remove(0);
        }
        // null 을 return하면 Reader를 끝낸다
        return null;
    }
}

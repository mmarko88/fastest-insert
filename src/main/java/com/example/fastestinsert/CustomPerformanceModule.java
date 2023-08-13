package com.example.fastestinsert;

import com.p6spy.engine.event.JdbcEventListener;
import com.p6spy.engine.spy.P6Factory;
import com.p6spy.engine.spy.P6LoadableOptions;
import com.p6spy.engine.spy.option.P6OptionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CustomPerformanceModule implements P6Factory {

    private final P6EventListener p6EventListener;

    @Override
    public P6LoadableOptions getOptions(P6OptionsRepository optionsRepository) {
        return null;
    }

    @Override
    public JdbcEventListener getJdbcEventListener() {
        return p6EventListener;
    }
}

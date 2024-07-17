package io.github.yaojiqunaer;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import io.github.yaojiqunaer.entity.LargeData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LargeDataListener extends AnalysisEventListener<LargeData> {

    private int count = 0;

    @Override
    public void invoke(LargeData data, AnalysisContext context) {
        if (count == 0) {
            log.info("First row:{}", data);
        }
        count++;
        if (count % 100000 == 0) {
            log.info("Already read:{},{}", count, data);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("Large row count:{}", count);
    }
}

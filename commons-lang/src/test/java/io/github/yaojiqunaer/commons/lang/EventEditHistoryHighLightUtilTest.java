package io.github.yaojiqunaer.commons.lang;

import org.junit.jupiter.api.Test;

class EventEditHistoryHighLightUtilTest {

    @Test
    void getHighLightDifferentOld() {
        String a = "你好 张三\n张三：你好";
        String b = "你很好 张三brother\n张山：nice";
        String[] diff = EventEditHistoryHighLightUtil.getHighLightDifferentOld(a, b);
        System.out.println(diff[0]);
        System.out.println(diff[1]);
    }

    @Test
    void getDiff() {
        String a = "你好 张三\n张三：你好";
        String b = "你很好 张三brother\n张山：nice";
        String[] diff = EventEditHistoryHighLightUtil.getDiff(a, b);
        System.out.println(diff[0]);
        System.out.println(diff[1]);
    }

    @Test
    void getHighLightDifferent() {
        String a = "你好 张三\n张三：你好";
        String b = "你很好 张三brother\n张山：nice";
        String[] diff = EventEditHistoryHighLightUtil.getHighLightDifferent(a, b);
        System.out.println(diff[0]);
        System.out.println(diff[1]);
    }
}
package io.github.yaojiqunaer;

import com.alibaba.excel.EasyExcel;
import io.github.yaojiqunaer.entity.LargeData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

    final static String DEFAULT_NAME = "zhangshan";

    public static void main(String[] args) throws InterruptedException {
        String fileName = "/Users/xiaodongzhang/IdeaProjects/github/java-technology-stack/sample/spring-easyexcel" +
                "-integration/src/main/resources/large07.xlsx";
        EasyExcel.read(fileName, LargeData.class, new LargeDataListener()).sheet(0).doRead();
    }

}
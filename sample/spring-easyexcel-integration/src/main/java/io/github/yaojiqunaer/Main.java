package io.github.yaojiqunaer;

import com.alibaba.excel.EasyExcel;
import io.github.yaojiqunaer.entity.IndexOrNameData;
import io.github.yaojiqunaer.entity.LargeData;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class Main {

    final static String DEFAULT_NAME = "zhangshan";

    public static void main(String[] args) throws InterruptedException {
        String fileName = "/Users/xiaodongzhang/IdeaProjects/github/java-technology-stack/sample/spring-easyexcel" +
                "-integration/src/main/resources/demo.xlsx";
        EasyExcel.read(fileName, LargeData.class, new LargeDataListener()).sheet(0).doRead();
        // List 写入一个文件
        IndexOrNameData indexOrNameData = new IndexOrNameData();
        indexOrNameData.setName(DEFAULT_NAME);
        indexOrNameData.setDoubleData(DEFAULT_NAME);
        indexOrNameData.setString(DEFAULT_NAME);
        EasyExcel.write("/Users/xiaodongzhang/IdeaProjects/github/java-technology-stack/sample/spring-easyexcel" +
                        "-integration/src/main/resources/write.xlsx",
                IndexOrNameData.class).sheet("模板").doWrite(List.of(indexOrNameData));
    }

}
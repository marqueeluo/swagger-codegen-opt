import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.luo.demo.swagger.codegen.convertor.MustacheEngine;
import lombok.Builder;
import lombok.Data;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * mustache模版 - 测试
 *
 * @author luohq
 * @date 2021-09-19 20:30
 */
public class MustacheTest {

    public static void main(String[] args) throws Throwable {
        testMustache();
    }

    public static void testMustache() throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        //main.mustache和familiesDesc.mustache需放在同一目录，如均在resources/mustache目录下
        Mustache mustache = mf.compile("mustache/main.mustache");
        mustache.execute(new PrintWriter(System.out), buildIntro()).flush();
        Writer fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("D:/test.txt"), "UTF-8"));
        mustache.execute(fileWriter, buildIntro()).flush();
    }


    public static Intro buildIntro() {
        return Intro.builder()
                .name("Luo")
                .desc("<b>Software Engineer</b>")
                .married(true)
                .families(Arrays.asList(
                        FamilyMember.builder().name("emma").relation("wife").build(),
                        FamilyMember.builder().name("siye").relation("father").build(),
                        FamilyMember.builder().name("amily").relation("mother").build()
                ))
                //Sections Lambda需借助Java8 Function实现
                .wrapped((obj) -> String.format("<b>%s - 重要的事情说三遍</b>", obj))
                .build();
    }

    @Data
    @Builder
    public static class Intro {
        private String name;
        private String desc;
        private Boolean married;
        private List<FamilyMember> families;
        //Sections Lambda需借助Java8 Function实现
        private Function<Object, Object> wrapped;
    }

    @Data
    @Builder
    public static class FamilyMember {
        private String name;
        private String relation;
    }
}

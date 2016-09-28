package spring.annotation.examples;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by chokst on 11/23/14.
 */
public class SampleConfigClient {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(SampleConfig.class);
        SampleService sampleService = (SampleService)applicationContext.getBean("sampleService");
        System.out.println("Using SampleService:"+sampleService.getName());

        AnotherService anotherService =  (AnotherService)applicationContext.getBean("anotherService");
        System.out.println(String.format("Using AnotherService:%s", anotherService.getName()));
    }
}

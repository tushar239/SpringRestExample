package spring.annotation.examples;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

// http://www.java-allandsundry.com/2013/07/spring-bean-and-propertyplaceholderconf.html

@Configuration
//@Import(SomeOtherConfigClass.class) // To import another configuration class
//@ImportResource("classpath:SomeOtherConfiguration.xml") // To import configuration from xml
@PropertySources(value={
        @PropertySource(value="classpath:test.properties")
})
@ComponentScan //(basePackageClasses = ...) // Default package is current package. AnotherService will be added.
public class SampleConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    @Bean
    public SampleService sampleService() {
        SampleService sampleService = new SampleService();
        return sampleService;
    }

    @Bean
    public MyDao myDao() {
        MyDao myDao = new MyDao();
        return myDao;
    }
}

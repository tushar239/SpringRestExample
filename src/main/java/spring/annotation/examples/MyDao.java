package spring.annotation.examples;

import org.springframework.beans.factory.annotation.Value;

/**
 * Created by chokst on 11/23/14.
 */
public class MyDao {
    @Value("${test.nameSuffix}")
    private String nameSuffix;


    public String getName() {
        return "Tushar "+nameSuffix;
    }
}

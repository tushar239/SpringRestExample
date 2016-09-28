package spring.annotation.examples;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes={SampleConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class SampleConfigTest {

}
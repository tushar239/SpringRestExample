package spring.annotation.examples;

import javax.annotation.Resource;

/**
 * Created by chokst on 11/23/14.
 */
public class SampleService {
    @Resource
    private MyDao myDao;

    public String getName() {
        return myDao.getName();
    }
}

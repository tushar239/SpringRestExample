package spring.annotation.examples;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by chokst on 11/23/14.
 */

@Service
public class AnotherService {
    @Resource
    private MyDao myDao;

    public String getName() {
        return myDao.getName();
    }
}

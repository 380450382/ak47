package com.ak47.plugins.dao.demo;

import com.ak47.plugins.dao.DemoDao;
import com.ak47.plugins.model.Demo;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DemoDaoImpl implements DemoDao {
    @Override
    public Demo select(String name) {
        Date now = new Date();
        Demo demo = new Demo();
        demo.setName(name);
        demo.setId(1L);
        demo.setCreatedDate(now);
        demo.setModifiedDate(now);
        return demo;
    }
}

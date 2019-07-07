package com.ak47.plugins.service.impl;

import com.ak47.plugins.dao.DemoDao;
import com.ak47.plugins.model.DTO.DemoDTO;
import com.ak47.plugins.model.Demo;
import com.ak47.plugins.service.DemoService;
import com.ak47.plugins.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DemoServiceImpl implements DemoService {
    private static final Logger logger = LoggerFactory.getLogger(DemoServiceImpl.class);
    @Autowired
    private DemoDao demoDao;
    @Override
    public DemoDTO say(String name) {
        Demo demo = demoDao.select(name);
        logger.info("{}运行","DemoServiceImpl#say");
        return ObjectUtils.setTbyObj(DemoDTO.class,demo);
    }
    @Override
    public DemoDTO hello(String name) {
        Demo demo = demoDao.select(name);
        logger.info("{}运行","DemoServiceImpl#hello");
        return ObjectUtils.setTbyObj(DemoDTO.class,demo);
    }
}

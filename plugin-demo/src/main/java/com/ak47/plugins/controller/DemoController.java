package com.ak47.plugins.controller;


import com.ak47.plugins.model.DTO.DemoDTO;
import com.ak47.plugins.model.VO.DemoVO;
import com.ak47.plugins.service.DemoService;
import com.ak47.plugins.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("demo")
public class DemoController {

    @Autowired
    private DemoService demoService;

    @GetMapping("say")
    public DemoVO say(String name){
        DemoDTO demoDTO = demoService.say(name);
        return  ObjectUtils.setTbyObj(DemoVO.class,demoDTO);
    }

    @GetMapping("hello")
    public DemoVO hello(String name){
        DemoDTO demoDTO = demoService.hello(name);
        return  ObjectUtils.setTbyObj(DemoVO.class,demoDTO);
    }

}

package com.ak47.plugins.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("resource/control")
public class ResourceController {

    @GetMapping("")
    public String control() {
        return "control/main";
    }

    @GetMapping("resource")
    public String resource() {
        return "control/resource";
    }
    @GetMapping("addResource")
    public String addResource() {
        return "control/add_resource";
    }
}

package com.ak47.plugins.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("plugin/control")
public class PluginController {

    @GetMapping("")
    public String control() {
        return "control/main";
    }

    @GetMapping("resource")
    public String resource() {
        return "control/resource";
    }

    @GetMapping("cache")
    public String cache() {
        return "control/cache";
    }
}

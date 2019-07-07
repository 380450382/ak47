package com.ak47.plugins.service;

import com.ak47.plugins.model.DTO.DemoDTO;

public interface DemoService {
    DemoDTO say(String name);
    DemoDTO hello(String name);
}

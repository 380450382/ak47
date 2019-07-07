package com.ak47.plugins.dao;

import com.ak47.plugins.model.Demo;

public interface DemoDao {
    Demo select(String name);
}

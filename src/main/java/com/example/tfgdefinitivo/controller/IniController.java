package com.example.tfgdefinitivo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IniController {
    @RequestMapping("/")
    public String index() {
        return "index";
    }
}

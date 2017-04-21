package com.ninjabooks.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Main controller is responsible to handle request with <b>'/'</b>
 * and render the request to index.html
 *
 * @author Piotr 'pitrecki' Nowak
 * @since 1.0
 */
@Controller
public class MainController
{
    @RequestMapping(value = "/")
    public String index() {
        return "index";
    }
}
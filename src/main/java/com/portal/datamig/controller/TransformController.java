package com.portal.datamig.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.portal.datamig.service.ReadService;
import com.portal.datamig.service.TransformService;

@Controller
@RequestMapping("/api")
public class TransformController {
    @Autowired
    ReadService read;
    @Autowired
    TransformService transform;
    @GetMapping("/transform")
    public String transform(Model model) throws IOException{
        model.addAttribute("entities", read.entityList());
        return "transform";
    }
    @GetMapping("/transforming/{name}")
    public String transforming(@PathVariable("name")String name,RedirectAttributes redirectAttributes){
        if(name!=null){
            redirectAttributes.addFlashAttribute("name",name);
        }
        return "transforming";
    }
    @PostMapping("/transforming")
    public String transformingProgress(@RequestParam String entityTransform){
        System.out.println(entityTransform);
        transform.transformList(entityTransform);
        return "redirect:/api/transforming/"+entityTransform;
    }
}

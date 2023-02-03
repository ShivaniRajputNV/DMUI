package com.portal.datamig.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.portal.datamig.service.AuthService;
import com.portal.datamig.service.LoadService;
import com.portal.datamig.service.ReadService;
import com.portal.datamig.service.ValidateService;

@Controller
@RequestMapping("/api")
public class LoadController {
    @Autowired(required = true)
    ReadService read;
    @Autowired(required = true)
    AuthService authService;
    @Autowired
    ValidateService validate;
    @Autowired
    LoadService load;
    
    @GetMapping("/load")
    public String load(Model model) throws IOException{
        model.addAttribute("entities", read.entityList());
        model.addAttribute("recentList", read.recentlyUsed("Sql_Output"));
        return "load";
    }
    @GetMapping("/loadprocess/{name}")
    public String loadprocess(@PathVariable("name") String name, Model model, RedirectAttributes attributes) throws IOException{
        if (name != null) {
            attributes.addFlashAttribute("name", name);
            String entityColor = read.entityList().entrySet().stream().filter(x -> x.getKey().equals(name))
                .map(Map.Entry::getValue).collect(Collectors.joining(", "));
      
            model.addAttribute("col", entityColor);
            model.addAttribute("dbdetails", load.dbDetails());
            System.out.println("bhksa"+load.dbDetails());
          }
        return "loadprocess";
    }

    @GetMapping(value = "/load/process")
    @ResponseBody
    public Map<String, List<String>> validateFiles(@RequestParam String loadEntity, RedirectAttributes attributes,
        Model model)
        throws IOException, InterruptedException {
      Map<String, List<String>> map = load.callLoadProgram(loadEntity);
      System.out.println(map);
      attributes.addFlashAttribute("message", map);
      return map;
    }

    
}
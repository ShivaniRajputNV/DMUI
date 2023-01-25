package com.portal.datamig.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.portal.datamig.service.AuthService;


import com.portal.datamig.service.ReadService;
import com.portal.datamig.service.ValidateService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/api")
public class mainController {
    @Autowired(required = true)
    ReadService read;
    @Autowired(required = true)
    AuthService authService;
    @Autowired
    ValidateService validate;
    private static String lookup="Field_Name,Field_Value";

    
    @GetMapping("/login")
    public String login(){
        return "login";
    } 
    @GetMapping("/primary/{name}")
    public String lookupfile(@PathVariable("name")String name,Model model) throws IOException, Exception{
        Boolean a=true;
        if(read.readCSVFiles(name).isEmpty()){
            a=false;
        }
        System.out.println(a);
        if(name!=null){
            model.addAttribute("entityName",name);
            model.addAttribute("csvfile", read.readCSVFile(name));
            model.addAttribute("boolean", a);
        }
        return "primary";
    }
    @GetMapping("/secondary/{name}")
    public String secondary(@PathVariable("name")String name,Model model){
        if(name!=null){
            model.addAttribute("entityName",name);
            model.addAttribute("csvfiles", read.readCSVFiles(name));
        }
        return "secondary";
    } 
   
    @GetMapping("/lookup")
    public String lookup(Model model) throws IOException{
        model.addAttribute("entities", read.entityList());
        model.addAttribute("recentList", read.recentlyUsed("Lookup"));
        return "lookup";
    }
    
    
    @GetMapping("/load")
    public String load(Model model) throws IOException{
        model.addAttribute("entities", read.entityList());
        return "load";
    }
    @GetMapping("/loadprocess")
    public String loadprocess(){
        return "loadprocess";
    }
    @GetMapping("/main")
    public String main(Model model) throws IOException{
        // Resource resource = new ClassPathResource("/csvs/Global_Lookup.csv");
        File file = new File("../DMUtil/Lookup/GlobalLookup.csv");
        // FileReader filereader = new FileReader(file);
            BufferedReader br = new BufferedReader(new FileReader(file));
            lookup =br.readLine();
            String line;
            List<String[]> allLines = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                String[] splited = line.split("\\s*,\\s*");
                allLines.add(splited);                
            }
             Map<String, String> map = new LinkedHashMap<>();
            for(String[] row : allLines){
                String key = row[0];
                String value = row[1];
                map.put(key, value);
            }    
            model.addAttribute("data", map);
            model.addAttribute("entities", read.entityList());
            model.addAttribute("recentList", read.recentlyUsed("Lookup"));
        return "index.html";
    }
    @GetMapping("/auth")
    public String auth(){
        return "login";
    }
    @PostMapping("/auth")
    public  String authenticate(@RequestParam Map<String, String> data, Model model, RedirectAttributes redirectAttributes, HttpSession httpSession){
        System.out.println(data);
        String result = authService.authenticate(data);
        if(result!=null) {
            redirectAttributes.addFlashAttribute("message", "Successfully Login");
            httpSession.setAttribute("name",org.apache.commons.lang3.StringUtils.capitalize(result));
            return "redirect:/api/main";
        }
        else {
            redirectAttributes.addFlashAttribute("error","Login Again");
            return "redirect:/api/auth";
        }
    }
    @PostMapping("/destroy")
public String destroySession(HttpSession httpSession,HttpServletRequest httpServletRequest) {
 httpSession.invalidate();
 httpSession = httpServletRequest.getSession(false);
 if(httpSession!=null) {
 httpSession.removeAttribute("name");
 }
return "redirect:/api/auth";
 }
//download sample files
 @GetMapping("/download/{name}")
    public String downloadAllFilesByDropdown(@PathVariable("name") String name,HttpServletResponse response,RedirectAttributes attributes) throws IOException {
        read.downloadAllFiles(name);
            attributes.addFlashAttribute("Downloadmessage", "Entity "+name+" successfully Downloaded!!");
            return "redirect:/api/main";
    }

    

    @PostMapping(value = "/main")
    public String save(@RequestParam Map<String, String> data , Model model,RedirectAttributes attributes)throws IOException {
        System.out.println(data.entrySet());
        File file = new File("../DMUtil/Lookup/GlobalLookup.csv");
        String eol = System.getProperty("line.separator");
        try (Writer writer = new FileWriter(file)) {
            writer.append(lookup)
            .append(eol);
            for (Map.Entry<String, String> entry : data.entrySet()) {
              writer.append(entry.getKey())
                    .append(',')
                    .append(entry.getValue())
                    .append(eol);
            }
        } catch (
        IOException e)
        {
            e.printStackTrace();
        }
        model.addAttribute("data", data);
        model.addAttribute("entities", read.entityList());
        attributes.addFlashAttribute("globalmessage", "Global Lookup values successfully updated !!");
        return "redirect:/api/main";
    }
    @PostMapping("/write/{name}")
    public String writeLookup(@RequestParam(required = false)Map<String, String> csvdata,@PathVariable("name") String name, Model model, RedirectAttributes attributes) throws IOException, Exception{
        System.out.println("write "+csvdata);
        
        read.saveLookup(csvdata, name);
        attributes.addFlashAttribute("Updatemessage", "Primary Lookup values successfully updated !!");

        return "redirect:/api/primary/"+name;
    }

    @PostMapping("/writeSec/{name}")
    public String writeLookupSec(@RequestParam(required = false)Map<String,String> c1data,@PathVariable("name") String name, Model model, RedirectAttributes attributes) throws IOException, Exception{
        System.out.println("write Secondary "+c1data);
        // for(Entry<String, String> bb :c1data.entrySet()){
            
        //     System.out.println(bb.getKey());
            
        //           System.out.println("hfg"+bb.getValue());

        //     }
        
        
       read.saveLookups(c1data, name);
        attributes.addFlashAttribute("Updatemessage", "Secondary Lookup values successfully updated !!");

        return "redirect:/api/secondary/"+name;
    }
}


   

   



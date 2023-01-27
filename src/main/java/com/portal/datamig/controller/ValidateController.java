package com.portal.datamig.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.portal.datamig.service.ReadService;
import com.portal.datamig.service.ValidateService;

@Controller
@RequestMapping("/api")
public class ValidateController {
  @Autowired
  ReadService read;
  @Autowired
  ValidateService validate;

  @GetMapping("/entityValidate/{name}")
  public String entityValidate(@PathVariable("name") String name, RedirectAttributes attributes,Model model) throws IOException {
    if (name != null) {
      attributes.addFlashAttribute("name", name);
      String entityColor = read.entityList().entrySet().stream().filter(x-> x.getKey().equals(name)).map(Map.Entry::getValue).collect(Collectors.joining(", "));
            
             model.addAttribute("col",entityColor);
      
    }
    return "entityValidate";
  }

  @GetMapping("/validate")
  public String validate(Model model) throws IOException {
    model.addAttribute("entities", read.entityList());
    model.addAttribute("recentList", read.recentlyUsed("Input"));
    return "validate";
  }

  // post upload for validate
  @PostMapping("/validate/upload")
  public String uploadFile(@RequestParam String selectedValueValidate, RedirectAttributes attributes, Model model) {
    System.out.println("sdfxgcvjhbx" + selectedValueValidate);
    try {
      validate.copyCSVFiles(selectedValueValidate);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      System.out.println(selectedValueValidate+" not Found");
      e.printStackTrace();
    }
    // return success response
    model.addAttribute(
        "messageP",
        "You successfully uploaded " + selectedValueValidate + '!');
    System.out.println("check " + selectedValueValidate);
    return "redirect:/api/entityValidate/" + selectedValueValidate;
  }

  @PostMapping(value = "/validate/primary")
  @ResponseBody
  public Map<String, List<String>> validateFiles(@RequestParam String entityValidate, RedirectAttributes attributes, Model model)
      throws IOException, InterruptedException {
    Map<String ,List<String>> map =validate.callValidationProgram(entityValidate);
    System.out.println(map);
    
    attributes.addFlashAttribute("message", map);
    return map;
  }
  @GetMapping("/validate/messageprimary")
  @ResponseBody
  public List<String> primaryMessage(@RequestParam String entityValidate) throws IOException, InterruptedException {
    List<String> output = validate.callEntityValidationProgram(entityValidate);
    return output;
  }

  @GetMapping("/validateSelect")
  @ResponseBody
  public List<String> populateSecondary(@RequestParam String entityValidate) throws IOException {
    List<String> listSecondary = new ArrayList<>();
    listSecondary.addAll(validate.entityListSecondary(entityValidate));
    return listSecondary;
  }

  @PostMapping("/validate/secondary")
  public String uploadSecondaryFiles(@RequestParam String entityValidate,String primaryEntityValidate, RedirectAttributes attributes, Model model)
      throws IOException, InterruptedException {
    System.out.print("Secondary Validate entity" + entityValidate + "" + "Primary Validate" + ""+primaryEntityValidate);
    validate.copyCSVFiles(primaryEntityValidate+"/"+entityValidate);
    return "redirect:/api/entityValidate/" + primaryEntityValidate;
  }

  @PostMapping("/validate/validateSecondary")
  public String validateSecondaryFiles(@RequestParam String entityValidate,String primaryEntityValidate, RedirectAttributes attributes, Model model)
      throws IOException, InterruptedException {
    System.out.print("Secondary Validate" + entityValidate + "" + "Primary Validate" + ""+primaryEntityValidate);
    validate.callSecondaryValidationProgram(primaryEntityValidate+"/"+entityValidate);
    return "redirect:/api/entityValidate/" + primaryEntityValidate;
  }
}

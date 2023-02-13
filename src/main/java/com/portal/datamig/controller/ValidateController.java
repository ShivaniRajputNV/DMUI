package com.portal.datamig.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import com.itextpdf.text.DocumentException;
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
  public String entityValidate(@PathVariable("name") String name, RedirectAttributes attributes, Model model)
      throws IOException {
    if (name != null) {
      attributes.addFlashAttribute("name", name);
      String entityColor = read.entityList().entrySet().stream().filter(x -> x.getKey().equals(name))
          .map(Map.Entry::getValue).collect(Collectors.joining(", "));

      model.addAttribute("col", entityColor);

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
      System.out.println(selectedValueValidate + " not Found");
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
  public Map<String, List<String>> validateFiles(@RequestParam String entityValidate, RedirectAttributes attributes,
      Model model)
      throws IOException, InterruptedException {
    Map<String, List<String>> map = validate.callValidationProgram(entityValidate);
    System.out.println(map);
    attributes.addFlashAttribute("message", map);
    return map;
  }

  @GetMapping("/validate/messageprimary")
  @ResponseBody
  public List<String> primaryMessage(@RequestParam String entityValidate) throws IOException, InterruptedException {
    List<String> output = validate.callEntityValidationProgram(entityValidate);
    if(output.isEmpty()){
      output.add("");
    }
    String exFolder = "../DMUtil/Reports/Validate/Entitywise_Val_Reports/Exception_Report/"+entityValidate;
    System.out.println(exFolder);
    String ll = validate.lastModifiled(exFolder);
    long count =0;
    try {

      // make a connection to the file
      Path file = Paths.get(ll);

      // read all lines of the file
       count = Files.lines(file).count();
      System.out.println("Total Lines: " + count);
      count--;

    } catch (Exception e) {
      e.getStackTrace();
    }
    System.out.println("COUNT"+count);
    output.add(String.valueOf(count));
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
  public String uploadSecondaryFiles(@RequestParam String entityValidate, String primaryEntityValidate,
      RedirectAttributes attributes, Model model)
      throws IOException, InterruptedException {
    System.out
        .print("Secondary Validate entity" + entityValidate + "" + "Primary Validate" + "" + primaryEntityValidate);
    validate.copyCSVFiles(primaryEntityValidate + "/" + entityValidate);
   
    return "redirect:/api/entityValidate/" + primaryEntityValidate;
  }

  @GetMapping("/validate/validateSecondary")
  @ResponseBody
  public Map<String, List<String>> validateSecondaryFiles(@RequestParam String entityValidate, String primaryEntityValidate,
      RedirectAttributes attributes, Model model)
      throws IOException, InterruptedException {
        Map<String, List<String>> output =  validate.callSecondaryValidationProgram(primaryEntityValidate + "/" + entityValidate);
      List<String> a = new ArrayList();

    if(output.isEmpty()){
      output.put("",null);
    }
    String exFolder = "../DMUtil/Reports/Validate/Exception_Report/"+entityValidate;
    System.out.println(exFolder);
    String ll = validate.lastModifiled(exFolder);
    long count =0;
    try {

      // make a connection to the file
      Path file = Paths.get(ll);

      // read all lines of the file
       count = Files.lines(file).count();
      System.out.println("Total Lines: " + count);
      count--;
      a.add(String.valueOf(count));

    } catch (Exception e) {
      e.getStackTrace();
    }
    System.out.println(count);
    output.put("Count",a);
    return output;
   
  }

  @GetMapping("/view-reports")
  @ResponseBody
  public List<List<String>> viewReport(@RequestParam String name) throws IOException, DocumentException {
    String summaryFolder = "../DMUtil/Reports/Validate/Entitywise_Val_Reports/Summary_Report/"+name;
    String exceptionFolder = "../DMUtil/Reports/Validate/Entitywise_Val_Reports/Exception_Report/"+name;
    if(name.contains("/")){
       summaryFolder = "../DMUtil/Reports/Validate/Common_Val_Reports/Summary_Report/"+name;
      exceptionFolder = "../DMUtil/Reports/Validate/Common_Val_Reports/Exception_Report/"+name;
    }
    
    String lastSummary = validate.lastModifiled(summaryFolder);
    String lastException = validate.lastModifiled(exceptionFolder);
    List<List<String>> data = new ArrayList<>();
    try{
    data.add(validate.reports(lastSummary));
    data.add(validate.reports(lastException));
    }catch(Exception e){
    }finally{
      List<String> nosumm = new ArrayList<>();
      List<String> noex = new ArrayList<>();
      nosumm.add("No data Found");
      noex.add("No data found");
      data.add(nosumm);
      data.add(noex);
    }
    
    System.out.println(data.get(0));
    return data;
  }

 
 
  @GetMapping("/validate/download")
  @ResponseBody
  public String downloadValidateReport(@RequestParam String validateEntity, RedirectAttributes attributes,
  Model model)throws IOException{
    String summaryFolder = "../DMUtil/Reports/Validate/Entitywise_Val_Reports/Summary_Report/"+validateEntity;
    String exceptionFolder = "../DMUtil/Reports/Validate/Entitywise_Val_Reports/Exception_Report/"+validateEntity;
    if(validateEntity.contains("/")){
      summaryFolder = "../DMUtil/Reports/Validate/Common_Val_Reports/Summary_Report/"+validateEntity;
     exceptionFolder = "../DMUtil/Reports/Validate/Common_Val_Reports/Exception_Report/"+validateEntity;
   }
   try{
    List<String> data = new ArrayList<>();
    String lastSummary = validate.lastModifiled(summaryFolder);
    String lastException = validate.lastModifiled(exceptionFolder);
   data.add(lastException);
   data.add(lastSummary);
   validate.downloadValidate(data,validateEntity);
   }catch(Exception e){

   }finally{
    System.out.println("No Record to Download");
   }

      return "Success"+validateEntity;
  }
      

}


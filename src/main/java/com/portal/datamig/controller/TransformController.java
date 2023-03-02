package com.portal.datamig.controller;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;

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

//import com.itextpdf.text.List;
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

    model.addAttribute("recentList", read.recentlyUsed("Transform"));
        return "transform";
    }
    @GetMapping("/transforming/{name}")
    public String transforming(@PathVariable("name")String name,RedirectAttributes redirectAttributes ,Model model) throws IOException{
        if(name!=null){
            redirectAttributes.addFlashAttribute("name",name);
            String entityColor = read.entityList().entrySet().stream().filter(x-> x.getKey().equals(name)).map(Map.Entry::getValue).collect(Collectors.joining(", "));
            
             model.addAttribute("col",entityColor);
        }
        return "transforming";
    }
    @PostMapping("/transforming")
    public String transformingProgress(@RequestParam String entityTransform){
        System.out.println(entityTransform);
        transform.archieveTransformFiles("Transform"+File.separator+"Summary_Reports"+File.separator+entityTransform,
      "Reports"+File.separator+"Transform"+File.separator+"Summary_Reports"+File.separator+entityTransform);
      transform.archieveTransformFiles("Sql_Output"+File.separator+entityTransform,
      "Sql_Output"+File.separator+entityTransform);
        transform.transformList(entityTransform);
        return "redirect:"+File.separator+"api"+File.separator+"transforming"+File.separator+entityTransform;
    }

    @GetMapping("/transforming/download-reports")
    @ResponseBody
    public String transformDownload(@RequestParam String entityTransform){
        // String summaryFolder = home+"/DMUtil/Report/Validate/Entitywise_Val_Reports/Summary_Report/"+entityTransform;
    // String exceptionFolder = home+"/DMUtil/Reports/Validate/Entitywise_Val_Reports/Exception_Report/"+validateEntity;
   try{
    // List<String> data = new ArrayList<>();
    // String lastSummary = transform.allModifiedFiles(entityTransform);
    // String lastException = validate.lastModifiled(exceptionFolder);
//    data.add(lastException);
//    data.add(lastSummary);
   transform.downloadValidate(transform.allModifiedFiles(entityTransform),entityTransform);
   }catch(Exception e){

   }finally{
    System.out.println("No Record to Download");
   }

        return "SUCCESS";
    }


    // view transform report

    @GetMapping("/transforming/view-reports")
    @ResponseBody    public List<List<String>> transformViewReport(@RequestParam String entityTransform){
     List<List<String>> data = new ArrayList<>();
   try{
   List<String> reports = transform.allModifiedFiles(entityTransform);
   System.out.println(reports+"REPORt");
   for(int i =0; i< reports.size();i++){
    String[] splitName=reports.get(i).split(File.separator);
    List<String> reportName= new ArrayList();
    reportName.add(splitName[splitName.length-2]);
    System.out.println(reportName);
    if(reports.get(i)!= null){
    data.add(reportName);
    data.add(transform.transformReports(reports.get(i)));
    }else{
        System.out.println("File not found");
    }
   }
   }catch(Exception e){
   }finally{
    System.out.println("No Record to show");
   }
    return data;
    }
}

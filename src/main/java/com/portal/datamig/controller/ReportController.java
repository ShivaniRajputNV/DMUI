package com.portal.datamig.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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

import com.portal.datamig.service.ReadService;
import com.portal.datamig.service.ReportService;
import com.portal.datamig.service.ValidateService;

@Controller
@RequestMapping("/api")
public class ReportController {
    @Autowired(required = true)
    ReadService read;
    @Autowired(required = true)
    ValidateService validate;
    @Autowired(required = true)
    ReportService report;

    String home = System.getProperty("user.home");

    @GetMapping("/downloadreports/{name}")
    public String downloadreports(@PathVariable("name") String name, RedirectAttributes attributes, Model model)
            throws IOException {
        if (name != null) {
            attributes.addFlashAttribute("name", name);
            String entityColor = read.entityList().entrySet().stream().filter(x -> x.getKey().equals(name))
                    .map(Map.Entry::getValue).collect(Collectors.joining(", "));
            model.addAttribute("col", entityColor);
        }
        return "downloadreports";
    }

    @GetMapping("/report/{name}")
    public String report(@PathVariable("name") String name, RedirectAttributes attributes, Model model)
            throws IOException {
        if (name != null) {
            attributes.addFlashAttribute("name", name);
            String entityColor = read.entityList().entrySet().stream().filter(x -> x.getKey().equals(name))
                    .map(Map.Entry::getValue).collect(Collectors.joining(", "));
            model.addAttribute("col", entityColor);
        }
        return "report";
    }

    @GetMapping("/reports/transform")
    @ResponseBody
    public List<List<String>> reportTransformTab(@RequestParam String entityToView) throws FileNotFoundException {
        List<List<String>> reportList = new ArrayList<>();
        String path = "Transform"+File.separator+"Summary_Reports"+File.separator + entityToView;
        reportList = report.reportTransformTab(path);
        return reportList;
    }

    @GetMapping("/reports/validate")
    @ResponseBody
    public List<List<String>> reportValidateTab(@RequestParam String entityToView) throws FileNotFoundException {
        List<List<String>> reportList = new ArrayList<>();
        String path = File.separator+"Validate"+File.separator+"Entitywise_Val_Reports"+File.separator+"Summary_Report"+File.separator + entityToView;
        reportList = report.reportTransformTab(path);
        return reportList;
    }

    @GetMapping("/reports-download/transform")
    @ResponseBody
    public String reportDownloadTransformTab(@RequestParam String entityToView) throws IOException {
        String path = "Transform"+File.separator+"Summary_Reports"+File.separator + entityToView;
        report.downloadTransformRep(path);
        return "Download report tranfrom from tab";
    }

    @GetMapping("/reports-download/validate")
    @ResponseBody
    public String reportDownloadValidateTab(@RequestParam String entityToView) throws IOException {
        List<File> filesname = new ArrayList<>();
        File path1 = new File(
                home + File.separator+"DMUtil"+File.separator+"Reports"+File.separator+"Validate"+File.separator+"Entitywise_Val_Reports"+File.separator+"Summary_Report"+File.separator + entityToView + File.separator);
        File path2 = new File(
                home + File.separator+"DMUtil"+File.separator+"Reports"+File.separator+"Validate"+File.separator+"Entitywise_Val_Reports"+File.separator+"Exception_Report"+File.separator + entityToView + File.separator);
        File summaryFolderCommon = new File(
                home+File.separator+"DMUtil"+File.separator+"Reports"+File.separator+"Validate"+File.separator+"Common_Val_Reports"+File.separator+"Summary_Report"+File.separator+  entityToView + File.separator);
        File exceptionFolderCommon = new File(
                home+File.separator+"DMUtil"+File.separator+"Reports"+File.separator+"Validate"+File.separator+"Common_Val_Reports"+File.separator+"Exception_Report"+File.separator + entityToView + File.separator);
        File[] files = null;
        filesname.add(path1);
        filesname.add(path2);
        filesname.add(summaryFolderCommon);
        filesname.add(exceptionFolderCommon);
        List<String> reportnames = new ArrayList<>();
        for (File path : filesname) {
            System.out.println(path.isDirectory());
            files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    reportnames.add(files[i].getPath());
                } else {
                    File[] subFiles = files[i].listFiles();
                    for (int j = 0; j < subFiles.length; j++) {
                        reportnames.add(subFiles[j].getPath());
                    }
                }
            }
        }
        report.downloadValidateTab(reportnames, entityToView);
        return "Download report validate from tab";
    }

    @GetMapping("/reports/load")
    @ResponseBody
    public List<List<String>> reportLoadTab(@RequestParam String entityToView) throws FileNotFoundException {
        List<List<String>> reportList = new ArrayList<>();
        String path = "Load"+File.separator+"Summary_Reports"+File.separator + entityToView;
        reportList = report.reportTransformTab(path);
        return reportList;
    }

    @GetMapping("/reports-download/load")
    @ResponseBody
    public String reportDownloadLoadTab(@RequestParam String entityToView) throws IOException {
        String path = "Load"+File.separator+"Summary_Reports"+File.separator + entityToView;
        report.downloadTransformRep(path);
        return "Download report load from tab";
    }
}
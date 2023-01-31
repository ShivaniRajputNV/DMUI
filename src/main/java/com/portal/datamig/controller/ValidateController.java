package com.portal.datamig.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPage;
import com.itextpdf.text.pdf.PdfRectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import com.portal.datamig.service.ReadService;
import com.portal.datamig.service.ValidateService;

import com.univocity.parsers.common.processor.ColumnProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.datatable.DataTable;
import jakarta.servlet.http.HttpServletResponse;

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

  @PostMapping("/validate/validateSecondary")
  public String validateSecondaryFiles(@RequestParam String entityValidate, String primaryEntityValidate,
      RedirectAttributes attributes, Model model)
      throws IOException, InterruptedException {
    System.out.print("Secondary Validate" + entityValidate + "" + "Primary Validate" + "" + primaryEntityValidate);
    validate.callSecondaryValidationProgram(primaryEntityValidate + "/" + entityValidate);
    return "redirect:/api/entityValidate/" + primaryEntityValidate;
  }

  @GetMapping("/view-reports")
  @ResponseBody
  public List<String> viewReport(@RequestParam String name) throws IOException, DocumentException {
    String reportFolder = "Premise";
    File path = new File("../DMUtil/Reports/Validate/Entitywise_Val_Reports/Summary_Report/" + name + "/");
    long time = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(2);
    File[] files = path.listFiles(pathname -> pathname.lastModified() >= time);
    System.out.println(files);
    if (files == null || files.length == 0) {
      return null;
    }

    File lastModifiedFile = files[0];
    for (int i = 1; i < files.length; i++) {
      if (lastModifiedFile.lastModified() < files[i].lastModified()) {
        lastModifiedFile = files[i];
      }
    }
    System.out.println(lastModifiedFile);
    String lastmodifiedFile = lastModifiedFile.toString();
    File file = new File(lastmodifiedFile);
    FileReader filereader = new FileReader(file);
    BufferedReader br = new BufferedReader(filereader);
    List<String> data = new ArrayList();
    // CsvParserSettings parserSettings = new CsvParserSettings(); //many options
    // here, check the documentation.
    // parserSettings.setHeaderExtractionEnabled(true); // gets the headers from the
    // first row// To get the values of all columns, use a column processor
    // ColumnProcessor rowProcessor = new ColumnProcessor();
    // parserSettings.setRowProcessor(rowProcessor);
    // CsvParser parser = new CsvParser(parserSettings);
    // //This will parse all rows and submit them to the column
    // parser.parse(new FileReader(lastmodifiedFile));
    // //Finally, we can get the column values:
    // Map<String, List<String>> columnValues =
    // rowProcessor.getColumnValuesAsMapOfNames();

    Scanner sc = new Scanner(new File(lastmodifiedFile));
    sc.useDelimiter("\r\n"); // sets the delimiter pattern
    while (sc.hasNext()) // returns a boolean value
    {
      // List<String> dataline = new ArrayList();
      // String[] st = sc.next().split(",");
      // for (String n : st) {
      //   dataline.add(n);
      data.add(sc.next());
      // }

      // data.addAll(dataline);
      // find and returns the next complete token from this scanner
    }
    System.out.println("vajgx" + data + "csvDATA");
    sc.close(); // closes the scanner

    // response.setContentType("text/plain; charset=utf-8");
    // response.getWriter().print(data);
    
    return data;
  }

  // return lastModifiedFile;
  @GetMapping("/view")
  public String view(){
    return "view-report";

  }
 

      

}


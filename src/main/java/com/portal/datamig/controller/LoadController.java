package com.portal.datamig.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;


import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
    String homeDir = System.getProperty("user.home");

    @GetMapping("/load")
    public String load(Model model) throws IOException {
        model.addAttribute("entities", read.entityList());
        model.addAttribute("recentList", read.recentlyUsed("Sql_Output"));
        return "load";
    }

    @GetMapping("/loadprocess/{name}")
    public String loadprocess(@PathVariable("name") String name, Model model, RedirectAttributes attributes)
            throws IOException {
        if (name != null) {
            attributes.addFlashAttribute("name", name);
            String entityColor = read.entityList().entrySet().stream().filter(x -> x.getKey().equals(name))
                    .map(Map.Entry::getValue).collect(Collectors.joining(", "));

            model.addAttribute("col", entityColor);
            model.addAttribute("dbdetails", load.dbDetails());
            System.out.println("bhksa" + load.dbDetails());
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

    @GetMapping("/load/load-download")
    @ResponseBody
    public String downloadPdf(@RequestParam String loadEntity) throws IOException {
        List<String> dirName = new ArrayList<>();
        dirName.add(validate.lastModifiled("../DMUtil/Reports/Load/Summary_Reports"));
        String zipFile = ("../Downloads/" + loadEntity+"_LoadReports.zip");
        FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);
            
            for (int i=0; i < dirName.size(); i++) {
                 
                File srcFile = new File(dirName.get(i));
                FileInputStream fis = new FileInputStream(srcFile);

                // Start writing a new file entry 
                zos.putNextEntry(new ZipEntry(srcFile.getName())); 

                int length;
                // create byte buffer
                byte[] buffer = new byte[1024];

                // read and write the content of the file
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                // current file entry is written and current zip entry is closed
                zos.closeEntry();
 
                // close the InputStream of the file 
                fis.close();
                 
            }

            // close the ZipOutputStream
            zos.close();



        return "Success";
    }

}
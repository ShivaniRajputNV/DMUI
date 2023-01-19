package com.portal.datamig.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;



@Service
public class ValidateService {
      // upload/copy for validate page
      public void copyCSVFiles(String loc) throws IOException {
      

        Path src = Paths.get("../Client/" + loc + "/");
        Path dest = Paths.get("../DMUtil/Input/" + loc+ "/");
        System.out.println(src.toString());
        File f = src.toFile();
        
        Arrays.stream(f.listFiles()).filter(p -> !p.isDirectory()).forEach(p -> {
            try {
                FileUtils.copyFileToDirectory(p, dest.toFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void callValidationProgram(String selectedFolder) throws IOException, InterruptedException{
        String loc = "../DMUtil/Validate/CmCommonValidation.java";
        String loc1 = "java -cp ../DMUtil/Validate/CmCommonValidation"+" "+"../DMUtil/Input/"+selectedFolder+" "+"../DMUtil/Validate/Mapping_Sheet/"+selectedFolder+".csv";
        String command[] = {"javac", loc};
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();
        process.waitFor();
            if (process.getErrorStream().read() != -1) {
                print("Compilation Errors",process.getErrorStream());
            }
            System.out.println(process.exitValue());
            if (process.exitValue() == 0) {
                process = new ProcessBuilder(new String[]{"java", "-cp", "../DMUtil/Validate/", "CmCommonValidation","../DMUtil/Input/"+selectedFolder,"../DMUtil/Validate/Mapping_Sheet/"+selectedFolder+".csv"}).start();
                if (process.getErrorStream().read() != -1) {
                    print("Errors ", process.getErrorStream());
                } else {
                    print("Output ", process.getInputStream());
                }
            }
            // process.waitFor();
            // process.exitValue();
    }
    private static void print(String status,InputStream input) throws IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        System.out.println("************* "+status+"***********************");
        String line = null;
        while((line = in.readLine()) != null ){
            System.out.println(line);
        }
        in.close();
    }

    public void callSecondaryValidationProgram(String selectedFolder) throws IOException, InterruptedException{
        String loc = "../DMUtil/Validate/CmCommonValidation.java";
        String loc1 = "java -cp /home/anshika/DMUtil/Validate/CmCommonValidation"+" "+"/home/anshika/DMUtil/Input/"+selectedFolder+" "+"/home/anshika/DMUtil/Validate/Mapping_Sheet/"+selectedFolder+".csv";
        String[] nn = selectedFolder.split("/");
        String command[] = {"javac", loc};
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();
        process.waitFor();
            if (process.getErrorStream().read() != -1) {
                print("Compilation Errors",process.getErrorStream());
            }
            System.out.println(process.exitValue());
            if (process.exitValue() == 0) {
                process = new ProcessBuilder(new String[]{"java", "-cp", "../DMUtil/Validate/", "CmCommonValidation","../DMUtil/Input/"+selectedFolder,"../DMUtil/Validate/Mapping_Sheet/"+nn[1].replace("_", "")+".csv"}).start();
                if (process.getErrorStream().read() != -1) {
                    print("Errors ", process.getErrorStream());
                } else {
                    print("Output ", process.getInputStream());
                }
            }
            // process.waitFor();
            // process.exitValue();
    }
    
    public List<String> entityListSecondary(String n) throws IOException {
        Path src = Paths.get("../DMUtil/Input/"+n+ "/");
        List<String> sList = new ArrayList<>();
            File f = src.toFile();
            Arrays.stream(f.listFiles()).filter(p -> p.isDirectory()).forEach(p -> {
                System.out.println(p);
                sList.add(p.getName());
                
            });
            // System.out.print(sList);
            return sList;
    }



}

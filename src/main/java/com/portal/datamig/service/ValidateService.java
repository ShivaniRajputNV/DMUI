package com.portal.datamig.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties.Retry;
import org.springframework.stereotype.Service;



@Service
public class ValidateService {
      private static List<String> outputEV = null;
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

    public Map<String, List<String>> callValidationProgram(String selectedFolder) throws IOException, InterruptedException{
        String loc = "../DMUtil/Validate/CmCommonValidation.java";
        String loc1 = "java -cp ../DMUtil/Validate/CmCommonValidation"+" "+"../DMUtil/Input/"+selectedFolder+" "+"../DMUtil/Validate/Mapping_Sheet/"+selectedFolder+".csv";
        String command[] = {"javac", loc};
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();
        Map<String,List<String>> result = new HashMap();
        List<String> output = new ArrayList<>();
        List<String> error = new ArrayList<>();
        process.waitFor();
            if (process.getErrorStream().read() != -1) {
                print("Compilation Errors",process.getErrorStream());
            }
            System.out.println(process.exitValue());
            if (process.exitValue() == 0) {
                process = new ProcessBuilder(new String[]{"java", "-cp", "../DMUtil/Validate/", "CmCommonValidation","../DMUtil/Input/"+selectedFolder,"../DMUtil/Validate/Mapping_Sheet/"+selectedFolder+".csv"}).start();
                if (process.getErrorStream().read() != -1) {
                    error = print("Errors ", process.getErrorStream());
                    result.put("Error", error);
                    

                } else {
                    output =print("Output ", process.getInputStream());
                   result.put("Output", output);
                }
            }
            // process.waitFor();
            // process.exitValue();
            return result;
    }
    private static List<String> print(String status,InputStream input) throws IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        System.out.println("************* "+status+"***********************");
        List<String> lines= new ArrayList<>();
        String line;
        while((line = in.readLine()) != null ){
            System.out.println(line);
            lines.add(line);
        }
        //in.close();
        return lines;
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
    public List<String> callEntityValidationProgram(String selectedFolder) throws IOException, InterruptedException{
        Path src = Paths.get("../DMUtil/Validate/"+selectedFolder+ "/");
        File f = src.toFile();
        
        Arrays.stream(f.listFiles()).filter(p -> p.getName().endsWith(".java")).forEach(p -> {
            try {
             System.out.println(p.getName()+" File "+f.getName());
              outputEV=callProgram(p.getName(),f.getName());
         } catch (IOException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
         } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        });
        //System.out.println(outputEV+"dvghkj");
        
       
        return outputEV;
    }
    public List<String> callProgram(String javaFile,String fileName) throws IOException, InterruptedException{
        String loc = "../DMUtil/Validate/"+fileName+"/"+javaFile;
        System.out.println(loc);
        List<String> output = new ArrayList<>();
        // String loc1 = "java -cp /home/anshika/DMUtil/Validate/CmCommonValidation"+" "+"/home/anshika/DMUtil/Input/"+nn+" "+"/home/anshika/DMUtil/Validate/Mapping_Sheet/"+nn+".csv";
        String command[] = {"javac", loc};
        String javaFileName= javaFile.replace(".java","");
        System.out.println(javaFileName);
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();
        process.waitFor();
            if (process.getErrorStream().read() != -1) {
                print("Compilation Errors",process.getErrorStream());
            }
            System.out.println(process.exitValue());
            System.out.println(" Secondary Validate Inside file ");
            if (process.exitValue() == 0) {
                process = new ProcessBuilder(new String[]{"java", "-cp", "../DMUtil/Validate/"+fileName+"/", javaFileName}).start();
                System.out.println("Secondary Processs");
                if (process.getErrorStream().read() != -1) {
                    print("Errors ", process.getErrorStream());
                } else {
                    output= print("Output ", process.getInputStream());
                }
            }
            // process.waitFor();
            // process.exitValue()
            
            return output;
    }
  
    



}

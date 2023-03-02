package com.portal.datamig.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

@Service
public class LoadService {
    private static String home = System.getProperty("user.home");
    public Map<String, String> dbDetails() throws IOException{
        File file = new File(home+File.separator+"DMUtil"+File.separator+"db"+File.separator+"db_details.csv");
        // FileReader filereader = new FileReader(file);
            BufferedReader br = new BufferedReader(new FileReader(file));
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
        return map;
    }
 
    public Map<String, List<String>> callLoadProgram(String name) throws IOException, InterruptedException{
        String loc = home+File.separator+"DMUtil" + File.separator + "Load" + File.separator + "JDBC_T1.java";
        String loc1 = home+File.separator+"DMUtil" + File.separator + "Load" + File.separator + "RnEx.java";
        String loc2 = home+File.separator+"DMUtil" + File.separator + "Load" + File.separator + "Sr.java";
        String command1[] = { "javac", loc1 };
        ProcessBuilder processBuilder1= new ProcessBuilder(command1);
        Process process1 = processBuilder1.start();
        Map<String, List<String>> result1 = new HashMap();
        process1.waitFor();
        if (process1.getErrorStream().read() != -1) {
            print("Compilation Errors", process1.getErrorStream());
        }
        System.out.println(process1.exitValue());
        String command2[] = { "javac", loc2 };
        ProcessBuilder processBuilder2 = new ProcessBuilder(command2);
        Process process2 = processBuilder2.start();
        Map<String, List<String>> result2 = new HashMap();
        process2.waitFor();
        if (process2.getErrorStream().read() != -1) {
            print("Compilation Errors", process2.getErrorStream());
        }
        System.out.println(process2.exitValue());

        String command[] = { "javac", loc };
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();
        Map<String, List<String>> result = new HashMap();
        List<String> output = new ArrayList<>();
        List<String> error = new ArrayList<>();
        process.waitFor();
        if (process.getErrorStream().read() != -1) {
            print("Compilation Errors", process.getErrorStream());
        }
        System.out.println(process.exitValue());
        if (process.exitValue() == 0) {
            process = new ProcessBuilder(new String[] { "java", "-cp",
                    home+File.separator+"DMUtil" + File.separator + "Load" + File.separator , "JDBC_T1",
                    home+File.separator+"DMUtil" + File.separator + "Sql_Output" + File.separator + name })
                    .start();
            if (process.getErrorStream().read() != -1 || process.getInputStream().read()!= -1) {
                error = print("Errors ", process.getErrorStream());
                
                output = print("Output ", process.getInputStream());
                System.out.println("OUTPUT"+output);
                result.put("Output", output);
                result.put("Error", error);
            } 
        }
        // process.waitFor();
        // process.exitValue();
    

        return result;
    }
    private static List<String> print(String status, InputStream input) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        System.out.println("************* " + status + "***********************");
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = in.readLine()) != null) {
            System.out.println(line);
            lines.add(line);
        }
        // in.close();
        return lines;
    }

     // lastModifies load

     public String lastModifiled(String filepath) {

        File path = new File(filepath + File.separator);
        System.out.println(path);
        long time = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(2);
        File[] files = path.listFiles(pathname -> pathname.lastModified() >= time);
       // System.out.println(files);
        if (files == null || files.length == 0) {
            System.out.println("no files in folder");
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

        return lastmodifiedFile;
    }
}
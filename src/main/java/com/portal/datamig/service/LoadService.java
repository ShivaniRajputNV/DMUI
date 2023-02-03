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

import org.springframework.stereotype.Service;

@Service
public class LoadService {
    public Map<String, String> dbDetails() throws IOException{
        File file = new File("../DMUtil/db/db_details.csv");
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
        String loc = "../DMUtil" + File.separator + "Load" + File.separator + "JDBC_T1.java";
        String loc1 = "../DMUtil" + File.separator + "Load" + File.separator + "RnEx.java";
        String loc2 = "../DMUtil" + File.separator + "Load" + File.separator + "Sr.java";
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
                    "../DMUtil" + File.separator + "Load" + File.separator , "JDBC_T1",
                    "../DMUtil" + File.separator + "Sql_Output" + File.separator + name })
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
}

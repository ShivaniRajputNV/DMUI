package com.portal.datamig.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import lombok.Synchronized;

@Service
public class LoadService {

    private static String home = System.getProperty("user.home");

    public Map<String, String> dbDetails() throws IOException {
        File file = new File(
                home + File.separator + "DMUtil" + File.separator + "db" + File.separator + "db_details.csv");
        // FileReader filereader = new FileReader(file);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        List<String[]> allLines = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            String[] splited = line.split("\\s*,\\s*");
            allLines.add(splited);
        }
        Map<String, String> map = new LinkedHashMap<>();
        for (String[] row : allLines) {
            String key = row[0];
            String value = row[1];
            map.put(key, value);
        }
        return map;
    }

    public Map<String, List<String>> callLoadProgram(String name) throws IOException, InterruptedException {
        String com = home + File.separator + "DMUtil" + File.separator + "Load" + File.separator;
        System.setProperty("CLASSPATH", com);
        System.out.println(">>>>" + System.getProperty("CLASSPATH"));
        String loc = home + File.separator + "DMUtil" + File.separator + "Load" + File.separator + "JDBC_T1.java";
        String command[] = { "javac", loc };
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.environment().put("CLASSPATH", com);
        Process process = processBuilder.start();
        Map<String, List<String>> result = new HashMap();
        List<String> output = new ArrayList<>();
        List<String> error = new ArrayList<>();
        process.waitFor();
        if (process.getErrorStream().read() != -1) {
            print("Compilation Errors", process.getErrorStream());
        }
        try {
            if (process.exitValue() == 0) {
                process = new ProcessBuilder(new String[] { "java", "-cp",
                        home + File.separator + "DMUtil" + File.separator + "Load" + File.separator, "JDBC_T1",
                        home + File.separator + "DMUtil" + File.separator + "Sql_Output" + File.separator + name })
                        .start();

                output = print("Output ", process.getInputStream());
                error = print("Errors ", process.getErrorStream());

                System.out.println(print("Errors ", process.getErrorStream()));

                process.waitFor();
                if (process.getErrorStream().read() != -1 || process.getInputStream().read() != -1) {
                    System.out.println(process.isAlive());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        result.put("Output", output);
        result.put("Error", error);

        return result;
    }

    private static List<String> print(String status, InputStream input) throws IOException {
        List<String> lines = new ArrayList<>();
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        new Thread(new Runnable() {
            public void run() {
                System.out.println("************* " + status + "***********************");

                String line;
                try {

                    while ((line = in.readLine()) != null) {
                        lines.clear();
                        System.out.println(line);
                        lines.add(line);

                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }).start();

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
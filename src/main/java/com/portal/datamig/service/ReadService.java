package com.portal.datamig.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import ch.qos.logback.core.net.SyslogOutputStream;

@Service
public class ReadService {

    private static String lookup = "Field_Name,Field_Value";

    public Map<String, String> entityList() throws IOException {
        // Resource resource = new ClassPathResource("/csvs/entities/EntityList.csv");
        // File file = resource.getFile();
        File file = new File("../DMUtil/EntityList.csv");
        FileReader filereader = new FileReader(file);
        BufferedReader br = new BufferedReader(filereader);
        Map<String, String> map = new LinkedHashMap<>();
        String[] groupArray = new String[1000];
        String line;
        int i = 0, k = 0;
        while ((line = br.readLine()) != null) {
            String[] split = line.split(",");
            if (k == 0) {
                k++;
                continue;
            }
            groupArray[i] = split[0].trim();
            groupArray[i + 1] = (split.length > 1) ? split[1].trim() : "";
            i += 2;
        }
        String keyname, keyvalue;
        for (int j = 0; j < i; j += 2) {
            keyname = groupArray[j];
            keyvalue = groupArray[j + 1];
            map.put(keyname, keyvalue);
        }
        return map;
    }

    // download sample fiels
    public void downloadAllFiles(String name) throws IOException {
        // HttpServletResponse response = null;
        // String dirName = name.toLowerCase();
        String dirName = name;
        try {
            File f = new File("../DMUtil/SampleFiles/" + dirName + "/");
            File fileList[] = f.listFiles();
            Arrays.stream(fileList).iterator().forEachRemaining(System.out::println);
            for (File r : fileList) {
                if (r.isDirectory()) {
                    Arrays.stream(r.listFiles()).iterator().forEachRemaining(System.out::println);
                }
            }
        } catch (Exception e) {
            System.out.println("Directory is empty");
        }
        // test download
        Path zipFile = Path.of("../Client/" + dirName + ".zip");
        File f = new File("../DMUtil/SampleFiles/" + dirName + "/");
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFile))) {
            if (Files.isDirectory(f.toPath())) {
                Files.walk(f.toPath()).filter(path -> !Files.isDirectory(path)).forEach(path -> {
                    ZipEntry zipEntry = new ZipEntry(f.toPath().relativize(path).toString());
                    System.out.println(zipOutputStream + " " + f.toPath());
                    try {
                        zipOutputStream.putNextEntry(zipEntry);
                        if (Files.isRegularFile(path)) {
                            Files.copy(path, zipOutputStream);
                        }
                        zipOutputStream.closeEntry();
                    } catch (IOException e) {
                        System.err.println(e);
                    }
                });
            }
        }
    }

    public Map<String, String> readCSVFile(String name) throws IOException, Exception {
        String dirName = name;
        name = name.replaceAll("_", "");
      //  Resource resource = new ClassPathResource("../DMUtil/Lookup/"+ dirName + "/" + name + "Lookup" + ".csv");
        File file = new File("../DMUtil/Lookup/"+ dirName + "/" + name + "Lookup" + ".csv");
        FileReader filereader = new FileReader(file);
        BufferedReader br = new BufferedReader(filereader);
        Map<String, String> map = new LinkedHashMap<>();
        String[] groupArray = new String[1000];
        String line;
        int i = 0, k = 0;
        while ((line = br.readLine()) != null) {
            String[] split = line.split(",");
            if (k == 0) {
                k++;
                continue;
            }
            groupArray[i] = split[0].trim();
            groupArray[i + 1] = (split.length > 1) ? split[1].trim() : "";
            i += 2;
        }
        String keyname, keyvalue;
        for (int j = 0; j < i; j += 2) {
            keyname = groupArray[j];
            keyvalue = groupArray[j + 1];
            map.put(keyname, keyvalue);
        }
        System.out.println(map.entrySet());
        // for (String key : map.keySet()) {
        // System.out.println(key + " " + map.get(key));
        // }
        // model.addAttribute("csvdata",map);

        return map;
    }
    //primary lookup update
    public Map<String, String> saveLookup(Map<String, String> data, String fname) throws IOException {
        System.out.println(data.entrySet());
        String dirName = fname;
        fname = fname.replaceAll("_", "");
        // Resource resource = new ClassPathResource("/csvs/csvs/" + dirName + "/" +
        // fname + "Lookup" + ".csv");
        File file = new File("../DMUtil/Lookup/"+ dirName + "/" + fname + "Lookup" + ".csv");
        String eol = System.getProperty("line.separator");

        try (Writer writer = new FileWriter(file)) {
            writer.append(lookup)
                    .append(eol);
            for (Map.Entry<String, String> entry : data.entrySet()) {
                writer.append(entry.getKey())
                        .append(',')
                        .append(entry.getValue())
                        .append(eol);
            }
        } catch (IOException e) {

            e.printStackTrace();
        }
        return data;
    }
    public Map<String, Map<String, String>> readCSVFiles(String fname) {
        Map<String, Map<String, String>> listMap = new HashMap();
        String dirName = fname;
        String filename = fname.replaceAll("_", "");
        try {
           // Resource resource = new ClassPathResource("/csvs/csvs/" + dirName + "/");
            File f = new File("../DMUtil/Lookup/"+ dirName + "/");
            System.out.println("file name" + f);
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File f, String name) {
                    int flag = 0;
                    // We want to find only .csv files
                    return name.endsWith(".csv") && (!name.equals(filename + "Lookup" + ".csv"));
                }
            };
            // Note that this time we are using a File class as an array,
            // instead of String
            File[] files = f.listFiles(filter);
            // Get the names of the files by using the .getName() method
            for (int i = 0; i < files.length; i++) {
                System.out.println("file name" + files[i].getName());
                listMap.put(files[i].getName(), printCSVFile(files[i].getName(), dirName));
            }
            // for (Map<String, String> map : listMap) {
            // for (Map.Entry<String, String> entry : map.entrySet()) {
            // String key = entry.getKey();
            // String value = entry.getValue();
            // System.out.println(key + " " + value);
            // }
            // }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return listMap;
    }
    public Map<String, String> printCSVFile(String name, String dirName) throws IOException {
       // Resource resource = new ClassPathResource("/csvs/csvs/" + dirName + "/" + name);
        File file = new File("../DMUtil/Lookup/"+ dirName + "/"+ name);
        FileReader filereader = new FileReader(file);
        BufferedReader br = new BufferedReader(filereader);
        Map<String, String> map = new LinkedHashMap<String, String>();
        String[] groupArray = new String[1000];
        String line;
        int i = 0, k = 0;
        while ((line = br.readLine()) != null) {
            String[] split = line.split(",");
            if (k == 0) {
                k++;
                continue;
            }
            groupArray[i] = split[0].trim();
            groupArray[i + 1] = (split.length > 1) ? split[1].trim() : "";
            i += 2;
        }
        String keyname, keyvalue;
        for (int j = 0; j < i; j += 2) {
            keyname = groupArray[j];
            keyvalue = groupArray[j + 1];
            map.put(keyname, keyvalue);
        }
        for (String key : map.keySet()) {
            System.out.println(key + " " + map.get(key));
        }
        return map;
    }

}

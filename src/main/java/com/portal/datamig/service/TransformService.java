package com.portal.datamig.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransformService {
    @Autowired
    ValidateService validate;
    private static String homeDir = System.getProperty("user.home");


    public void transformList(String name) {
        Path src = Paths.get("../DMUtil/Transform/" + name + "/");
        File f = src.toFile();
        Arrays.stream(f.listFiles()).filter(p -> p.getName().endsWith(".java")).forEach(p -> {
            try {
                System.out.println(p.getName() + " File " + f.getName());
                callProgram(p.getName(), f.getName());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
        Arrays.stream(f.listFiles()).filter(p -> p.isDirectory()).forEach(p -> {
            Arrays.stream(p.listFiles()).filter(n -> n.getName().endsWith(".java")).forEach(n -> {
                System.out.println(p.getName() + " : JAVA File " + n.getName());
                try {
                    callProgram(n.getName(), (f.getName() + "/" + p.getName()));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
        });
    }

    public void callProgram(String javaFile, String fileName) throws IOException, InterruptedException {
        String loc = "../DMUtil/Transform/" + fileName + "/" + javaFile;
        System.out.println(loc);
        // String loc1 = "java -cp /home/anshika/DMUtil/Validate/CmCommonValidation"+"
        // "+"/home/anshika/DMUtil/Input/"+nn+"
        // "+"/home/anshika/DMUtil/Validate/Mapping_Sheet/"+nn+".csv";
        String command[] = { "javac", loc };
        String javaFileName = javaFile.replace(".java", "");
        System.out.println(javaFileName);
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();
        process.waitFor();
        if (process.getErrorStream().read() != -1) {
            print("Compilation Errors", process.getErrorStream());
        }
        System.out.println(process.exitValue());
        System.out.println(" Transform Inside file ");
        if (process.exitValue() == 0) {
            process = new ProcessBuilder(
                    new String[] { "java", "-cp", "../DMUtil/Transform/" + fileName + "/", javaFileName }).start();
            System.out.println("Transform Processs");
            if (process.getErrorStream().read() != -1) {
                print("Errors ", process.getErrorStream());
            } else {
                print("Output ", process.getInputStream());
            }
        }
        File filename = new File("../DMUtil/Transform/" + fileName);
        filename.setLastModified(System.currentTimeMillis());
        // process.waitFor();
        // process.exitValue()
    }

    private static void print(String status, InputStream input) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        System.out.println("************* " + status + "***********************");
        String line = null;
        while ((line = in.readLine()) != null) {
            System.out.println(line);
        }
        in.close();
    }

    public List<String> allModifiedFiles(String foldername) {
        List<String> modifiedFiles = new ArrayList<>();
        // File folder = new
        // File("../DMUtil/Reports/Transform/Summary_Reports/"+foldername);
        Path src = Paths.get("../DMUtil/Reports/Transform/Summary_Reports/" + foldername);
        File f = src.toFile();
        List<String> subFolder = new ArrayList<>();
        Arrays.stream(f.listFiles()).filter(p -> p.isDirectory()).forEach(p -> {
            p.listFiles();
            System.out.println(p.toPath() + "pp");
            subFolder.add(p.toPath().toString());
        });
        for (int i = 0; i < subFolder.size(); i++) {
            modifiedFiles.add(validate.lastModifiled(subFolder.get(i)));
        }
        subFolder.clear();
        // Arrays.stream(f.listFiles()).filter(p -> p.getName().endsWith(".csv")).forEach(p -> {
        //     p.listFiles();
        //     System.out.println(p.toPath() + "kk");
        //     modifiedFiles.add(p.toPath().toString());

        // });
        File path = new File("../DMUtil/Reports/Transform/Summary_Reports/" + foldername);
        System.out.println(path);
        long time = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(2);
        File[] files = path.listFiles(pathname -> pathname.lastModified() >= time && pathname.getName().endsWith(".csv"));
       System.out.println("csv files"+files);
       for (File a: files){
        System.out.println(a.getName());
       }
        if (files == null || files.length == 0) {
            System.out.println("no files in folder");
            return null;
        }

        File lastModifiedFile = files[0];
        
            

            
        
        System.out.println("File NAME"+lastModifiedFile);
         modifiedFiles.add(lastModifiedFile.toString());

        System.out.println(modifiedFiles);
        // validate.lastModifiled();
        return modifiedFiles;
    }

    public String downloadValidate(List<String> dirName, String folderName) throws IOException {
        String zipFile = ("../Downloads/" + folderName + "_TransformReports.zip");
        FileOutputStream fos = new FileOutputStream(zipFile);
        ZipOutputStream zos = new ZipOutputStream(fos);

        for (int i = 0; i < dirName.size(); i++) {
            if (dirName.get(i) != null) {
                System.out.println(dirName.get(i));
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
            }else{
                System.out.println("NULLLL");
            }

        }

        // close the ZipOutputStream
        zos.close();

        return "Success";
    }

}

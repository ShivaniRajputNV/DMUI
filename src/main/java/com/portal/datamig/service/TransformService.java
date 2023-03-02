package com.portal.datamig.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransformService {
    @Autowired
    ValidateService validate;
    private static String home = System.getProperty("user.home");


    public void transformList(String name) {
        Path src = Paths.get(home+File.separator+"DMUtil"+File.separator+"Transform"+File.separator + name + File.separator);
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
        String loc = home+File.separator+"DMUtil"+File.separator+"Transform"+File.separator + fileName + File.separator + javaFile;
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
                    new String[] { "java", "-cp", home+File.separator+"DMUtil"+File.separator+"Transform"+File.separator + fileName + File.separator, javaFileName }).start();
            System.out.println("Transform Processs");
            if (process.getErrorStream().read() != -1) {
                print("Errors ", process.getErrorStream());
            } else {
                print("Output ", process.getInputStream());
            }
        }
        File filename = new File(home+File.separator+"DMUtil"+File.separator+"Transform"+File.separator + fileName);
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
        // File(home+"/DMUtil/Reports/Transform/Summary_Reports/"+foldername);
        Path src = Paths.get(home+File.separator+"DMUtil"+File.separator+"Reports"+File.separator+"Transform"+File.separator+"Summary_Reports"+File.separator + foldername);
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
        File path = new File(home+File.separator+"DMUtil"+File.separator+"Reports"+File.separator+"Transform"+File.separator+"Summary_Reports"+File.separator + foldername);
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
        String zipFile = (home+File.separator+"Downloads"+File.separator + folderName + "_TransformReports.zip");
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

    //view report     
    public List<String> transformReports(String filepath) throws FileNotFoundException {
        List<String> data = new ArrayList();
        Scanner sc = new Scanner(new File(filepath));
        sc.useDelimiter("\r\n"); // sets the delimiter pattern    
        while (sc.hasNext()) // returns a boolean value    
        {
          data.add(sc.next());
        }
        // System.out.println("vajgx" + data + "csvDATA");    
        sc.close(); 
        return data;
    }

//archive transform files
    public void archieveTransformFiles(String dest, String src) {
        File destinationDir = new File(home+File.separator+"DMUtil"+File.separator+"Archieve"+File.separator+dest+File.separator);
        File directory = new File(home+File.separator+"DMUtil"+File.separator+src+File.separator);
        File[] subfiles = directory.listFiles();
        for(File f:subfiles){
            if(f.isFile()){
            System.out.println("dest Dir"+destinationDir.getAbsolutePath()+f.getName());
            new File(destinationDir.getAbsolutePath()+f.getName()).delete() ;//remove the duplicate       
             try {
                FileUtils.moveFileToDirectory(f, destinationDir, true);
            } catch (IOException e) {
                // TODO Auto-generated catch block            
                e.printStackTrace();
            }finally{
                System.out.println("No files to Archieve");
            }
            }else{
                System.out.println(f.getName());
                File[] sf = f.listFiles();
                for(File sub:sf){
                    if(sub.isFile()){
                    System.out.println("dest Dir"+destinationDir.getAbsolutePath()+sub.getName());
                    new File(destinationDir.getAbsolutePath()+sub.getName()).delete() ;//remove the duplicate                
                    try {
                        File destinationSubDir = new File(home+File.separator+"DMUtil"+File.separator+"Archieve"+File.separator+dest+File.separator+f.getName()+File.separator);
                        FileUtils.moveFileToDirectory(sub, destinationSubDir, true);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block                    
                        e.printStackTrace();
                    }finally{
                        System.out.println("No files to Archieve");
                    }
                    }
                } 
            }
        }
    }
}

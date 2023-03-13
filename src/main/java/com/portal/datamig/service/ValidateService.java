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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

@Service
public class ValidateService {
    private static String home = System.getProperty("user.home");
    private static List<String> outputEV = null;

    // upload/copy for validate page
    public void copyCSVFiles(String loc) throws IOException {

        Path src = Paths.get(home + File.separator + "Client" + File.separator + loc + File.separator);
        Path dest = Paths.get(
                home + File.separator + "DMUtil" + File.separator + "Input" + File.separator + loc + File.separator);
        System.out.println(src.toString());
        File f = src.toFile();

        Arrays.stream(f.listFiles()).filter(p -> !p.isDirectory()).forEach(p -> {
            try {
                FileUtils.copyFileToDirectory(p, dest.toFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        // String srcfolder = home+"/DMUtil/Reports/Validate/";
        // String destFolder = home+"/DMUtil/Archieve";
        // FileUtils.moveDirectoryToDirectory(FileUtils.getFile(srcfolder),
        // FileUtils.getFile(destFolder),true);

    }

    public Map<String, List<String>> callValidationProgram(String selectedFolder)
            throws IOException, InterruptedException {
        String loc = home + File.separator + "DMUtil" + File.separator + "Validate" + File.separator
                + "CmCommonValidation.java";
        // String loc1 = "java -cp ../DMUtil/Validate/CmCommonValidation" + " " +
        // home+"/DMUtil/Input/" + selectedFolder + " "
        // + home+"/DMUtil/Validate/Mapping_Sheet/" + selectedFolder + ".csv";
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
                    home + File.separator + "DMUtil" + File.separator + "Validate" + File.separator,
                    "CmCommonValidation",
                    home + File.separator + "DMUtil" + File.separator + "Input" + File.separator + selectedFolder,
                    home + File.separator + "DMUtil" + File.separator + "Validate" + File.separator + "Mapping_Sheet"
                            + File.separator + selectedFolder.replace("_", "") + ".csv" })
                    .start();
            if (process.getErrorStream().read() != -1) {
                error = print("Errors ", process.getErrorStream());
                result.put("Error", error);

            } else {
                output = print("Output ", process.getInputStream());
                result.put("Output", output);
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

    public Map<String, List<String>> callSecondaryValidationProgram(String selectedFolder)
            throws IOException, InterruptedException {
        String loc = home + File.separator + "DMUtil" + File.separator + "Validate" + File.separator
                + "CmCommonValidation.java";
        // String loc1 = "java -cp /home/anshika/DMUtil/Validate/CmCommonValidation" + "
        // " + "/home/anshika/DMUtil/Input/"
        // + selectedFolder + " " + "/home/anshika/DMUtil/Validate/Mapping_Sheet/" +
        // selectedFolder + ".csv";
        String[] nn = selectedFolder.split("/");
        System.out.println("SFFF" + selectedFolder);
        System.out.println(nn[1]);
        String command[] = { "javac", loc };
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();
        process.waitFor();
        Map<String, List<String>> result = new HashMap();
        List<String> output = new ArrayList<>();
        List<String> error = new ArrayList<>();
        if (process.getErrorStream().read() != -1) {
            print("Compilation Errors", process.getErrorStream());
        }
        System.out.println(process.exitValue());
        if (process.exitValue() == 0) {
            process = new ProcessBuilder(new String[] { "java", "-cp",
                    home + File.separator + "DMUtil" + File.separator + "Validate" + File.separator,
                    "CmCommonValidation",
                    home + File.separator + "DMUtil" + File.separator + "Input" + File.separator + selectedFolder,
                    home + File.separator + "DMUtil" + File.separator + "Validate" + File.separator + "Mapping_Sheet"
                            + File.separator
                            + nn[1].replace("_", "") + ".csv" })
                    .start();
            if (process.getErrorStream().read() != -1 || process.getInputStream().read() != -1) {
                error = print("Errors ", process.getErrorStream());
                result.put("Error", error);
                output = print("Output ", process.getInputStream());
                result.put("Output", output);

            }
        }
        // process.waitFor();
        // process.exitValue();
        return result;
    }

    public List<String> entityListSecondary(String n) throws IOException {
        Path src = Paths
                .get(home + File.separator + "DMUtil" + File.separator + "Input" + File.separator + n + File.separator);
        List<String> sList = new ArrayList<>();
        File f = src.toFile();
        Arrays.stream(f.listFiles()).filter(p -> p.isDirectory()).forEach(p -> {
            System.out.println(p);
            sList.add(p.getName());

        });
        // System.out.print(sList);
        return sList;
    }

    public List<String> callEntityValidationProgram(String selectedFolder) throws IOException, InterruptedException {
        Path src = Paths.get(home + File.separator + "DMUtil" + File.separator + "Validate" + File.separator
                + selectedFolder + File.separator);
        File f = src.toFile();

        Arrays.stream(f.listFiles()).filter(p -> p.getName().endsWith(".java")).forEach(p -> {
            try {

                if (selectedFolder.contains(File.separator)) {
                    System.out.println(p.getName() + " File " + f.getName());
                    outputEV = callProgram(p.getName(), selectedFolder);
                } else {
                    System.out.println(p.getName() + " File " + f.getName());
                    outputEV = callProgram(p.getName(), f.getName());
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        });
        // System.out.println(outputEV+"dvghkj");

        return outputEV;
    }

    public List<String> callProgram(String javaFile, String fileName) throws IOException, InterruptedException {
        String loc = home + File.separator + "DMUtil" + File.separator + "Validate" + File.separator + fileName
                + File.separator + javaFile;
        System.out.println(loc);
        List<String> output = new ArrayList<>();
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
        System.out.println(" Secondary Validate Inside file ");
        if (process.exitValue() == 0) {
            process = new ProcessBuilder(
                    new String[] { "java", "-cp", home + File.separator + "DMUtil" + File.separator + "Validate"
                            + File.separator + fileName + File.separator, javaFileName })
                    .start();
            System.out.println("Secondary Processs");
            if (process.getErrorStream().read() != -1) {
                print("Errors ", process.getErrorStream());
            } else {
                output = print("Output ", process.getInputStream());
            }
        }

        // process.waitFor();
        // process.exitValue()

        return output;
    }

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

    // view report
    public List<String> reports(String filepath) throws FileNotFoundException {
        List<String> data = new ArrayList();
        Scanner sc = new Scanner(new File(filepath));
        sc.useDelimiter("\n"); // sets the delimiter pattern
        while (sc.hasNext()) // returns a boolean value
        {

            data.add(sc.next());

        }
        // System.out.println("vajgx" + data + "csvDATA");
        sc.close();
        return data;

    }

    public String downloadValidate(List<String> dirName, String folderName) throws IOException {
        String zipFile = (home + File.separator + "Downloads" + File.separator + folderName + "_ValidateReports.zip");
        FileOutputStream fos = new FileOutputStream(zipFile);
        ZipOutputStream zos = new ZipOutputStream(fos);

        for (int i = 0; i < dirName.size(); i++) {

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

    public void archieveFiles(String dest, String src) {
        File destinationDir = new File(home + File.separator + "DMUtil" + File.separator + "Archive" + File.separator
                + dest + File.separator);
        File directory = new File(
                home + File.separator + "DMUtil" + File.separator + "Reports" + File.separator + src + File.separator);
        System.out.println(directory.getAbsolutePath());
        File[] subfiles = directory.listFiles();
        for (File f : subfiles) {
            if (f.isFile()) {
                System.out.println("dest Dir" + destinationDir.getAbsolutePath() + f.getName());
                new File(destinationDir.getAbsolutePath() + f.getName()).delete();// remove the
                                                                                  // duplicate                   
                try {
                    FileUtils.moveFileToDirectory(f, destinationDir, true);
                } catch (IOException e) {
                    // TODO Auto-generated catch block                           
                    e.printStackTrace();
                } finally {
                    System.out.println("No files to Archive");
                }
            } else {
                File[] sub = f.listFiles();
                for (File sf : sub) {
                    destinationDir = new File(home + File.separator + "DMUtil" + File.separator + "Archive"
                            + File.separator + dest + File.separator + f.getName() + File.separator);
                    System.out.println("dest Dir" + destinationDir.getAbsolutePath() + sf.getName());
                    new File(destinationDir.getAbsolutePath() + sf.getName()).delete();// remove the
                                                                                       // duplicate                       
                    try {
                        FileUtils.moveFileToDirectory(sf, destinationDir, true);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block                               
                        e.printStackTrace();
                    } finally {
                        System.out.println("No files to Archive");
                    }
                }
            }
        }
    }
}

package com.portal.datamig.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportService {
    @Autowired
    ValidateService validate;
    String home = System.getProperty("user.home");

    public List<List<String>> reportTransformTab(String entityToView)throws FileNotFoundException{
    List<List<String>> reportList = new ArrayList<>();
    List<String> reportnames = new ArrayList<>();
        File path = new File(home+File.separator+"DMUtil"+File.separator+"Reports"+File.separator+entityToView+File.separator);
       
        File[] files = path.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                reportnames.add(files[i].getPath());
                List<String> reportname = new ArrayList<>();
                System.out.println(files[i].getName());
                reportname.add(path.getName());
                reportList.add(reportname);
                reportList.add(validate.reports(files[i].getPath()));
                // reportname.clear();
            }else{
                File[] subFiles = files[i].listFiles();
                for (int j = 0; j < subFiles.length; j++) {
                    System.out.println(subFiles[j].getPath());
                    List<String> reportname = new ArrayList<>();
                    reportname.add(files[i].getName());
                    reportList.add(reportname);
                    reportnames.add(subFiles[j].getPath());
                    reportList.add(validate.reports(subFiles[j].getPath()));
                    // reportname.clear();

                }
            }
        }
        System.out.println(reportList);
        return reportList;
    }
    public String downloadTransformRep(String folderName) throws IOException {
        String[] name = folderName.split("/");
        String zipFile = (home+File.separator+"Downloads"+File.separator + name[name.length-1] + "_"+name[0]+"Reports.zip");
        FileOutputStream fos = new FileOutputStream(zipFile);
        ZipOutputStream zos = new ZipOutputStream(fos);
        List<String> reportnames = new ArrayList<>();
        File path = new File(home+File.separator+"DMUtil"+File.separator+"Reports"+File.separator+folderName+File.separator);
       
        File[] files = path.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                reportnames.add(files[i].getPath());
                List<String> reportname = new ArrayList<>();
            }else{
                File[] subFiles = files[i].listFiles();
                for (int j = 0; j < subFiles.length; j++) {
                    reportnames.add(subFiles[j].getPath());
                   

                }
            }
        }

        for (int i = 0; i < reportnames.size(); i++) {
            if (reportnames.get(i) != null) {
                System.out.println(reportnames.get(i));
                File srcFile = new File(reportnames.get(i));
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
    public String downloadValidateTab(List<String> dirName, String folderName) throws IOException {
        String zipFile = (home+File.separator+"Downloads"+File.separator + folderName + "_ValidateReports.zip");
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

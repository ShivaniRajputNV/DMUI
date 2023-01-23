package com.portal.datamig.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.springframework.stereotype.Service;

@Service
public class TransformService {
    public void transformList(String name){
        Path src = Paths.get("../DMUtil/Transform/"+name+ "/");
            File f = src.toFile();
            Arrays.stream(f.listFiles()).filter(p -> p.getName().endsWith(".java")).forEach(p -> {
               try {
                System.out.println(p.getName()+" File "+f.getName());
                 callProgram(p.getName(),f.getName());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
                 } );
                Arrays.stream(f.listFiles()).filter(p -> p.isDirectory()).forEach(p -> {
                    Arrays.stream(p.listFiles()).filter(n -> n.getName().endsWith(".java")).forEach(n -> {
                        System.out.println(p.getName()+" : JAVA File "+n.getName());
                        try {
                            callProgram(n.getName(),(f.getName()+"/"+p.getName()));
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                         } );
                });
            }
    
    public void callProgram(String javaFile,String fileName) throws IOException, InterruptedException{
        String loc = "../DMUtil/Transform/"+fileName+"/"+javaFile;
        System.out.println(loc);
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
            System.out.println(" Transform Inside file ");
            if (process.exitValue() == 0) {
                process = new ProcessBuilder(new String[]{"java", "-cp", "../DMUtil/Transform/"+fileName+"/", javaFileName}).start();
                System.out.println("Transform Processs");
                if (process.getErrorStream().read() != -1) {
                    print("Errors ", process.getErrorStream());
                } else {
                    print("Output ", process.getInputStream());
                }
            }
            File filename = new File("../DMUtil/Transform/"+fileName);
            filename.setLastModified(System.currentTimeMillis());
            // process.waitFor();
            // process.exitValue()
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
    
}

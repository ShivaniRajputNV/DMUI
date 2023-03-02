package com.portal.datamig.service;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
  
    @Autowired
     EncryptDecryptService encryptDecryptService;
    
    
    String home = System.getProperty("user.home");

    public String authenticate(Map<String, String> data) {
        System.out.print(">>>>>>>>>>>>>>>>"+home);
        JSONParser jsonParser = new JSONParser();
        JSONParser parser = new JSONParser();
        String uname=null;
        String passwd=null;
        JSONObject jsonObject;
        String result=null;
        try {
            jsonObject = (JSONObject) parser.parse(new FileReader(home+File.separator+"DMUtil"+File.separator+"db"+File.separator+"users.json"));
            System.out.println("<br>"+jsonObject);
            List<String> list = new ArrayList<>();
           int j=0;
           for (Map.Entry<String,String> map: data.entrySet())
           {
               String val = map.getValue();
               list.add(val);
           }
           System.out.print(list);
           uname = list.get(0);
           passwd = list.get(1);
           System.out.println(uname+passwd);
            JSONArray user = (JSONArray)jsonObject.get("user");
            // for item output 3
            for (int i = 0; i < user.size(); i++) {
                JSONObject jsonObjectRow = (JSONObject) user.get(i);
                String name = (String) jsonObjectRow.get("username");
                String password = (String) jsonObjectRow.get("password");
                String enpassword = encryptDecryptService.encryptPassword(passwd);
//                System.out.println("<br>Name="+name +"; Password="+password);
System.out.println(name);

                if(name.equals(uname) && password.equals(enpassword))
                {   
                    System.out.print("Success");
                    result = uname;
                    break;
                }
                else {
                    System.out.print("Invalid Username and Password");
                    result = null;
                }
            }
        } catch (Exception e) {
            System.out.println("Error: "+e);
        }
        return result;
    }

public String getRole(String uname){
    JSONParser jsonParser = new JSONParser();
    JSONParser parser = new JSONParser();
    JSONObject jsonObject;
    String result=null;
    try {
        System.out.print(">>>>>>>>"+home);
        jsonObject = (JSONObject) parser.parse(new FileReader(home+File.separator+"DMUtil"+File.separator+"db"+File.separator+"users.json"));
        System.out.println("<br>"+jsonObject);
        JSONArray user = (JSONArray)jsonObject.get("user");
        // for item output 3            
        for (int i = 0; i < user.size(); i++) {
            JSONObject jsonObjectRow = (JSONObject) user.get(i);
            String name = (String)jsonObjectRow.get("username");
            String role = (String) jsonObjectRow.get("role");
            if(name.equals(uname))
            { 
                result = role;
                break;
            }
            else {
                result = null;
            }
        }
    } catch (Exception e) {
        System.out.println("Error: "+e);
    }
    return result;
}
}
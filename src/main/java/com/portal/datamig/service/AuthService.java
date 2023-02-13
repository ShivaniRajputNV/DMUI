package com.portal.datamig.service;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {
    public String authenticate(Map<String, String> data) {
        JSONParser jsonParser = new JSONParser();
        JSONParser parser = new JSONParser();
        String uname=null;
        String passwd=null;
        JSONObject jsonObject;
        String result=null;
        try {
            jsonObject = (JSONObject) parser.parse(new FileReader("../DMUtil/db/users.json"));
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
//                System.out.println("<br>Name="+name +"; Password="+password);
System.out.println(name);
                if(name.equals(uname) && password.equals(passwd))
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
}

package com.portal.datamig.service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@Service
public class UserService {

    @Autowired
    EncryptDecryptService encryptDecryptService;

    public static String home = System.getProperty("user.home");

    public List<String> getRoles() {
        List<String> rolenames = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(home+ File.separator+"DMUtil"+File.separator+"db"+File.separator+"roles.json"));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray roles = (JSONArray) jsonObject.get("roles");
            for (int i = 0; i < roles.size(); i++) {
                JSONObject jsonObjectRow = (JSONObject) roles.get(i);
                String role = (String) jsonObjectRow.get("role");
                rolenames.add(role);
            }
            System.out.println(rolenames);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rolenames;
    }

    public List<String> getSecurityQuestion() {
        List<String> security_question = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(home +File.separator+ "DMUtil"+File.separator+"db"+File.separator+"security_question.json"));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray questions = (JSONArray) jsonObject.get("sec_questions");
            for (int i = 0; i < questions.size(); i++) {
                JSONObject jsonObjectRow = (JSONObject) questions.get(i);
                String question = (String) jsonObjectRow.get("question");
                security_question.add(question);
            }
            System.out.println(security_question);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return security_question;
    }

    /// for add user
    public String addUser(LinkedHashMap<String, String> data) {
        SecureRandom rand = new SecureRandom();
        int upperbound = 1000;
        int random = rand.nextInt(upperbound);
        JSONObject user = new JSONObject();
        JSONParser parser = new JSONParser();
        String uid = data.get("username").substring(0, 2).toUpperCase().concat(String.valueOf(random));
        try {
            JSONObject jsonObject = (JSONObject) parser
                    .parse(new FileReader(home+File.separator+"DMUtil"+File.separator+"db"+File.separator+"users.json"));
            JSONArray userList = (JSONArray) jsonObject.get("user");
            System.out.println(userList);
            user.put("id", uid);

            String password = data.get("password");
            String enpassword = encryptDecryptService.encryptPassword(password);
            user.put("password", enpassword);

            for (Map.Entry<String, String> entry : data.entrySet()) {
                // System.out.println("Key = " + entry.getKey() + ", Value = " +

                if (!entry.getKey().equals("password")) {
                    user.put(entry.getKey(), entry.getValue());
                }

            }
            userList.add(user);
            System.out.println(userList);
            FileWriter file = new FileWriter(home+File.separator+"DMUtil"+File.separator+"db"+File.separator+"users.json");
            final ObjectMapper mapper = new ObjectMapper();
            final ObjectWriter writer = mapper.writer().withRootName("user");
            final String json = writer.writeValueAsString(userList);
            file.write(json);
            file.flush();
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }

    // delete user
    public String deleteUser(String username) {
        JSONObject jsonObject;
        JSONParser parser = new JSONParser();
        String result = null;
        // code for delete
        try {
            jsonObject = (JSONObject) parser.parse(new FileReader(home+File.separator+"DMUtil"+File.separator+"db"+File.separator+"users.json"));
            System.out.println("<br>" + jsonObject);
            JSONArray user = (JSONArray) jsonObject.get("user");
            Map<String, Object> map = new HashMap<>();
            // for item output 3
            for (int i = 0; i < user.size(); i++) {
                JSONObject jsonObjectRow = (JSONObject) user.get(i);
                System.out.println("Json Object row" + jsonObjectRow);
                String name = (String) jsonObjectRow.get("username");
                System.out.println(name);
                if (username.equals(name)) {
                    result = "success";
                    user.remove(i);
                    // user.get(i)).replace("password",list.get(0));
                    FileWriter fileWriter = new FileWriter(home+File.separator+ "DMUtil"+File.separator+"db"+File.separator+"users.json");
                    final ObjectMapper mapper = new ObjectMapper();
                    final ObjectWriter writer = mapper.writer().withRootName("user");
                    final String json = writer.writeValueAsString(user);
                    fileWriter.write(json);
                    fileWriter.flush();
                    fileWriter.close();
                    break;
                } else {
                    result = "fail";
                    System.out.println(result);
                }
            }
        } catch (

        Exception e) {
            System.out.println("Error: " + e);
        }
        // end code for delete
        return "result";
    }

    // Get User By Id    
    public Map<String, JSONObject> getUserById(String userid) {
        JSONObject jsonObject;
        JSONParser parser = new JSONParser();
        HashMap<String, JSONObject> userData = new HashMap<String, JSONObject>();
        // code for delete        
        try {
            jsonObject = (JSONObject) parser.parse(new FileReader(home+File.separator+"DMUtil"+File.separator+"db"+File.separator+"users.json"));
            System.out.println("<br>" + jsonObject);
            JSONArray user = (JSONArray) jsonObject.get("user");
            // for item output 3           
             for (int i = 0; i < user.size(); i++) {
                JSONObject jsonObjectRow = (JSONObject) user.get(i);
                System.out.println("Json Object row" + jsonObjectRow);
                String id = (String) jsonObjectRow.get("id");
                String name = (String) jsonObjectRow.get("username");
                String password = (String) jsonObjectRow.get("password");
                String role = (String) jsonObjectRow.get("role");
                String security_question = (String) jsonObjectRow.get("security_question");
                String security_answer = (String) jsonObjectRow.get("security_answer");
                if (userid.equals(id)) {
                    userData.put("User" + i, jsonObjectRow);
                    break;
                }
            }
            System.out.println("result " + userData);
            for (Map.Entry<String, JSONObject> me : userData.entrySet()) {
                // Printing keys               
                 System.out.print(me.getKey() + ":");
                System.out.println(me.getValue());
            }
        } catch (
        Exception e) {
            System.out.println("Error: " + e);
        }
        // end code for delete        // return userData;       
         return userData;
    }

    // get user
    public Map<String, JSONObject> getUser() {
        JSONObject jsonObject;
        JSONParser parser = new JSONParser();
        String result = null;
        Map<String, String> map = new HashMap<>();
        Map<String, Object> userMap = new HashMap<>();
        HashMap<String, JSONObject> userData = new HashMap<String, JSONObject>();
        Map<String, JSONObject> filteredMap = new HashMap<String, JSONObject>();
        // code for delete
        try {
            jsonObject = (JSONObject) parser.parse(new FileReader(home+File.separator+"DMUtil"+File.separator+"db"+File.separator+"users.json"));
            System.out.println("<br>" + jsonObject);
            JSONArray user = (JSONArray) jsonObject.get("user");
            // for item output 3
            for (int i = 0; i < user.size(); i++) {
                JSONObject jsonObjectRow = (JSONObject) user.get(i);
                System.out.println("Json Object row" + jsonObjectRow);
                String id = (String) jsonObjectRow.get("id");
                String name = (String) jsonObjectRow.get("username");
                String role = (String) jsonObjectRow.get("role");
                userData.put("User" + i, jsonObjectRow);
                // userMap.put("user"+i++, map);
            }
            System.out.println("result " + userData);
            for (Map.Entry<String, JSONObject> me : userData.entrySet()) {
                // Printing keys
                System.out.print(me.getKey() + ":");
                System.out.println(me.getValue());
            }
            filteredMap = userData.entrySet().stream()
                    .filter(e -> "password".equals(e.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            System.out.println("catch" + filteredMap);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        // end code for delete // return userData;
        return userData;
    }
//edit user
    public String editUser(Map<String, String> data) {
        boolean success = false;
        JSONObject jsonObject;
        List<Object> userList = new ArrayList<>();
        JSONParser parser = new JSONParser();
        String result = null;
        String uid = null;
        String r = null;
        // List<String> updatedRole = new ArrayList<>();        
        uid = data.get("id");
        r = data.get("userRole");
        System.out.println("id = " + uid + "" + "role = " + r);
        // code for change role       
         try {
            jsonObject = (JSONObject) parser.parse(new FileReader(home+File.separator+ "DMUtil"+File.separator+"db"+File.separator+"users.json"));
            // System.out.println("<br>" + jsonObject);            
            JSONArray user = (JSONArray) jsonObject.get("user");
            userList.add(user);
            Map<String, Object> map = new HashMap<>();
            // for item output 3            
            for (int i = 0; i < user.size(); i++) {
                JSONObject jsonObjectRow = (JSONObject) user.get(i);
                System.out.println("Json Object row" + jsonObjectRow);
                String role = (String) jsonObjectRow.get("role");
                String id = (String) jsonObjectRow.get("id");
                if (id.equals(uid)) {
                    result = "success";
                    ((JSONObject) user.get(i)).replace("role", r);
                    FileWriter fileWriter = new FileWriter(home+File.separator+"DMUtil"+File.separator+"db"+File.separator+"users.json");
                    final ObjectMapper mapper = new ObjectMapper();
                    final ObjectWriter writer = mapper.writer().withRootName("user");
                    final String json = writer.writeValueAsString(user);
                    fileWriter.write(json);
                    fileWriter.flush();
                    fileWriter.close();
                    success = true;
                    break;
                } else {
                    result = "fail";
                    System.out.println(result);
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return result;
    }
}

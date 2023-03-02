package com.portal.datamig.controller;

import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import com.portal.datamig.service.UserService;

@Controller
@RequestMapping("/api")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/addUser")
    public String addUser(@RequestParam LinkedHashMap<String, String> data, Model model) {
        System.out.println(userService.addUser(data));
        System.out.print(data);
        model.addAttribute("data", data);
        return "redirect:/api/setting";
    }

    @GetMapping("/deleteUser")
    public String deleteUser(@RequestParam String username) {
        if (!username.equals("") && username != null) {
            System.out.println(userService.deleteUser(username));
        }
        return "redirect:/api/setting";
    }

    @GetMapping("/getUser")
    public String getUser(Model model) {
        String username = "prateek";
        System.out.println(userService.getUser());
        Map<String, JSONObject> map = userService.getUser();

        Map<String, JSONObject> filteredMap = map.entrySet().stream()
                .filter(e -> e.getKey().contains("password"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        for (String name : filteredMap.keySet()) {
            System.out.println("test" + name);
        }
        model.addAttribute("user", filteredMap);
        return "redirect:/api/setting";
    }

    // Get User By Id    
    @GetMapping("/getUser/{userid}") 
    @ResponseBody
    public Map<String, JSONObject> getUserById(@PathVariable String userid,Model model) {
    System.out.println("sdddddddddddddfgt"+userService.getUserById(userid)); 
    Map<String,JSONObject> map = userService.getUserById(userid);
    System.out.println("hello"+map);
    // model.addAttribute("userForEdit", map); 
    return map;
 }

    @GetMapping("/fetchRole")
    @ResponseBody    
    public List<String> fetchRole() {
        List<String> roles = userService.getRoles();
        return roles;
    }
    @GetMapping("/editUser")
    public String editUser(@RequestParam LinkedHashMap<String, String> data, Model model) {
        System.out.println("edited data" + data);
        System.out.println("Answer"+userService.editUser(data));
        // model.addAttribute("updated", value);       
         return "redirect:/api/setting";
    }
}

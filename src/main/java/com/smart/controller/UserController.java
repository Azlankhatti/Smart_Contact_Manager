package com.smart.controller;


import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;

@Controller
@RequestMapping("/user")
public class UserController {

        @Autowired
        private UserRepository userRepository;

        @ModelAttribute
        public void addCommonData(Model model , Principal principal){

            String userName = principal.getName();
            System.out.println("USERNAME " +userName);
            //get the user using userName(Email)

            User user =  userRepository.getUserByUserName(userName);

            model.addAttribute("user",user);

            System.out.println("USER "+user);

        }


        //dashboard home
    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal){

        model.addAttribute("title","User Dashboard");
        return "normal/user_dashboard";
    }

    //open add form handler
    @GetMapping("/add-contact")
    public String openAddContactForm(Model model){

            model.addAttribute("contact",new Contact());
        model.addAttribute("title","Add Contact");
        return "normal/add_contact_form";
    }

    //processing add contact form
        @PostMapping("/process-contact")
        public String processContact(@ModelAttribute Contact contact,
                                     @RequestParam("profileImage") MultipartFile file,
                                     Principal principal){

            try{
            String name =principal.getName();
           User user =  this.userRepository.getUserByUserName(name);

           //processing and uploading file

                if(file.isEmpty()){
                    //if the file is empty then try our message
                    System.out.println("File is empty");
                }else{
                    //file the file to folder and update the name contact
                    contact.setImage(file.getOriginalFilename());

                   File saveFile =new ClassPathResource("static/image").getFile();

                   Path path =  Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());

                    Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);

                    System.out.println("image is upload");


                }

           contact.setUser(user);
            user.getContact().add(contact);

            this.userRepository.save(user);

            System.out.println("DATA " +contact);

            System.out.println("Added to database");

            } catch (Exception e) {


                System.out.println("ERROR "+e.getMessage());
                e.printStackTrace();
            }



            return "normal/add_contact_form";
        }
}



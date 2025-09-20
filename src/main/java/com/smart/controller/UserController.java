package com.smart.controller;


import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

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
        public String processContact(@ModelAttribute Contact contact,Principal principal){


            String name =principal.getName();
           User user =  this.userRepository.getUserByUserName(name);

           contact.setUser(user);
            user.getContact().add(contact);

            this.userRepository.save(user);

            System.out.println("DATA " +contact);

            System.out.println("Added to database");
            return "normal/add_contact_form";
        }
}



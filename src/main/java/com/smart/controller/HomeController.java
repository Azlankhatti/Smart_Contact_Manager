package com.smart.controller;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/")
    public String home(Model model){
        model.addAttribute("title","Home - smart contact manager");
        return "home";
    }

    @RequestMapping("/about")
    public String about(Model model){
        model.addAttribute("title","About -smart contact manager");
        return "about";
    }

    @RequestMapping("/signup")
    public String signup(Model model){
        model.addAttribute("title","Register -smart contact manager");
        model.addAttribute("user",new User());
        return "signup";
    }

    @RequestMapping(value = "/do_register",method = RequestMethod.POST)
     public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result1,
                                @RequestParam(value="agreement",defaultValue= "false") boolean agreement, Model model, RedirectAttributes redirectAttributes, HttpSession session){


        try{

            if(!agreement){


                System.out.println("you have not agree the terms and conditons");
                throw new Exception("you have not agree the terms and conditons");
            }

            if (result1.hasErrors()){

                System.out.println("ERROR" + result1.toString());
                model.addAttribute("user",user);

                return "signup";
            }


            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            System.out.println("Agreement " + agreement);
            System.out.println("User " + user);

            User result =  this.userRepository.save(user);


            model.addAttribute("user",new User());

            redirectAttributes.addFlashAttribute("message" ,new Message("successfully registered","alert-success"));
            System.out.println(5);

            return "redirect:/signup";

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(6);

            model.addAttribute("user",user);
            redirectAttributes.addFlashAttribute("message" ,new Message("Something went wrong !! "+e.getMessage(),"alert-danger"));
            return "redirect:/signup";
        }

    }



    //handler for custom login
    @GetMapping("/signin")
    public String customLogin(Model model){

        model.addAttribute("title" , "Login Page");
        return "login";
    }
}

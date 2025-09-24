package com.smart.controller;


import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;


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
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {

        try {
            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);

            //processing and uploading file

            if (file.isEmpty()) {
                //if the file is empty then try our message
                System.out.println("File is empty");
                contact.setImage("contact.png");

            } else {
                //file the file to folder and update the name contact
                contact.setImage(file.getOriginalFilename());

                File saveFile = new ClassPathResource("static/image").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("image is upload");


            }

            contact.setUser(user);
            user.getContact().add(contact);

            this.userRepository.save(user);

            System.out.println("DATA " + contact);

            System.out.println("Added to database");

            //Show message success
            redirectAttributes.addFlashAttribute("message", new Message("Your contact is added !! Add more", "success"));
            return "redirect:/user/add-contact";


        } catch (Exception e) {
            System.out.println("ERROR " + e.getMessage());
            e.printStackTrace();
            //show error message
            redirectAttributes.addFlashAttribute("message", new Message("Something went wrong !! Try again..", "danger"));
            return "redirect:/user/add-contact";

        }


    }



    //show contacts handler
    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page,Model m,Principal principal){
        m.addAttribute("title","Show User Contacts");
        //Contact list send
//        String userName =principal.getName();
//
//        User user = this.userRepository.getUserByUserName(userName);
//        user.getContact();

            String userName = principal.getName();
           User user = this.userRepository.getUserByUserName(userName);

              Pageable pageable1 =  PageRequest.of(page,5);

//        List<Contact>  contacts  = this.contactRepository.findContactsByUser(user.getId(),pageable1);
        Page<Contact> contacts  = this.contactRepository.findContactsByUser(user.getId(),pageable1);

        m.addAttribute("contacts", contacts);
        m.addAttribute("currentPage",page);

         m.addAttribute("totalPages",contacts.getTotalPages());


        return "normal/show_contacts";
    }

        //show Contact details
    @RequestMapping("/{cId}/contact")
    public String showContactDetail(@PathVariable("cId") int cId, Model model ,Principal principal){

        Optional<Contact> contactOptional =  this.contactRepository.findById(cId);
        Contact contact =contactOptional.get();

        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);

        if (user.getId()==contact.getUser().getId()) {
            model.addAttribute("contact", contact);
        }

        model.addAttribute("title","Show Contact Detail");
        return "normal/contact_detail";
    }

    //delete contact handler
    @GetMapping("/delete/{cId}")
    public String deleteContact(@PathVariable("cId") int cId ,Model model,RedirectAttributes redirectAttributes){


       Optional<Contact> contactOptional =  this.contactRepository.findById(cId);
       Contact contact = contactOptional.get();


        contact.setUser(null);

        //remove img

       this.contactRepository.delete(contact);

       redirectAttributes.addFlashAttribute("message",new Message("Contact Deleted Successfully..", "success"));


       return "redirect:/user/show-contacts/0";

    }
}



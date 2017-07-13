package com.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController
{
    public List<UserDetails> userDetailsList = new ArrayList<UserDetails>();
    
    public UserController()
    {
        userDetailsList.add(new UserDetails("User1", "Mechanical"));
        userDetailsList.add(new UserDetails("User2", "Electrical"));
    }
    @RequestMapping(value="/userdetails",method=RequestMethod.GET,produces="application/json")
    public List<UserDetails> GetUserdetails()
    {
        return userDetailsList;
    }
    
    @RequestMapping(value="/user",consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ResponseEntity<?> ProcessUser(@RequestBody UserDetails userDetails)
    {
        boolean nameExist = false;
        
        for(UserDetails ud : userDetailsList)
        {
            if(ud.getName().equals(userDetails.getName()))
            {
                nameExist = true;
                ud.setDepartment(userDetails.getDepartment());
                break;
            }
        }
        if(!nameExist)
        {
            userDetailsList.add(userDetails);
        }
        
        return new ResponseEntity<Object>(HttpStatus.OK);
    }
    
    @RequestMapping(value="/deleteuser",consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.DELETE)
    public ResponseEntity<?> DeleteUser(@RequestBody UserDetails userDetails)
    {
        Iterator<UserDetails> it = userDetailsList.iterator();
        while(it.hasNext())
        {
            UserDetails ud = (UserDetails) it.next();
            if(ud.getName().equals(userDetails.getName()))
                it.remove();
        }
        return new ResponseEntity<Object>(HttpStatus.OK);
    }
    
    private List<User> users = Arrays.asList(
    		  new User("ana@yahoo.com", "pass", "Ana", 20),
    		  new User("bob@yahoo.com", "pass", "Bob", 30),
    		  new User("john@yahoo.com", "pass", "John", 40),
    		  new User("mary@yahoo.com", "pass", "Mary", 30));
    
    @GetMapping(value = "/users")
    @ResponseBody
    public List<User> getUsers() {
        return users;
    }
    
    @GetMapping("/userPage")
    public String getUserProfilePage() {
        return "user";
    }
    
    @PostMapping(value = "/user")
	@ResponseBody
	public ResponseEntity<Object> saveUser(@Valid User user, 
	  BindingResult result, Model model) {
	    if (result.hasErrors()) {
	        List<Object> errors = result.getAllErrors().stream()
	          .map(DefaultMessageSourceResolvable::getDefaultMessage)
	          .collect(Collectors.toList());
	        return new ResponseEntity<>(errors, HttpStatus.OK);
	    } else {
	        if (users.stream().anyMatch(it -> user.getEmail().equals(it.getEmail()))) {
	            return new ResponseEntity<>(
	              Collections.singletonList("Email already exists!"), 
	              HttpStatus.CONFLICT);
	        } else {
	            users.add(user);
	            return new ResponseEntity<>(HttpStatus.CREATED);
	        }
	    }
	}
}
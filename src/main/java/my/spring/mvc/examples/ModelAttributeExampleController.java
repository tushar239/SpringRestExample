package my.spring.mvc.examples;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Examples of @ModelAttribute
// http://www.javabeat.net/modelattribute-spring-mvc/
// http://fruzenshtein.com/spring-mvc-form-handling/
// http://article-stack.com/education/spring-3-mvc-form-handling-covering-all-aspects-theory-apart.amty

// Example of @SessionAttributes
// http://www.roseindia.net/tutorial/spring/session-attributes-annotation.html

// Example of Form Processing
// 
@Controller
public class ModelAttributeExampleController {
    @Autowired
    private UserDetails userDetails;
    
    //This method is invoked before the above method
    // Method annotted with @ModelAttribute is executed before calling any method annotted with @RequestMapping
    // It simply creates one command object (here userDetails) and put it into ModelMap as key="userDetails" and value=userDetails instance.
    // This command object (userDetails) can be accessed directly in resulting jsp (here examplOutput.jsp)
    // Normally, @ModelAttribute is used to keep the model ready with database retrieved data.

    // you can use Model also instead of ModelAttribute. See WelcomeController.java
    @ModelAttribute("userDetails")
    public UserDetails getAccount(@RequestParam String user, @RequestParam String emailId){
        System.out.println("User Value from Request Parameter : " + user);
        userDetails.setUserName(user);
        userDetails.setEmailId(emailId);
        return userDetails;
    }
    
    //http://localhost:8080/SpringRestExample/modelattributeexample?user=Tushar&emailId=tushar@adp.com
    // Model or ModelMap or ModelAttribute objects are eventually command objects kept in the request scope.
    // It looks like you can have multiple command objects in the request.
    @RequestMapping(value="/modelattributeexample")
    public String getMethod(@ModelAttribute("userDetails") UserDetails userDetails, ModelMap modelMap, Model model/*, BindingResult result*/){
        System.out.println("UserDetails's User Name : " + userDetails.getUserName());
        System.out.println("UserDetails's Email Id : " + userDetails.getEmailId());
        
        //modelMap.addAttribute("name", userDetails.getUserName());
        //modelMap.addAttribute("email", userDetails.getEmailId());
        
        //model.addAttribute("name1", userDetails.getUserName());
        //model.addAttribute("email1", userDetails.getEmailId());
        
        System.out.println("ModelMap's username:"+((UserDetails)modelMap.get("userDetails")).getUserName());
        System.out.println("ModelMap's emai id:"+((UserDetails)modelMap.get("userDetails")).getEmailId());
        
        System.out.println("Model's username:"+((UserDetails)model.asMap().get("userDetails")).getUserName());
        System.out.println("Model's emai id:"+((UserDetails)model.asMap().get("userDetails")).getEmailId());


        return "exampleOutput";
    }
    

}
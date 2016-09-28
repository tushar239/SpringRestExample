package my.spring.mvc.examples;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus.Series;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// http://www.mkyong.com/spring3/spring-3-mvc-hello-world-example/

@Controller
@RequestMapping("/welcome")
public class WelcomeController {
    // http://localhost:8080/SpringRestExample/welcome/printwelcome
    @RequestMapping(value="printwelcome", method = RequestMethod.GET)
    public String printWelcome(ModelMap modelMap) {
        // message variable can be accessed inside hello.jsp
        modelMap.addAttribute("message", "Spring 3 MVC Hello World");
        // InternalResourceViewResolver is added in spring context file to consider hello as hello.jsp
        return "hello";
 
    }
    
    // Example of ModelAndView
    // http://localhost:8080/SpringRestExample/welcome/modelandviewexample
    @RequestMapping(value="modelandviewexample", method = RequestMethod.GET)
    public ModelAndView modelAndViewExample() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("exampleOutput");
        
        UserDetails userDetails = new UserDetails();
        userDetails.setUserName("my name");
        userDetails.setEmailId("my@email.com");

        modelAndView.addObject("userDetails", userDetails);
        return modelAndView;
    }
    
    // http://localhost:8080/SpringRestExample/welcome/modelexample
    // you can use ModelAttribute also instead of Model. See ModelAttibuteExampleController.java
    // Model or ModelMap or ModelAttribute objects are eventually command objects kept in the request scope.
    @RequestMapping(value="/modelexample")
    public String getAccountAsModel(Model model) {
        UserDetails userDetails = new UserDetails();
        userDetails.setUserName("my name");
        userDetails.setEmailId("my@email.com");
        model.addAttribute("userDetails", userDetails);
        
        return "exampleOutput";
    }
    //http://localhost:8080/SpringRestExample/welcome/autopopulationofrequestparams?userName=Tushar&emailId=tushar@adp.com
    // request parameter userName will automatically be bound to UserDetails's userName field
    // request parameter emailId will automatically be bound to UserDetails's emailId field
    // I tried using ModelMap instead of @ModelAttribute, but it doesn't work
    @RequestMapping(value="/autopopulationofrequestparams")
    public String getAutoPopulationOfRequestParams(@ModelAttribute UserDetails userDetails/*, ModelMap model*/){
        System.out.println("User Name : " + userDetails.getUserName());
        System.out.println("Email Id : " + userDetails.getEmailId());
        /*
        System.out.println(model.get("userName"));
        System.out.println(model.get("emailId"));
        */
        return "hello";
        
    }
    
    // http://localhost:8080/SpringRestExample/welcome/loadhomejsp
    // This proves that userDetails works as a command object set in the request scope.
    // home.jsp accesses userDetails. Changed values are set in the same userDetails command object 
    // and the same userDetails command object can be retrieved in exampleOutput.jsp
    @RequestMapping(value="/loadhomejsp")
    public ModelAndView getHomeJsp(ModelMap model) {
        UserDetails userDetails = new UserDetails();
        userDetails.setUserName("Tushar");
        userDetails.setEmailId("abc@email.com");
        //return new ModelAndView("home", "command", userDetails);
        // or
        model.put("command", userDetails);
        return new ModelAndView("home");
    }
    
    @RequestMapping(value="/oneFormToAnother")
    public String oneFormToAnother(@ModelAttribute UserDetails userDetails) {
        System.out.println("coming inside oneFormToAnother");
        System.out.println("name:"+userDetails.getUserName());
        System.out.println("email:"+userDetails.getEmailId());
        return "exampleOutput";
    }
    
    // Try below url with method=delete in chrome's postman rest client plugin 
    // http://localhost:8080/SpringRestExample/welcome/testdeleteseries/1
    // O/P: status = 204 No Content
    @RequestMapping(value="/testdeleteseries/{seriesId}", method=RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSeries(@PathVariable("seriesId") long id) {
        //service.deleteSeries(id);
        System.out.println("deleted");
    }
    
    @RequestMapping(value="/testinsertseries", method=RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void insertSeries(@RequestBody Series series, HttpServletRequest request, HttpServletResponse response) {
        //.service.insertSeries(series);
        // normally after inserting a record, you return a url how client can retrieve it back.
        //response.setHeader("Location", request.getRequestURL().append("/").append(series.getId()).toString());
        System.out.println("new series created");
    }
    /*
    @RequestMapping(value="testgetseries/{seriesId}", method=RequestMethod.GET)
    public ResponseEntity<Series> getSeries(@PathVariable("seriesId") long id) {
        //Series series = service.getSeries(id);
        
        //if (series == null) {
           // return new ResponseEntity<Series>(HttpStatus.NOT_FOUND);
        //}
        
        //return new ResponseEntity<Series>(series, HttpStatus.OK);
    }
    */
}

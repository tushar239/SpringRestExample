package com.journaldev.spring.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.journaldev.spring.model.Employee;
import com.journaldev.spring.model.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This example is taken from
 * http://www.journaldev.com/2552/spring-restful-web-service-example-with-json-jackson-and-client-program
 * 
 * You can run TestSpringRestExample.java that uses RestTemplate to build URLs.
 * Alternatively, you can use Rest Client Tool (Postman - REST Client) available in chrome.
 */
@Controller
public class JasonTestController {
	
	private static final Logger logger = LoggerFactory.getLogger(JasonTestController.class);
	
	//Map to store employees, ideally we should use database
	Map<Integer, Employee> empData = new HashMap<Integer, Employee>();

	@RequestMapping(value = EmpRestURIConstants.HEALTH, method = RequestMethod.GET)
	public String health() {
		return "GOOD";
	}
	/*
    To test this method, use below information in Rest Client
    URL: http://localhost:8080/SpringRestExample/rest/emp/dummy
    Method: GET
    
    O/P:
        {
            "id": 9999,
            "name": "Dummy",
            "createdDate": 1396634631619
        }
	 */
	@RequestMapping(value = EmpRestURIConstants.DUMMY_EMP, method = RequestMethod.GET, 
	        produces = "application/json")
	public @ResponseBody Employee getDummyEmployee() {
		logger.info("Start getDummyEmployee");
		Employee emp = new Employee();
		emp.setId(9999);
		emp.setName("Dummy");
		emp.setCreatedDate(new Date());
		empData.put(9999, emp);
		return emp;
	}

	@RequestMapping(value = EmpRestURIConstants.GET_EMP_PARTIAL, method = RequestMethod.GET,
			produces = "application/json")
	@JsonView(View.Summary.class)
	public @ResponseBody Employee getPartialEmployee() {
		logger.info("Start getPartialEmployee");
		Employee emp = new Employee();
		emp.setId(9999);
		emp.setName("Dummy");
		emp.setCreatedDate(new Date());
		empData.put(9999, emp);
		return emp;
	}

	
	@RequestMapping(value = EmpRestURIConstants.GET_EMP, method = RequestMethod.GET)
	public @ResponseBody Employee getEmployee(@PathVariable("id") int empId) {
		logger.info("Start getEmployee. ID=" + empId);
		
		return empData.get(empId);
	}
	
	@RequestMapping(value = EmpRestURIConstants.GET_ALL_EMP, method = RequestMethod.GET)
	public @ResponseBody List<Employee> getAllEmployees() {
		logger.info("Start getAllEmployees.");
		List<Employee> emps = new ArrayList<Employee>();
		Set<Integer> empIdKeys = empData.keySet();
		for(Integer i : empIdKeys){
			emps.add(empData.get(i));
		}
		return emps;
	}
	
	/*
	    To test this method, use below information in Rest Client
	    URL: http://localhost:8080/SpringRestExample/rest/emp/create
	    Headers:
	    Content-Type=application/json
	    Method: POST
	    Raw Data to be sent={"id":9999, "name":"Dummy","createdDate":1393835422483}
	    
	    O/P:
	    {
            "id": 9999,
            "name": "Dummy",
            "createdDate": 1396634631619
        }
        
        You can also test it through Client.java's testJsonTestController() method. Internally it will serialize/deserialize request emp and response emp objects
        into Json. You can also choose other libraries like XStream, JaxB etc. Read readme.txt.
        Look at JaxBTestController to see how does it work with JaxB. 
	*/
	@RequestMapping(value = EmpRestURIConstants.CREATE_EMP, method = RequestMethod.POST)
	public @ResponseBody Employee createEmployee(@RequestBody Employee emp) {
		logger.info("Start createEmployee.");
		emp.setCreatedDate(new Date());
		empData.put(emp.getId(), emp);
		return emp;
	}

	// StringHttpMessageConverter's canRead(targetClass, contentType) returns true because targetClass=String.class and contentType=text/plain
	// So, it will use StringHttpMessageConverter's read(...) method to convert incoming InputMessage.getBody() to String.

	// Request's Accept=applicaion/json, which is supported by MappingJackson2HttpMessageConverter. So, it will use it's write(...) method to convert Employee to json.

	@RequestMapping(value = EmpRestURIConstants.CREATE_EMP_FROM_STRING, method = RequestMethod.POST)
	public @ResponseBody Employee createEmployeeFromString(@RequestBody String empStr) throws Exception {
		logger.info("Start createEmployeeFromString.");
		ObjectMapper mapper = new ObjectMapper();
		Employee emp = mapper.readValue(empStr, Employee.class);
		emp.setCreatedDate(new Date());
		empData.put(emp.getId(), emp);
		return emp;
	}


	// StringHttpMessageConverter supports all media types and String.class as target class.
	// StringHttpMessageConverter's canRead(targetClass, contentType) returns true because targetClass=String.class and contentType=application/json
	// So, it will use StringHttpMessageConverter's read(...) method to convert incoming InputMessage.getBody() to String.

	// consumes value has to match with request's Content-Type header value and produces value has to match with request's Accept header value
	// It means here consumes=application/json has no effect here , which is dangerous because client can send any string (not a json) with Content-Type=application/json
	// and this method will be executed and then it will fail because ObectMapper won't be able to convert non-json to Employee.

	@RequestMapping(value = EmpRestURIConstants.CREATE_EMP_CONSUMING_JSON, method = RequestMethod.POST)
	public @ResponseBody Employee createEmployeeConsumingJson(@RequestBody String empStr) throws Exception {
		logger.info("Start createEmployeeConsumingJson.");
		ObjectMapper mapper = new ObjectMapper();
		Employee emp = mapper.readValue(empStr, Employee.class);
		emp.setCreatedDate(new Date());
		empData.put(emp.getId(), emp);
		return emp;
	}

	@Async
	@RequestMapping(value = EmpRestURIConstants.CREATE_EMP_ASYNCHRONOUSLY, method = RequestMethod.POST)
	public @ResponseBody Future<Employee> createEmployeeAsynchrounously(@RequestBody Employee emp) throws InterruptedException {
		logger.info("Start createEmployeeAsynchrounously.");
		emp.setCreatedDate(new Date());
		empData.put(emp.getId(), emp);
		//Thread.sleep(3000L);
		return new AsyncResult<Employee>(emp);
	}

	// http://www.javacodegeeks.com/2013/03/deferredresult-asynchronous-processing-in-spring-mvc.html
	// Here you are trying to span a different thread that does some job and updates DeferredResult with the result.
	// Even though, you are returning DeferredResult before another thread completes its job and populates the result in DeferredResult, spring will still keep http connection open with client and will not return the end result till another thread completes its job.
	// So, for client it is still synchronous process.
	// Better idea to do so is use AsyncRestTemplate at client side.
	// DeferredResult is just one wrapper class having callback methods and actual results. You can implement your own Wrapper to which another thread can update the result if you dont want all callback facilities provied by DeferredResult

	// Below link explains how DeferredResult is handled by Servlet container. ????????????
	// https://spring.io/blog/2012/05/07/spring-mvc-3-2-preview-introducing-servlet-3-async-support
	// After returning DeferredResult from controller, it calls
	// DeferredResultMethodReturnValueHandler -> handleReturnValue
	//	WebAsyncUtils.getAsyncManager(webRequest).startDeferredResultProcessing(deferredResult, mavContainer);
	//		WebAsyncManager-> this.asyncWebRequest.startAsync();
	// I am not sure how it is handled further??????????
	// But client is still waiting for the response.


	@RequestMapping(value = EmpRestURIConstants.GET_QUOTE_WITH_DEFFERED_RESULT, method = RequestMethod.GET)
	public @ResponseBody DeferredResult<String> quotesWithDifferedResult() {
		DeferredResult<String> deferredResult = new DeferredResult<String>();
		/*deferredResult.setResultHandler(new DeferredResult.DeferredResultHandler() {
			@Override
			public void handleResult(Object result) {
				System.out.println("result in DeferredResult is set as :" + result.toString());
			}
		});*/


		//deferredResult.onCompletion(some Runnable);
		//deferredResult.onTimeout(some Runnable);
		// Add deferredResult to a Queue
		Thread thread = new MyThread(deferredResult);
		thread.start();
		return deferredResult;
	}

	public class MyThread extends Thread {
		private DeferredResult<String> deferredResult;
		public MyThread(DeferredResult<String> deferredResult) {
			this.deferredResult = deferredResult;
		}

		public void run(){
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			deferredResult.setResult("some result");
		}
	}

	// https://spring.io/blog/2012/05/07/spring-mvc-3-2-preview-introducing-servlet-3-async-support
	// http://stackoverflow.com/questions/141284/the-difference-between-the-runnable-and-callable-interfaces-in-java

	// This method returns a Future, but spring actually converts it into Map containing two parameters "cancelled:true/false, done:true/false"
	// So, spring won't return Future object with body (result) in it to the client. So, if you intend to return a Future with result, then it won't be possible.
	// Better idea to do so is use AsyncRestTemplate at client side.
	@RequestMapping(value = EmpRestURIConstants.GET_QUOTE_WITH_FUTURE_AND_CALLABLE, method = RequestMethod.GET)
	public @ResponseBody
	Future<String> quotes() {
		ExecutorService executor = Executors.newFixedThreadPool(10);

		// Runnable Vs Callable
		// http://stackoverflow.com/questions/141284/the-difference-between-the-runnable-and-callable-interfaces-in-java
		// Callable is just like Runnable, but it's call() can return the result whereas run() doesn't return the result.
		// If you don't wrap callable with Future, Callable remains synchronous call because you need to wait for the result to come back.
		Callable<String> callable = new Callable<String>() {
			public String call() throws Exception {
				// ...
				Thread.sleep(10000);
				System.out.println("returning from callable....");
				return "some result";
			}
		};
		Future<String> future = executor.submit(callable);
		while(!future.isDone()) {
			System.out.println("waiting for callable to completed");
		}
		System.out.println("returning from quotes()...");
		return future;

	}
	@RequestMapping(value = EmpRestURIConstants.DELETE_EMP, method = RequestMethod.PUT)
	public @ResponseBody Employee deleteEmployee(@PathVariable("id") int empId) {
		logger.info("Start deleteEmployee.");
		Employee emp = empData.get(empId);
		empData.remove(empId);
		return emp;
	}

	/*
	 To test this method using Rest client
	 URL: http://localhost:8080/SpringRestExample/rest/helloworld
	 Method: POST
	 Row Data to be sent : Tushar
	 O/P:
	 Heelo World, Tushar
	 */
	@RequestMapping(value = "rest/helloworld", method = RequestMethod.POST)
	public @ResponseBody String getHelloWorld(@RequestBody String name) {
	    logger.info(name);
        logger.info("Start getHelloWorld.");
        return "Hello World, "+name;
    }

    // Spring MVC Exception tutorial: http://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc
    /*
        To test response status 200
        http://localhost:8080/SpringRestExample/rest/testexception/1

        To test response status 404
        http://localhost:8080/SpringRestExample/rest/testexception/2

        Test it in rest client tool, there you can easily see the response status instead of browser.
    */
    @RequestMapping(value = "rest/testexception/{orderid}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK) // If there is no exception, then response status 200 will be sent
    public @ResponseBody String testException(@PathVariable("orderid") int orderid) {
        if(orderid > 1) {
            // If there is an exception, then response status defined on Exception class will be sent
            // If some other exception is thrown, then it will display stack trace in the browser
            throw new OrderNotFoundException();
        }
        return "success";
    }

    @ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No such Order") // 404
    public class OrderNotFoundException extends RuntimeException {
        // ...
    }
}

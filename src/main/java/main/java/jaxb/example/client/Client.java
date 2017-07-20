package jaxb.example.client;

import com.journaldev.spring.controller.EmpRestURIConstants;
import com.journaldev.spring.model.Employee;
import jaxb.example.models.Lead;
import jaxb.example.models.Product;
import my.spring.mvc.examples.UserDetails;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


public class Client {


public static final String SERVER_URI = "http://localhost:8080/SpringRestExample";
    
    public static void main(String args[]){
        
        /*
        System.out.println("*****");
        testGetProduct();
        */

        System.out.println("*****");
        testJsonTestController();

        /*
        System.out.println("*****");
        testRequestResponseEntityExampleController();

        System.out.println("*****");
        testXmlInputOutput();
        */
    }

    private static void testGetProduct() {
        RestTemplate restTemplate = new RestTemplate();
        Product product = restTemplate.getForObject(SERVER_URI+"/products/1", Product.class);
        System.out.println(product.getName());
    }
    
    private static void testJsonTestController() {
        RestTemplate restTemplate = new RestTemplate();
        final Employee emp = new Employee();
        emp.setId(1);
        emp.setName("Tushar");
        /*
        {
            Employee retunedEmp = restTemplate.postForObject(SERVER_URI+EmpRestURIConstants.CREATE_EMP_FROM_STRING, emp, Employee.class);
            System.out.println(retunedEmp.getName());// O/P json is converted into Employee object using message converter. 
        }
        */

        /*{
            // Making asynchronous client calls using AsyncRestTemplate
            // http://javattitude.com/2014/04/20/using-spring-4-asyncresttemplate/

            // AsyncRestTemplate is not really async. On client side, it creates a new thread returns a ListenableFuture (same as Java 8's CompletableFuture).
            // So that main (client) thread can continue working ahead.
            // As soon as that new thread receives a response from Controller, it populates ListenableFuture and calls registered callbacks onSuccess/onFailure.

            // You can supply your own Excecutor with thread pool. If not provided, it may just blindly create a new Thread without any thread pool.

            AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();
            HttpEntity<Employee> requestEntity = new HttpEntity<Employee>(emp);
            ListenableFuture<ResponseEntity<Employee>> futureEntity = asyncRestTemplate.postForEntity(SERVER_URI + EmpRestURIConstants.CREATE_EMP, requestEntity, Employee.class);

            futureEntity
                    .addCallback(new ListenableFutureCallback<ResponseEntity<Employee>>() {
                        @Override
                        public void onSuccess(ResponseEntity<Employee> result) {
                            System.out.println("response available...");
                            Employee returnedEmployee = result.getBody();
                            System.out.println(returnedEmployee.getName());
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            t.printStackTrace();
                        }
                    });
            int i=0;
            while(i<1000) {
                System.out.println("continue doing other operations...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i++;
            }
        }*/

        {
           {
                HttpHeaders headers = new HttpHeaders();
                HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

                ResponseEntity<String> responseEntity = restTemplate.exchange(SERVER_URI + EmpRestURIConstants.GET_QUOTE_WITH_DEFFERED_RESULT, HttpMethod.GET, requestEntity, String.class);
                System.out.println("Trying Endpoint returning DeferredResult:"+responseEntity.getBody()); // O/P: some result
            }

            /*{
                String future = restTemplate.getForObject(SERVER_URI + EmpRestURIConstants.GET_QUOTE_WITH_FUTURE_AND_CALLABLE, String.class);
                //Map map = restTemplate.getForObject(SERVER_URI + EmpRestURIConstants.GET_QUOTE_WITH_FUTURE_AND_CALLABLE, Map.class);

                try {
                    System.out.println("Trying Endpoint returning Future:"+future); // O/P: {cancelled=false, done=true}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }*/

        }
        /*
        {
            // Here, we haven't specified Content-Type
            // Spring sees type of request entity. It is String. StringHttpMessageConverter's canWrite(Class<?> clazz, MediaType mediaType) will return true because it supports all media types and input class as String.class.
            // As supplied Content-Type=null, it will set StringHttpMessageConverter's supported media types (text/plain) as request's Content-Type
            // and its write(...) method to convert String to OutputStream
            // So, even though you don't specify Content-Type, spring will always set some Content-Type based on which MessageConverter supports the RequestEntity class type.

            // Similarly, we haven't specified Accept header value, but expected ResponseEntity type is set to Employee.class, which is supported by MappingJackson2HttpMessageConverter' canRead(Class<?> clazz, MediaType mediaType), so Accept's header value will be set to application/json
            // and it's read(...) method will be used to convert json response to Employee.class
            try {
                ObjectMapper mapper = new ObjectMapper();
                Employee retunedEmp = restTemplate.postForObject(SERVER_URI+ EmpRestURIConstants.CREATE_EMP_FROM_STRING, mapper.writeValueAsString(emp), Employee.class);
                System.out.println(retunedEmp.getName());// O/P json is converted into Employee object using message converter.
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        */

        /*{

            try {
                // Here, we have specified Content-Type as application/json
                // Spring sees type of request entity. It is String. StringHttpMessageConverter's canWrite(Class<?> clazz, MediaType mediaType) will return true because it supports all media types and input class as String.class.
                // StringHttpMessageConverter's write(...) method will be used to convert string input to OutputStream.

                // We haven't specified Accept header value, but expected ResponseEntity type is set to Employee.class, which is supported by MappingJackson2HttpMessageConverter' canRead(Class<?> clazz, MediaType mediaType),
                // so request's Accept header value will be set to application/json (media type supported by MappingJackson2HttpMessageConverter)
                // and MappingJackson2HttpMessageConverter's read(...) method will be used to convert json response to Employee.class
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> requestEntity = new HttpEntity<String>(new ObjectMapper().writeValueAsString(emp), headers);
                //HttpEntity<String> requestEntity = new HttpEntity<String>("not a json string", headers);

                // RequestEntity type is Employee, so StringHttpMessageConverter can't be used because it supports only String.class.
                // MappingJackson2HttpMessageConverter will support Employee class, so it will be used to generate OutputStream from Employee class.
                //HttpEntity<Employee> requestEntity = new HttpEntity<Employee>(emp, headers);

                ResponseEntity<Employee> response = restTemplate.exchange(SERVER_URI+ EmpRestURIConstants.CREATE_EMP_CONSUMING_JSON,HttpMethod.POST, requestEntity, Employee.class);

                System.out.println(response.getBody().getName());// O/P json is converted into Employee object using message converter.
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        {
            // EmployeeCopy has json annotations. So, spring know how to map output json to EmployeeCopy
            EmployeeCopy retunedEmp = restTemplate.postForObject(SERVER_URI+EmpRestURIConstants.CREATE_EMP_FROM_STRING, emp, EmployeeCopy.class);
            System.out.println(retunedEmp.getFirstName());// O/P json is converted into Employee object using message converter. 
        }
        {
            // Here we are not expecting to convert output json into any object.
            String retunedEmp = restTemplate.postForObject(SERVER_URI+EmpRestURIConstants.CREATE_EMP_FROM_STRING, emp, String.class);
            System.out.println(retunedEmp); // O/P is json : {"id":1,"name":"Tushar","createdDate":1413417012999}
        }*/
        
    }
    
    //http://docs.spring.io/spring/docs/3.2.8.RELEASE/javadoc-api/org/springframework/http/ResponseEntity.html
    private static void testRequestResponseEntityExampleController() {
        
        RestTemplate restTemplate = new RestTemplate();
        {
            ResponseEntity<String> entity = restTemplate.getForEntity(SERVER_URI+"/entity/returnstring", String.class);
            MediaType contentType = entity.getHeaders().getContentType();
            HttpStatus statusCode = entity.getStatusCode();
            String body = entity.getBody();
            System.out.println("Returned String:"+body);
        }
        {
            HttpHeaders headers = new HttpHeaders();
            //headers.setContentType(MediaType.APPLICATION_JSON);
            //headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
            
            UserDetails userDetails = new UserDetails();
            userDetails.setUserName("Tushar");
            HttpEntity<UserDetails> requestEntity = new HttpEntity<UserDetails>(userDetails, headers);
            
            ResponseEntity<UserDetails> responseEntity = restTemplate.exchange(SERVER_URI+"/entity/handleobject", HttpMethod.POST, requestEntity, UserDetails.class);
            System.out.println("Content Type from Header:"+responseEntity.getHeaders().getContentType()); // O/P: application/json;charset=UTF-8
            //System.out.println("Accept from Header:"+responseEntity.getHeaders().getAccept());
            System.out.println("has body?:"+responseEntity.hasBody());
            UserDetails returnedUserDetails = responseEntity.getBody();
            System.out.println(returnedUserDetails.getUserName()+":"+returnedUserDetails.getEmailId());
        
        }
        {
            HttpHeaders headers = new HttpHeaders();
            //headers.setContentType(MediaType.APPLICATION_JSON);
            //headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
            
            HttpEntity<String> requestEntity = new HttpEntity<String>("Hi, welcome", headers);
            
            ResponseEntity<String> responseEntity = restTemplate.exchange(SERVER_URI+"/entity/handlestring", HttpMethod.POST, requestEntity, String.class);
            System.out.println("has body?:"+responseEntity.hasBody());
            String returnedString = responseEntity.getBody();
            System.out.println("returnedString:"+returnedString);
        
        }
    }
    private static void testXmlInputOutput() {
        RestTemplate restTemplate = new RestTemplate();
        {
            HttpHeaders headers = new HttpHeaders();
            // It is mandatory to set ContentType=application/xml, if you expect REST service to consider an input as an XML and not as a TEXT.
            // If you set ContentType, then REST service knows that it is an XML and using JAXB or any other Marshalling/Unmarshalling api, it can convert XML to an object.
            headers.setContentType(MediaType.APPLICATION_XML);
            
            HttpEntity<String> requestEntity = new HttpEntity<String>("<lead><name>kialead</name></lead>", headers);
                   
            ResponseEntity<String> responseEntity = restTemplate.exchange(SERVER_URI+"/xmlaccepting/xmlconsumetest", HttpMethod.POST, requestEntity, String.class);
            String returnedString = responseEntity.getBody();
            System.out.println("returnedString:"+returnedString);
        } 
        {
            
            // Lead class has JaxB annotations, so spring automatically knows that jaxb message converter has to be used to convert output text to an object.
            Lead lead = new Lead();
            lead.setName("test");
            String xml = restTemplate.postForObject(SERVER_URI+"/xmlaccepting/xmlproducetest", lead, String.class);
            System.out.println("lead in xml form:"+xml);
            //O/P: <?xml version="1.0" encoding="UTF-8" standalone="yes"?><lead><name>test</name></lead>
        }
    }
}

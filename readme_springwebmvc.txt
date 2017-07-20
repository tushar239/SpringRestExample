Read MVC lifecycle
	in "Sprint in Action, 4th Edition.pdf" (Chapter 5)

	1) DispatcherServlet -
	The first stop in the request’s travels is at Spring’s DispatcherServlet.
	In the case of Spring MVC, DispatcherServlet is the front controller.

	2) Handler Mappings -
	Typical application may have several controllers, and DispatcherServlet needs some help deciding which controller to send the request to. So the DispatcherServlet consults one or more handler mappings to figure out where the request’s next stop will be.

	3) Controller-
	Once an appropriate controller has been chosen, DispatcherServlet sends the request on its merry way to the chosen controller (Actually, a well-designed controller per- forms little or no processing itself and instead delegates responsibility for the business logic to one or more service objects.)
	The logic performed by a controller often results in some information that needs to be carried back to the user and displayed in the browser. This information is referred to as the model.

	4) Model and Logical View name returned by Controller to DispatcherServlet -
	One of the last things a controller does is package up the model data and identify the name of a view that should render the output. It then sends the request, along with the model and view name, back to the DispatcherServlet

	5) View Resolver -
	The DispatcherServlet consults a view resolver to map the logical view name to a specific view implementation, which may or may not be a JSP.

	6) View Implementation -
	Its final stop is at the view implementation
	The request’s job is finally done. The view will use the model data to render output that will be carried back to the client by the response object.


These HTTP methods are often mapped to CRUD verbs as follows:
	Create—POST
	Read—GET
	Update—PUTorPATCH
	Delete—DELETE

	http://restful-api-design.readthedocs.io/en/latest/methods.html

	HEAD - Returns only headers in the response (no body)
	OPTIONS - Return available HTTP methods and other options look at the “Allow” header that is returned. This header contains a comma-separated list of methods are are supported for the resource or collection.
	PUT vs PATCH - The semantics of this call are like PUT in that it updates a resource, but unlike PUT, it applies a delta rather than replacing the entire resource.

Safe and Idempotent methods

	http://restcookbook.com/HTTP%20Methods/idempotency/

	These are industry standards, not hard and fast rules,
	but you should always follow industry standards
	If you are using POST on your endpoint,
	then it’s an industry standard that it is non-idempotent method.
	If you are using PUT/PATCH, then it is understood by industry that it carries idempotent operation.

Configuring DispatcherServlet

	WebApplicaitonInitializer
		|
	AbstractContextLoaderInitailzer - registers ContextLoaderListener to ServletContext and creates Root WebApplicationContext from provided getRootConfigClasses()
		|
	AbstractDispatcherServletInitializer  - creates WebApplicationContext based on provided getServletConfigClasses() and creates DispatcherServlet by passing WebApplicationContext to. It also registers all Filters.
	If you want to customize DispatcherServlet for some reason, you can override customizeRegistration(), you can apply additional configuration to DispatcherServlet.
		|
	AbstractAnnotationConfigDispatcherServletInitializer
		|
	Your own custom class that can configure dispatcher servlet related mapping, config class etc

	SpringServletContainerInitializer (implements ServletContainerInitializer) looks for all WebApplicationInializers in your
	context and calls them accordingly their order defined using @Order on top of WebApplicationInitializers,
	AnnotationAwareOrderComparator.sort(initializers);

	If you just want to configure DispatcherServlet in you application, you can do it as shown in above code.

	DispatcherServlet’s WebApplicationContext is expected to load beans containing web components such as controllers, view resolvers, and handler mappings (components required for handling spring mvc lifecycle)
	ContextLoaderListener’s WebApplicationContext is expected to load the other beans in your application. These beans are typically the middle-tier and data-tier components that drive the back end of the application.

	It’s important to realize that configuring DispatcherServlet via Abstract- AnnotationConfigDispatcherServletInitializer is an alternative to the traditional web.xml file. It will only work when deploying to a server that supports Servlet 3.0, such as Apache Tomcat 7 or higher.

	import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

	public class SpittrWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	  @Override
	  protected String[] getServletMappings() {
		return new String[] { "/" };
	  }
	  @Override
	  protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] { RootConfig.class };
	  }
	  @Override
	  protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[] { WebConfig.class };
	  }
	}
	It’s important to realize that configuring DispatcherServlet via Abstract- AnnotationConfigDispatcherServletInitializer is an alternative to the tradi- tional web.xml file.
	The only gotcha with configuring DispatcherServlet in this way, as opposed to in a web.xml file, is that it will only work when deploying to a server that supports Servlet 3.0, such as Apache Tomcat 7 or higher.

	ENABLING SPRING MVC
	Just as there are several ways of configuring DispatcherServlet, there’s more than one way to enable Spring MVC components. Historically, Spring has been configured using XML, and there’s an <mvc:annotation-driven> element that you can use to enable annotation-driven Spring MVC.
	The very simplest Spring MVC configuration you can create is a class annotated with @EnableWebMvc:

	@Configuration
	@EnableWebMvc --- Enable Spring MVC
	@ComponentScan("spitter.web")
	public class WebConfig
		   extends WebMvcConfigurerAdapter {
	  @Bean
	  public ViewResolver viewResolver() {
		InternalResourceViewResolver resolver =
				new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/views/");
		resolver.setSuffix(".jsp");
		resolver.setExposeContextBeansAsAttributes(true);
		return resolver;
	  }
	  @Override
	  public void configureDefaultServletHandling(
			DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	  }
	}

	With WebConfig settled, what about RootConfig?

	@Configuration
	@ComponentScan(basePackages={"spitter"},
		excludeFilters={
			@Filter(type=FilterType.ANNOTATION, value=EnableWebMvc.class)
		})
	public class RootConfig {
	}


	What is ViewController?

        you can override addViewControllers method in WebConfig

        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/").setViewName("index");
        }
        It will just say that all requests made with path "/" goes to index.jsp (.jsp is added by ViewResolver)




REST Operations
----------------
http://briansjavablog.blogspot.com/2012/08/rest-services-with-spring.html

GET
http://localhost:8080/rest-sample/rest/funds/12345
Returns fund information to the client for fund Id  specified in URL (in this case 1235)

GET
http://localhost:8080/rest-sample/rest/funds/
Returns fund information to the client for all available funds

POST
http://localhost:8080/rest-sample/rest/funds/
Create a new fund on the sever using the fund data in the HTTP request body. 
The path to the newly created resource is returned in a HTTP header as follows Location: /rest-sample/rest/funds/12345 

PUT
http://localhost:8080/rest-sample/rest/funds/123456
Update existing fund resource with fund data in HTTP request body. 
The updated fund data is returned to the client. 
Returning the updated fund data is not absolutely necessary but can be convenient where the server sets object values like ids or timestamps. 
By returning the updated entity to the client we can guarantee that the client has the exact current state of that entity.

DELETE
http://localhost:8080/rest-sample/rest/funds/123456
Deletes the fund specified by the fund Id in the URL.


Important point is to understand the difference between PUT and POST. 

- Both can be used for Insert or Update. In case of PUT, client tells server the location/id item should be updated. If item doesn't exist then it creates one at same location/id.
If it already exists then it updates one. You can compare update with delete+create.
- In case of POST, client doesn't tell server at which location/id where item should be created/updated. 
If item with similar information exists, then that item is updated and ID of that item is sent back to client as a 'Location' header attribute. 
If item with similar information doesn't exists, then new item with new ID is created and that new ID is sent back to client as a 'Location' header attribute.

----------------------------------------------------------------
http://www.byteslounge.com/tutorials/first-spring-mvc-application-tutorial

@Controller, @RequestMapping, @ModelAndView Example with HelloWorld program
http://viralpatel.net/blogs/spring-3-mvc-create-hello-world-application-spring-3-mvc/

@ModelAttribute example
http://krams915.blogspot.com/2010/12/spring-3-mvc-using-modelattribute-in.html

@RequestMapping, @RequestParam, @PathVariable Examples
http://www.byteslounge.com/tutorials/spring-mvc-requestmapping-example

Consumes, ResponseEntity<String>  Example
http://www.byteslounge.com/tutorials/spring-mvc-requestmapping-consumes-condition-example


REST Example
@RequestBody and @ResponseBody with Json example
http://www.mkyong.com/spring-mvc/spring-3-mvc-and-json-example/

@RequestBody and @ResponseBody works with JSon
http://www.journaldev.com/2552/spring-restful-web-service-example-with-json-jackson-and-client-program

@RequestBody and @ResponseBody with rest service
http://www.oudmaijer.com/2010/01/16/spring-3-0-rest-services-with-spring-mvc/
http://www.baeldung.com/spring-httpmessageconverter-rest

@ResponseEntity
http://docs.spring.io/spring/docs/3.2.8.RELEASE/javadoc-api/org/springframework/http/ResponseEntity.html

Exception Handling
http://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc
http://viralpatel.net/blogs/spring-mvc-exception-handling-controlleradvice-annotation/
http://www.mkyong.com/spring-mvc/spring-mvc-exceptionhandler-example/

JQuery Ajax and Spring MVC REST
http://www.beingjavaguys.com/2013/07/sending-html-form-data-to-spring.html

web.xml
-------
 <servlet>
        <servlet-name>springmvc</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <load-on-startup>2</load-on-startup>
    </servlet>

from the name of DispatcherServlet (springmvc), it identifies spring configuration file name (springmvc-servlet.xml)


if you want to customize the name of xml file, use ContextLoaderListener in web.xml and give customized xml name as contextConfigLocation

<context-param>
   <param-name>contextConfigLocation</param-name>
   <param-value>/WEB-INF/HelloWeb-servlet.xml</param-value>
</context-param>

<listener>
   <listener-class>
      org.springframework.web.context.ContextLoaderListener
   </listener-class>
</listener>

Controller
----------

@Controller
@RequestMapping("/seo_local_listing.mvc")
public class SEOLocalListingsController {
...
}



ViewResolvers
-------------
http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/mvc.html#mvc-viewresolver-resolver
http://book.javanb.com/Professional-Java-Development-with-the-Spring-Framework/BBL0102.html

1) UrlBasedViewResolver
<bean id="viewResolver"
      class="org.springframework.web.servlet.view.UrlBasedViewResolver">
    <property name="viewClass" value="org.springframework.web.servlet.view.JstlView"/>
    <property name="prefix" value="/WEB-INF/jsp/"/>
    <property name="suffix" value=".jsp"/>
</bean>

UrlBasedViewResolver attaches prefix and suffix to the view name that you set in the controller method and gives that url to viewClass (here JstlView) to resolve the view.
JstlView extends InternalResourceView. It is a specialization of InternalResourceView for JSTL pages, i.e. JSP pages that use the JSP Standard Tag Library.

When using JSP for a view technology you can use the UrlBasedViewResolver. This view resolver translates a view name to a URL and hands the request over to the RequestDispatcher to render the view.
When returning test as a viewname, this view resolver will hand the request over to the RequestDispatcher that will send the request to /WEB-INF/jsp/test.jsp.

The UrlBasedViewResolver comes in two shapes: InternalResourceViewResolver and the VelocityViewResolver, which have only minor differences between them. 
The InternalResourceViewResolver is a convenient resolver, already setting a specific view class (InternalResourceView), but still allowing you to override it. 
The VelocityViewResolver applies some Velocity-specific properties to all views it creates, also allowing you to override those. They include the NumberTool and DateTool objects, which allow for easy formatting of numbers and dates.

2) BeanNameViewResolver

<bean id="welcome" class="org.springframework.web.servlet.view.JstlView">
  <property name="url"><value>/WEB-INF/jsps/welcome.jsp</value></property>
</bean>
   
<bean class="org.springframework.web.servlet.view.BeanNameViewResolver"/>

If you set 'welcome' as a view name in controller's method, BeanNameViewResolver will search for a bean name 'welcome'. welcome should be the bean name of 
a View class that helps to resolve the view. Here it is JstlView(which resolves the view to jstl based jsps). 
BeanNameViewResolver searches for the View bean names in the same BeanFactory(xml) file only.

3) XmlViewResolver

You are just telling spring to look for View class beans in a separate xml (spring xml file). Here it is views.xml.
This just helps to override the limitation of BeanNameViewResolver, which requires the View class bean in the same xml file.

<bean class="org.springframework.web.servlet.view.XmlViewResolver">
  <property name="location"><value>/WEB-INF/views/views.xml</value></property>
</bean>

4) ResourceBundleViewResolver

read more about it on 
http://book.javanb.com/Professional-Java-Development-with-the-Spring-Framework/BBL0102.html

5) Chaining the view resolvers
 
read more about it on 
http://book.javanb.com/Professional-Java-Development-with-the-Spring-Framework/BBL0102.html


ViewResolvers
-------------
You can define multiple View Resolvers in your application context.

    @Bean
    public VelocityConfigurer velocityConfig() {
        final VelocityConfigurer configurer = new VelocityConfigurer();
        configurer.setResourceLoaderPath("/WEB-INF/views/");
        return configurer;
    }

    @Bean
    public VelocityViewResolver viewResolver() {
        final VelocityViewResolver resolver = new VelocityViewResolver();
        resolver.setPrefix("");
        resolver.setSuffix(".vm");
        return resolver;
    }

    @Bean
    public InternalResourceViewResolver internalResourceViewResolver() {
        final InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

ContentNegotiatingViewResolver will iterate through all these view resolvers to figure out whether a view is available from any one of these View Resolvers.
It is also one type of ViewResolver.

see its "resolveViewName(String viewName, Locale locale)" method.
Based on passed MediaType also, it can add the suffix to passed view name and resolve it using proper ViewResolver.
e.g. If MediaType=text/html and viewName="something", then it knows that it needs resolve a viewName=something.html

ResourceHandler
---------------
http://www.baeldung.com/spring-mvc-static-resources

To access static resources like css, images etc easily in jsp file using <c:url....>, you can define ResourceHandler.

@Configuration
@EnableWebMvc
public class MvcConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
          .addResourceHandler("/resources/**")
          .addResourceLocations("(/resources/");
    }
}
Now in jsp, you don't have to use entire path of css file. You can just do as follows.
<link href="<c:url value="/resources/myCss.css" />" rel="stylesheet">

--------------------------------------------------------
What is ModelMap, Model and ModelAttribute?
They are all almost same.
Model extends ModelMap. model.addAttibute(...,...) is same as modelMap.put(...,...)
All objects added as attributes in Model act as command objects. By default Model (or ModelMap or ModelAttribute) objects are placed in Request scope by spring framework.
This Model (or ModelMap or ModelAttribute) object can be accessed easily a method argument. Spring framework simply retrieves it from Request scope and passes in the method as its argument.
  
ModelAttribute is nothing but an attribute set in the Model. Look at ModelAttributeExampleController.java

--------------------------------------------------------

This is the perfect tutorial to understand how @RequestBody and @ResponseBody works
http://www.journaldev.com/2552/spring-restful-web-service-example-with-json-jackson-and-client-program
https://examples.javacodegeeks.com/enterprise-java/spring/mvc/spring-mvc-interceptor-tutorial/

@RequestBody and @ResponseBody works according to Accept Header set in the request.
@RequestBody ane @ResponseBody with method arguments simply means that spring should use RequestResponseBodyMethodArgumentResolver to set the value of the method arguments.
For every annotation in spring, there are Resolvers.

Request comes to DispatcherServlet.
DispatcherServlet's doDispatch(...) figures out correct handler that can handle the request.
This Handler is a type of HandlerExecutorChain(has HandlerMethod->...Controller.create...(...), handlerInterceptors etc)
From Handler, it finds appropriate HandlerAdapter and calls its handle(...) method.
e.g. if HandlerMethod has @RequestMapping, it will use RequestMappingHandlerAdapter.
RequestMappingHandlerAdapter has many MethodArgumentResolvers. It uses appropriate Resolver as per the type or annotation of method argument.
For @RequestBody and @ResponseBody, it uses RequestResponseBodyMethodArgumentResolver.

Argument Resolver called RequestResponseBodyMethodProcessor uses appropriate MessageConverter to convert incoming value to a type expected by Method argument and request's Content-Type header value.
Based on Content-Type header's value it chooses the correct MessageConverter and converts the body of http request to an Object using appropriate MessageConverter.

There are many types of MethodArgumentResolvers like RequestResponseBodyMethodProcessor. It resolves an argument annotated with @RequestBody by calling the RequestBodyAdvice before and after writing the httpservletrequest body to and object using a MessageConverter.

	resolveArgument(...) {
		readWithMessageConverters(...)
	}

	readWithMessageConverters(...) {
		inputMessage = getAdvice().beforeBodyRead(inputMessage, param, targetType, converterType);  --- calls RequestBodyAdvice before converting an request body to appropriate object using MessageConverter
		body = genericConverter.read(targetType, contextClass, inputMessage);
		body = getAdvice().afterBodyRead(body, inputMessage, param, targetType, converterType); --- calls RequestBodyAdvice after converting an request body to appropriate object using MessageConverter
	}

Similarly, it uses MessageConverter to convert outgoing object to a type set by request's Accept header.
It uses ContentNegotiationStrategy to extract the MediaType. There are multiple types of ContentNegotiationStrategies.
    - HeaderContentNegotiationStrategy - looks for Accept header
    - ParameterContentNegotiationStrategy - looks for query param 'format'
    - PathExtensionContentNegotiationStrategy -???
    - ServletPathExtensionContentNegotiationStrategy - uses ServletContext's MimeType to determine MediaType

    e.g. if HeaderContentNegotiationStrategy finds out that there is an Accept Header with value application/json, then MappingJackson2HttpMessageConverter will be used to convert response object to json.

    ContentNegotiation is nothing but how client would like to get the response back. Normally, in rest call, client sets Accept header.
    It is always better to use both Content-Type and Accept headers for spring to better understand the client's request.
    If not provided, then spring will use some MessageConverter. If you have both xtream dependency and jackson dependencies, then it will use MessageConverter suitable to convert the response either to xml or json.

    https://www.youtube.com/watch?v=6yF0GpjAhwk
    https://www.youtube.com/watch?v=UT8-4_3s_XA


	handleReturnValue(...) {
		writeWithMessageConverters(returnValue, returnType, webRequest);
	}

	writeWithMessageConverters(...) {
		getAdvice().beforeBodyWrite(...)   --- calls ResponseBodyAdvice before converting an object using MessageConverter
	}
NOTE: Read JsonTestController and Client.java for better understanding of how these MessageConverters work.



Without @ResponseBody, spring will consider returned string as a view name (jsp name). Look at WelcomeController.java
When you provide @RequestBody and @ResponseBody, you also need provide how request and response objects should be serialized and deserialized.
Read below how to provide serializing/deserializing (marshalling/unmarshalling) api to spring mvc. 

e.g. method argument emp has @RequestBody. While calling this method, if we set Accept:application/json, then we can pass 
json to createEmployee method, spring mvc will convert that json to Employee object.
Similary, returned emp will be converted to json.
 
curl --header "Accept: application/json" http://localhost:8080/SpringRestExample/......

@RequestMapping(value = EmpRestURIConstants.CREATE_EMP, method = RequestMethod.POST)
	public @ResponseBody Employee createEmployee(@RequestBody Employee emp) {
		logger.info("Start createEmployee.");
		emp.setCreatedDate(new Date());
		empData.put(emp.getId(), emp);
		return emp;
	}

We can achieve the similar functionality by adding RequestMappingHandlerAdapter in spring context (here servlet-context.xml).

	<!-- Configure to plugin JSON as request and response in method handler -->
	<beans:bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter">
		<beans:property name="messageConverters">
			<beans:list>
				<beans:ref bean="jsonMessageConverter"/>
			</beans:list>
		</beans:property>
	</beans:bean>
	
	<!-- Configure bean to convert JSON to POJO and vice versa -->
	<beans:bean id="jsonMessageConverter" class="org.springframework.http.converter.json.MappingJackson2HttpMessageConverter">
	</beans:bean>	


If you go in RequestMappingHandlerAdapter class of Spring,
you will see in the constructor that by default following HttpMessageConverter are already added. It looks like by doing above configuration, you are adding one more MappingJackson2HttpMessageConverter to it.
		
		this.messageConverters = new ArrayList<HttpMessageConverter<?>>();
		this.messageConverters.add(new ByteArrayHttpMessageConverter());
		this.messageConverters.add(stringHttpMessageConverter);
		this.messageConverters.add(new SourceHttpMessageConverter<Source>());
		this.messageConverters.add(new AllEncompassingFormHttpMessageConverter());	
		
If you don't apply above spring context configuration, then AllEncompassingFormHttpMessageConverter will automatically detect that jackson library is present in class path, so add MappingJackson2HttpMessageConverter to the list.
Same thing is true for Jaxb2RootElementHttpMessageConverter. 

When request comes, spring will iterate through all available HttpMessageConverters. Each HttpMessageConverer (look at HttpMessageConverter interface) has methods canRead, canWrite, supportedMediaTypes etc. Each HttpMessageConverter can determine
by itself whether it can convert incoming object to a specific media type.	


Migrating from spring 3 rest service to spring 4 rest service
http://xpadro.blogspot.com/2014/01/migrating-spring-mvc-restful-web.html


How Spring MVC works?
---------------------
@EnableWebMvc
@Configuration
CustomWebMvcConfiguration extends WebMvcConfigurerAdapter {  --- WebMvcConfigurerAdapter extends WebMvcConfigurer
	configureMessageConverters() {
		.......
	}
}


public abstract class WebMvcConfigurerAdapter implements WebMvcConfigurer {
	public void addFormatters(FormatterRegistry registry) {}
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {}
	public Validator getValidator() {return null;}
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {}
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {}
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {}
	public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {}
	public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {}
	public MessageCodesResolver getMessageCodesResolver() { return null; }
	public void addInterceptors(InterceptorRegistry registry) { }
	public void addViewControllers(ViewControllerRegistry registry) {}
	public void addResourceHandlers(ResourceHandlerRegistry registry) {}
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {}
}



@Import(DelegatingWebMvcConfiguration.class)
public @interface EnableWebMvc {}


class DelegatingWebMvcConfiguration extends WebMvcConfigurationSupport
{

	private final WebMvcConfigurerComposite configurers = new WebMvcConfigurerComposite();

	@Autowired(required = false)  ----- All beans of type WebMvcConfigurer are registered
	public void setConfigurers(List<WebMvcConfigurer> configurers) {
		if (configurers == null || configurers.isEmpty()) {
			return;
		}
		this.configurers.addWebMvcConfigurers(configurers);
	}

	@Override
	protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		this.configurers.configureMessageConverters(converters);  ----- calls all beans of type WebMvcConfigurer's configureMessageConverters(...)
	}

	.....

}


class WebMvcConfigurationSupport implements ApplicationContextAware, ServletContextAware {

	//--- adds custom configured message converters first, if not available then uses default list of message converters
	protected final List<HttpMessageConverter<?>> getMessageConverters() {
		if (messageConverters == null) {
			messageConverters = new ArrayList<HttpMessageConverter<?>>();
			configureMessageConverters(messageConverters); --- calls  DelegatingWebMvcConfiguration's configureMessageConverters()
			if (messageConverters.isEmpty()) {
				addDefaultHttpMessageConverters(messageConverters);
			}
		}
		return messageConverters;
	}

	protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {}

	protected final void addDefaultHttpMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
		StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
		stringConverter.setWriteAcceptCharset(false);

		messageConverters.add(new ByteArrayHttpMessageConverter());
		messageConverters.add(stringConverter);
		messageConverters.add(new ResourceHttpMessageConverter());
		messageConverters.add(new SourceHttpMessageConverter<Source>());
		messageConverters.add(new AllEncompassingFormHttpMessageConverter());
		if (romePresent) {
			messageConverters.add(new AtomFeedHttpMessageConverter());
			messageConverters.add(new RssChannelHttpMessageConverter());
		}
		if (jaxb2Present) {
			messageConverters.add(new Jaxb2RootElementHttpMessageConverter());
		}
		if (jackson2Present) {
			messageConverters.add(new MappingJackson2HttpMessageConverter());
		}
		else if (jacksonPresent) {
			messageConverters.add(new MappingJacksonHttpMessageConverter());
		}
	}

	@Bean
	public RequestMappingHandlerAdapter requestMappingHandlerAdapter() { --- RequestMappingHandlerAdapter is used to call controller's method and resolving its arguments and returned value
		List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<HandlerMethodArgumentResolver>();
		addArgumentResolvers(argumentResolvers);

		List<HandlerMethodReturnValueHandler> returnValueHandlers = new ArrayList<HandlerMethodReturnValueHandler>();
		addReturnValueHandlers(returnValueHandlers);

		RequestMappingHandlerAdapter adapter = new RequestMappingHandlerAdapter();
		adapter.setContentNegotiationManager(mvcContentNegotiationManager());
		adapter.setMessageConverters(getMessageConverters());  ------ message converters are set in the RequestMappingHandlerAdapter
		adapter.setWebBindingInitializer(getConfigurableWebBindingInitializer());
		adapter.setCustomArgumentResolvers(argumentResolvers); ----- All the argument resolvers to resolve @RequestHeader, @PathParam etc and to set the value of arguments accordingly
		adapter.setCustomReturnValueHandlers(returnValueHandlers); ----- Sets the handlers to resolve return value of the controller's method

		if (jackson2Present) {
			List<ResponseBodyAdvice<?>> interceptors = new ArrayList<ResponseBodyAdvice<?>>();
			interceptors.add(new JsonViewResponseBodyAdvice()); ---- ResponseBodyAdvice called before final response is
			adapter.setResponseBodyAdvice(interceptors);
		}

		AsyncSupportConfigurer configurer = new AsyncSupportConfigurer();
		configureAsyncSupport(configurer);

		if (configurer.getTaskExecutor() != null) {
			adapter.setTaskExecutor(configurer.getTaskExecutor());
		}
		if (configurer.getTimeout() != null) {
			adapter.setAsyncRequestTimeout(configurer.getTimeout());
		}
		adapter.setCallableInterceptors(configurer.getCallableInterceptors());
		adapter.setDeferredResultInterceptors(configurer.getDeferredResultInterceptors());

		return adapter;
	}

	@Bean
	public RequestMappingHandlerMapping requestMappingHandlerMapping() { ----- RequestMappingHandlerMapping is used to detect the controller and its method based on requested REST path
		RequestMappingHandlerMapping handlerMapping = new RequestMappingHandlerMapping();
		handlerMapping.setOrder(0);
		handlerMapping.setInterceptors(getInterceptors());
		handlerMapping.setContentNegotiationManager(mvcContentNegotiationManager());

		PathMatchConfigurer configurer = getPathMatchConfigurer();
		if (configurer.isUseSuffixPatternMatch() != null) {
			handlerMapping.setUseSuffixPatternMatch(configurer.isUseSuffixPatternMatch());
		}
		if (configurer.isUseRegisteredSuffixPatternMatch() != null) {
			handlerMapping.setUseRegisteredSuffixPatternMatch(configurer.isUseRegisteredSuffixPatternMatch());
		}
		if (configurer.isUseTrailingSlashMatch() != null) {
			handlerMapping.setUseTrailingSlashMatch(configurer.isUseTrailingSlashMatch());
		}
		if (configurer.getPathMatcher() != null) {
			handlerMapping.setPathMatcher(configurer.getPathMatcher());
		}
		if (configurer.getUrlPathHelper() != null) {
			handlerMapping.setUrlPathHelper(configurer.getUrlPathHelper());
		}

		return handlerMapping;
	}
}


class DispatcherServlet {
	protected void initStrategies(ApplicationContext context) {
		initMultipartResolver(context);
		initLocaleResolver(context);
		initThemeResolver(context);
		initHandlerMappings(context);
		initHandlerAdapters(context);
		initHandlerExceptionResolvers(context);
		initRequestToViewNameTranslator(context);
		initViewResolvers(context);
		initFlashMapManager(context);
	}


	initHandlerMappings(ApplicationContext context) {
		-- detects all Handler Mapping beans from the BeanFactory
		Map<String, HandlerMapping> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerMapping.class, true, false);
		-- sort HandlerMappings as per the provided order of their beans
		OrderComparator.sort(this.handlerMappings);


		-- if there is no HandlerMapping inside the BeanFactory, it uses the old spring approach
		if (this.handlerMappings == null) {
			this.handlerMappings = getDefaultStrategies(context, HandlerMapping.class);
		}
	}

	initHandlerAdapters(ApplicationContext context) {
		Map<String, HandlerAdapter> handlerAdapters = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerAdapter.class, true, false);
		-- sort HandlerAdapter as per the provided order of their beans
		OrderComparator.sort(handlerAdapters);
	}

	void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HandlerExecutionChain mappedHandler = getHandler(processedRequest);  --- iterates through all HandlerMappings and finds the Controller's method and related interceptors
		...
		HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler()); --- finds HandlerAdapter that supports found Controller's method

		if (!mappedHandler.applyPreHandle(processedRequest, response)) { --- calls interceptors preHandle methods
			return;
		}

		// Actually invoke the handler.
		mv = ha.handle(processedRequest, response, mappedHandler.getHandler()); ---- calls HandlerAdapter's handle()

		if (asyncManager.isConcurrentHandlingStarted()) {
			return;
		}

		applyDefaultViewName(request, mv);
		mappedHandler.applyPostHandle(processedRequest, response, mv); --- calls interceptors postHandle methods

	}

}


HandlerMapping and HandlerAdapter
---------------------------------

Look at DispatcherServlet's initHandlerMapping() and doDispatch().
doDispatch() iterates all HandlerMappings and creates HandlerExecutionChain having handler method (matched controller method) and Handler(Controller) interceptors.
HandlerInterceptor has methods like pre and postHandle methods which are called before and after controller's method is called.

After HandlerMappings are called, HandlerAdapters are called to actually invoke handler (controller's) method.

https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/servlet/HandlerMapping.html

e.g. RequestMappingHandlerMapping - based on url it finds, it matches url with @RequestMapping annotation in @Controller class and creates matched controller information int the form of RequestMappingInfo.

RequestMappingInfo encapsulates the following request mapping conditions:
   PatternsRequestCondition
   RequestMethodsRequestCondition
   ParamsRequestCondition
   HeadersRequestCondition
   ConsumesRequestCondition
   ProducesRequestCondition
   RequestCondition (optional, custom request condition)

Unless a specific handler mapping is defined in your context, it will use default handler mapping.

e.g. RequestMappingHandlerAdapter
It actually calls controller's matched method. Before that it does a lot many things like resolving matched methods arguements
and after invoking a method, resolving return type of the method.

It has
public void afterPropertiesSet() {
		// Do this first, it may add ResponseBody advice beans
		initControllerAdviceCache();

		if (this.argumentResolvers == null) {
			List<HandlerMethodArgumentResolver> resolvers = getDefaultArgumentResolvers(); ----- intializes argument resolvers
			this.argumentResolvers = new HandlerMethodArgumentResolverComposite().addResolvers(resolvers);
		}
		if (this.initBinderArgumentResolvers == null) {
			List<HandlerMethodArgumentResolver> resolvers = getDefaultInitBinderArgumentResolvers();
			this.initBinderArgumentResolvers = new HandlerMethodArgumentResolverComposite().addResolvers(resolvers);
		}
		if (this.returnValueHandlers == null) {
			List<HandlerMethodReturnValueHandler> handlers = getDefaultReturnValueHandlers();
			this.returnValueHandlers = new HandlerMethodReturnValueHandlerComposite().addHandlers(handlers);
		}
	}

e.g. RequestParamMapMethodArgumentResolver
It resolves method argument annoted with @RequestParam. It sets the value of parameter by extracting it related value from request parameters of httprequest.

RequestResponseBodyMethodProcessor
It uses message converters and resolves method parameters annoted with @RequestBody and @ResponseBody

You can create customized HandlerMapping and HandlerAdapter also
http://www.java-allandsundry.com/2013/01/spring-mvc-customizing.html
https://sergialmar.wordpress.com/2011/03/29/extending-handler-method-argument-resolution-in-spring-mvc/
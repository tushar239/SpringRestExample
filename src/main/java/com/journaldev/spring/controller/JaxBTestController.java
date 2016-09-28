package com.journaldev.spring.controller;

import jaxb.example.models.Product;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/*
 * This controller is using Product model which has JAXB annotation on it. 
 * So, spring mvc will use JaxBMarshaller to generate xml response using JAXB.
 * It will convert returned Product to xml using JAXB.
 * 
 * No extra configuration is required in spring context because JAXB converter will be a part of default list HttpMessageConverters, if JAXB library is there in pom.xml
 * Read readme.txt for more information
 * 
 * Couldn't test the same thing with XStream. It didn't work. I tried to add Xstream marshaller in spring context as it is not a part of default HttpMessageConverters, but still it didn't work. 
 */
@Controller
public class JaxBTestController {
    @RequestMapping(value="/products/{productId}", method = RequestMethod.GET)
    // By adding produces=application/json, you are forcing spring to use Jackson library to generate json response.
    // By default, as Product has JAXB annotation, it will generate XML output.
    // 'produces' sets Content-Type of the response header. Similarly, consumes sets Content-Type of request header. 
    //@RequestMapping(value="/products/{productId}", method = RequestMethod.GET, produces = "application/json")
    
    public @ResponseBody Product getProductById(@PathVariable Long productId) {
        Product product = new Product();
        product.setId(productId);
        product.setName("amazing product");
        return product;
    }
}

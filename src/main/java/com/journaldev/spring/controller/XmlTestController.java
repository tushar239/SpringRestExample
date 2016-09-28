package com.journaldev.spring.controller;

import jaxb.example.models.Lead;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("xmlaccepting")
public class XmlTestController {
    // Look at Client.java's testXmlInputOutput()
    // You can also look at http://www.byteslounge.com/tutorials/spring-mvc-requestmapping-consumes-condition-example
    @RequestMapping(value = "/xmlconsumetest", method = RequestMethod.POST, consumes = "application/xml")
    public ResponseEntity<String> processXml(@RequestBody Lead lead) {
        return new ResponseEntity<String>(
                "Handled application/xml request. Request body was: " + lead.getName(), 
                new HttpHeaders(), 
                HttpStatus.OK);
    }
    
    // Look at Client.java's testXmlInputOutput()
    // if you don't put produces="application/xml", then by default it will produce json response.
    // because in default list of message converters, first one is Jackson and then JaxB 
    @RequestMapping(value = "/xmlproducetest", method = RequestMethod.POST, produces = "application/xml")
    public @ResponseBody Lead produceXml(@RequestBody Lead lead) {
       return lead;
    }
}

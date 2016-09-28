package com.journaldev.spring.controller;

import my.spring.mvc.examples.UserDetails;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
/*
 Test using Client.java
 */
@Controller
@RequestMapping("/entity")
public class RequestResponseEntityExampleController {
    @RequestMapping("/returnstring")
    public ResponseEntity<String> returnString() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("MyResponseHeader", "MyValue");
        return new ResponseEntity<String>("Hello World", responseHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value="/handleobject",  method = RequestMethod.POST)
    public ResponseEntity<UserDetails> handleObject(@RequestBody UserDetails userDetailsRequest) {
        userDetailsRequest.setEmailId("test@test.com");
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("MyResponseHeader", "MyValue");
        return new ResponseEntity<UserDetails>(userDetailsRequest, responseHeaders, HttpStatus.CREATED);
    }
    
    // even String argument needs to be serialized using @RequestBody
    // look at JasonTestController.java to know how serialization/deserialization works in spring mvc.
    @RequestMapping(value="/handlestring",  method = RequestMethod.POST)
    public ResponseEntity<String> handleString(@RequestBody String str) {
        System.out.println("Inside rest method, str:"+str);
        HttpHeaders responseHeaders = new HttpHeaders();
        return new ResponseEntity<String>(str, responseHeaders, HttpStatus.CREATED);
    }
}

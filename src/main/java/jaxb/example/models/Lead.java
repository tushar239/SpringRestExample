package jaxb.example.models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Lead {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}

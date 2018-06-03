package model;

import javafx.collections.ObservableList;

public class Admin {

    private ObservableList<User> users ;
    private String name;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

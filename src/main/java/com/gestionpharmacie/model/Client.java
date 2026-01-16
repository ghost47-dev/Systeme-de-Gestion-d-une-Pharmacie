package com.gestionpharmacie.model;

public class Client {
    private int id;
    private String name, surname;
    private int phoneNumber;

    public Client(int id, String name, String surname, int phoneNumber){
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
    }

    public int getId(){
        return id;
    }

    public void display(){
        System.out.println("Client " + name + " " + surname + " " + phoneNumber);
    }
}

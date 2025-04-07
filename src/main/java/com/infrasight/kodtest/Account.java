package com.infrasight.kodtest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {
    
    @JsonProperty("id")
    public String id;

    @JsonProperty("active")
    public boolean active;

    @JsonProperty("computer")
    public boolean computer;

    @JsonProperty("age")
    public int age;

    @JsonProperty("employedSince")
    public long employedSince;
    
    @JsonProperty("foodPreference")
    public String foodPreference;
    
    @JsonProperty("lastName")
    public String lastName;
    
    @JsonProperty("phone")
    public String phone;
    
    @JsonProperty("phoneType")
    public String phoneType;
    
    @JsonProperty("salaryCurrency")
    public String salaryCurrency;
    
    @JsonProperty("computerModel")
    public String computerModel;
    
    @JsonProperty("firstName")
    public String firstName;
    
    @JsonProperty("employeeId")
    public String employeeId;
    
    @JsonProperty("salary")
    public int salary;

    public Account() {
    }

    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmployeeId() { return employeeId; }
    public boolean isActive() { return active; }
    public boolean hasComputer() { return computer; }
    public int getAge() { return age; }
    public long getEmployedSince() { return employedSince; }
    public String getFoodPreference() { return foodPreference; }
    public String getPhone() { return phone; }
    public String getSalaryCurrency() { return salaryCurrency; }
    public String getComputerModel() { return computerModel; }
    public int getSalary() { return salary; }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", active=" + active +
                ", computer=" + computer +
                ", age=" + age +
                ", employedSince=" + employedSince +
                ", foodPreference='" + foodPreference + '\'' +
                ", phone='" + phone + '\'' +
                ", salaryCurrency='" + salaryCurrency + '\'' +
                ", computerModel='" + computerModel + '\'' +
                ", salary=" + salary +
                '}';
    }
}

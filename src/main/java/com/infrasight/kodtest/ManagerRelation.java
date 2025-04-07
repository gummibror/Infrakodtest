package com.infrasight.kodtest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class ManagerRelation {

    @JsonProperty("objectType")
    private String objectType;

    @JsonProperty("id")
    private String id;

    @JsonProperty("accountId")
    private String accountId;

    @JsonProperty("managedId")
    private String managedId;

    public String getObjectType() {
        return objectType;
    }

    public String getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getManagedId() {
        return managedId;
    }

    @Override
    public String toString() {
        return "ManagerRelation{" +
                "objectType='" + objectType + '\'' +
                ", id='" + id + '\'' +
                ", accountId='" + accountId + '\'' +
                ", managedId='" + managedId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ManagerRelation)) return false;
        ManagerRelation that = (ManagerRelation) o;
        return Objects.equals(id, that.id); // Uniqueness based on id
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

package com.infrasight.kodtest;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupRelation {
    
    @JsonProperty("objectType")
    private String objectType;

    @JsonProperty("id")
    private String id;

    @JsonProperty("memberId")
    private String memberId;

    @JsonProperty("groupId")
    private String groupId;

    public GroupRelation() {
    }

    public String getObjectType() { return objectType; }
    public String getId() { return id; }
    public String getMemberId() { return memberId; }
    public String getGroupId() { return groupId; }

    @Override
    public String toString() {
        return "Group{" +
                "objectType='" + objectType + '\'' +
                ", id='" + id + '\'' +
                ", memberId='" + memberId + '\'' +
                ", groupId='" + groupId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupRelation group = (GroupRelation) o;
        return Objects.equals(id, group.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId);
    }
}
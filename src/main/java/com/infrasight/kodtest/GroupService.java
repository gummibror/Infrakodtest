package com.infrasight.kodtest;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.*;
/**
 * Service class for handling group relations.
 */
public class GroupService {
    private final HttpClientWrapper httpClient;
    private final String baseUrl;
    private final String authToken;

    public GroupService(HttpClientWrapper httpClient, String baseUrl, String authToken) {
        this.httpClient = httpClient;
        this.baseUrl = baseUrl;
        this.authToken = authToken;
    }
    /**
     * Fetches all direct groups for a given member ID.
     *
     * @param memberId The ID of the member.
     * @return A set of direct group relations.
     */
    public Set<GroupRelation> getDirectGroupsOnMemberId(String memberId) {
        Set<GroupRelation> directGroups = new HashSet<>();
        fetchDirectGroups(memberId, directGroups);
        return directGroups;
    }
    
    private void fetchDirectGroups(String memberId, Set<GroupRelation> directGroups) {
        int offset = 0; // Pagination offset
        int limit = 50; // Number of results per request (API limit)
        boolean hasMore = true;

        
        while (hasMore) { // runs again if needed until all groups have been fetched
            String url = baseUrl + "/api/relationships?filter=memberId%3D" + memberId + "&skip=" + offset + "&take=" + limit;
            Map<String, String> headers = Map.of("Authorization", authToken, "Accept", "application/json");

            Optional<List<GroupRelation>> groupsOpt = httpClient.sendRequest(
                url, "GET", headers, null, new TypeReference<List<GroupRelation>>() {}
            );

            if (groupsOpt.isPresent() && !groupsOpt.get().isEmpty()) {
                
                List<GroupRelation> groups = groupsOpt.get();
                for (GroupRelation group : groups) {
                    directGroups.add(group); 
                }
                hasMore = groups.size() == limit;
                offset += limit;
            } else {
                hasMore = false;
            }
        }
    }
    /**
     * Fetches all group relations for a given member ID, including sub-groups.
     * @param memberId
     * @return A set of all group relations. can contain same group multiple times but not relation to said group.
     */
    public Set<GroupRelation> getAllGroupsOnMemberId(String memberId) {
        Set<GroupRelation> allGroups = new HashSet<>();
        fetchAllGroups(memberId, allGroups);
        return allGroups;
    }

    private void fetchAllGroups(String incId, Set<GroupRelation> allGroups) {
        int offset = 0;
        int limit = 50;
        boolean hasMore = true;

        while (hasMore) { // runs again if needed until all groups have been fetched
            String url = baseUrl + "/api/relationships?filter=memberId%3D" + incId + "&skip=" + offset + "&take=" + limit;
            Map<String, String> headers = Map.of("Authorization", authToken, "Accept", "application/json");

            Optional<List<GroupRelation>> groupsOpt = httpClient.sendRequest(
                url, "GET", headers, null, new TypeReference<List<GroupRelation>>() {}
            );

            if (groupsOpt.isPresent() && !groupsOpt.get().isEmpty()) {
                List<GroupRelation> groups = groupsOpt.get();
                for (GroupRelation group : groups) {
                    if (allGroups.add(group)) { 
                        fetchAllGroups(group.getGroupId(), allGroups); 
                    }
                } 
                hasMore = groups.size() == limit;
                offset += limit;
            } else {
                hasMore = false;
            }
        }
    }
    /**
     * Fetches all group relations for a given group ID,
     * including contents of subgroups, but not the subgroup relation itself.
     *
     * @param groupId The ID of the group.
     * @return A list of group relations.
     */
    public Optional<List<GroupRelation>> getAllGroupRelations(String groupId) {
        List<GroupRelation> allRelations = new ArrayList<>();
        fetchAllGroupRelations(groupId, allRelations);
        return Optional.of(allRelations);
    }

    private Optional<List<GroupRelation>> fetchAllGroupRelations(String incId, List<GroupRelation> allRelations) {
        int offset = 0; // Pagination offset
        int limit = 50; // Number of results per request (API limit)
        boolean hasMore = true;

        while (hasMore) {            
            String url = baseUrl + "/api/relationships?filter=groupId%3D" + incId + "&skip=" + offset + "&take=" + limit;
            Map<String, String> headers = Map.of("Authorization", authToken, "Accept", "application/json");

            Optional<List<GroupRelation>> response = httpClient.sendRequest(
                url, "GET", headers, null, new TypeReference<List<GroupRelation>>() {}
            );

            if (response.isPresent() && !response.get().isEmpty()) {
                List<GroupRelation> relations = response.get();
                for(GroupRelation relation : relations) {
                    if((!allRelations.contains(relation))) { 
                        if(relation.getMemberId().startsWith("grp")){ 
                            fetchAllGroupRelations(relation.getMemberId(), allRelations);
                        }else{
                            allRelations.add(relation); 
                        }
                    }
                }
                hasMore = relations.size() == limit; 
                offset += limit; 
            } else {
                hasMore = false; 
            }
        }

        return Optional.of(allRelations);
    }

    /**
     *  Fetches all managers for a given managed ID, in other words the id of an account
     * @param managedId 
     * @return A list of manager relations.
     */
    public Optional<List<ManagerRelation>> getManagersFor(String managedId){
        List<ManagerRelation> allManagers = new ArrayList<>();
        int offset = 0; // Pagination offset
        int limit = 50; // Number of results per request (API limit)
        boolean hasMore = true;
        
        while(hasMore){
            String url = baseUrl + "/api/relationships?filter=managedId%3D" + managedId + "&skip=" + offset + "&take=" + limit;
            Map<String, String> headers = Map.of("Authorization", authToken, "Accept", "application/json");
            
            Optional<List<ManagerRelation>> managerOpt = httpClient.sendRequest(
                url, "GET", headers, null, new TypeReference<List<ManagerRelation>>() {}
            );
            if(managerOpt.isPresent() && !managerOpt.get().isEmpty()){
                List<ManagerRelation> managers = managerOpt.get();
                allManagers.addAll(managers);
                hasMore = managers.size() == limit;;
                offset += limit;
            } else {
                hasMore = false;
            }
        }

        return allManagers.isEmpty() ? Optional.empty() : Optional.of(allManagers);
    }
}
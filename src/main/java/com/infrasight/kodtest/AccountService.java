package com.infrasight.kodtest;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *  Service class for retrieving accounts from the API.
 */
public class AccountService {
    private final HttpClientWrapper httpClient;
    private final String baseUrl;
    private final String authToken;

    public AccountService(HttpClientWrapper httpClient, String baseUrl, String authToken) {
        this.httpClient = httpClient;
        this.baseUrl = baseUrl;
        this.authToken = authToken;
    }
    /**
     * Retrieves accounts associated with a specific group ID. Ended up not being used.
     *
     * @param groupId The group ID .
     * @return An Optional containing a list of accounts, or an empty Optional if no accounts are found.
     */
    public Optional<List<Account>> getAccountsInGroup(String groupId) {
        String url = baseUrl + "/api/relationships?filter=groupId%3D" + groupId;
        Map<String, String> headers = Map.of("Authorization", authToken, "Accept", "application/json");

        Optional<List<GroupRelation>> relations = httpClient.sendRequest(
            url, "GET", headers, null, new TypeReference<List<GroupRelation>>() {});

        List<Account> accounts = relations.get().stream()
            .map(relation -> getAccountByEmployeeId(relation.getMemberId()))
            .flatMap(Optional::stream)
            .flatMap(List::stream)
            .collect(Collectors.toList());

        return accounts.isEmpty() ? Optional.empty() : Optional.of(accounts);
    }
    /**
     * Retrieves accounts associated with a specific employee ID.
     *
     * @param accountId The employee ID.
     * @return An Optional containing a list of accounts, or an empty Optional if no accounts are found.
     */

    public Optional<List<Account>> getAccountByEmployeeId(String accountId) {
        String url = baseUrl + "/api/accounts?filter=employeeId%3D" + accountId;
        Map<String, String> headers = Map.of("Authorization", authToken, "Accept", "application/json");

        Optional<List<Account>> response = httpClient.sendRequest(
            url, "GET", headers, null, new TypeReference<List<Account>>() {}
        );

        return response;
    }

    /**
     * Retrieves accounts associated with a specific account ID.
     *
     * @param accountId The account ID.
     * @return An Optional containing a list of accounts, or an empty Optional if no accounts are found.
     */

    public Optional<List<Account>> getAccountById(String accountId) {
        String url = baseUrl + "/api/accounts?filter=id%3D" + accountId;
        Map<String, String> headers = Map.of("Authorization", authToken, "Accept", "application/json");

        Optional<List<Account>> response = httpClient.sendRequest(
            url, "GET", headers, null, new TypeReference<List<Account>>() {}
        );
        return response;
    }

    /**
     * Converts a list of group relations to a list of accounts.
     *
     * @param relations The list of group relations.
     * @return An Optional containing a list of accounts, or an empty Optional if no accounts are found.
     */

    public Optional<List<Account>> convertRelationsToAccounts(List<GroupRelation> relations) {
    List<Account> accounts = new ArrayList<>();

    for (GroupRelation relation : relations) {
       Optional<List<Account>> acc = getAccountById(relation.getMemberId());
       accounts.addAll(acc.get());  
    }
    return Optional.of(accounts);
}

}
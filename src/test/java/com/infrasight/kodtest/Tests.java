package com.infrasight.kodtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

/**
 * Simple concrete class for JUnit tests with uses {@link TestsSetup} as a
 * foundation for starting/stopping the API server for tests.
 * 
 * You may configure port, api user and api port in {@link TestVariables} if
 * needed.
 */
public class Tests extends TestsSetup {

	/**
	 * Simple example test which asserts that the Kodtest API is up and running.
	 */
	@Test
	public void connectionTest() throws InterruptedException {
		assertTrue(serverUp);
	}

	@Test
	public void assignment1() throws InterruptedException {
		assertTrue(serverUp);

		/**
		 * Retrieves the account for the provided "account ID" 
		 * and confirms it belongs to the ID "vera_scop"
		 */
		Optional<List<Account>> account = accountService.getAccountByEmployeeId("1337");
		Account accountVera = account.flatMap(accList -> accList.stream().findFirst()).orElse(null); 

		 assertEquals("vera_scope", accountVera.getId());
	}

	@Test
	public void assignment2() throws InterruptedException {
		assertTrue(serverUp);

		/**
		 * Again retrieves the account for vera.
		 * but also retriving the group relations she is directly involved
		 */
		Optional<List<Account>> account = accountService.getAccountByEmployeeId("1337");
		Account accountVera = account.flatMap(accList -> accList.stream().findFirst()).orElse(null); 
		Set<GroupRelation> directGroups = groupService.getDirectGroupsOnMemberId(accountVera.getId());

		int groupCount = directGroups.size();

		assertEquals(3, groupCount);

		/**
		 * asserts that the retrieved group realations has the expected ID's
		 */
		 assert(directGroups.stream().anyMatch(group -> group.getGroupId().equals("grp_itkonsulter")));
		 assert(directGroups.stream().anyMatch(group -> group.getGroupId().equals("grp_köpenhamn")));
		 assert(directGroups.stream().anyMatch(group -> group.getGroupId().equals("grp_malmo")));
	}

	@Test
	public void assignment3() throws InterruptedException {
		assertTrue(serverUp);

		/**
		 * Once again retrieves the account for vera.
		 * and this time also retrieves the relations to sub groups, IE all group relations of vera.
		 */
		Optional<List<Account>> account = accountService.getAccountByEmployeeId("1337");
		Account accountVera = account.flatMap(accList -> accList.stream().findFirst()).orElse(null); 
		Set<GroupRelation> allGroups = groupService.getAllGroupsOnMemberId(accountVera.getId());
		
		/*
		 * since we retrieve all relations there can be multiple relations to the same group.
		 * and we only want the groups so we remake the set with unique values.
		 */
		Map<String, GroupRelation> uniqueGroupIds = new HashMap<>();
        for (GroupRelation relation : allGroups) {
            uniqueGroupIds.putIfAbsent(relation.getGroupId(), relation);
        }

        Set<GroupRelation> uniqueGroups = new HashSet<>(uniqueGroupIds.values());
		int groupCount = uniqueGroups.size();

		/*
		 * assert that the number of groups are as expected 
		 * and that their ID's are as expected
		 */
		assertEquals(9, groupCount);
		
		assert(uniqueGroups.stream().anyMatch(group -> group.getGroupId().equals("grp_inhyrda")));
		assert(uniqueGroups.stream().anyMatch(group -> group.getGroupId().equals("grp_itkonsulter")));
		assert(uniqueGroups.stream().anyMatch(group -> group.getGroupId().equals("grp_choklad")));
		
		assert(uniqueGroups.stream().anyMatch(group -> group.getGroupId().equals("grp_sverige")));
		assert(uniqueGroups.stream().anyMatch(group -> group.getGroupId().equals("grp_konfektyr")));
		assert(uniqueGroups.stream().anyMatch(group -> group.getGroupId().equals("grp_köpenhamn")));

		assert(uniqueGroups.stream().anyMatch(group -> group.getGroupId().equals("grp_danmark")));
		assert(uniqueGroups.stream().anyMatch(group -> group.getGroupId().equals("grp_malmo")));
		assert(uniqueGroups.stream().anyMatch(group -> group.getGroupId().equals("grp_chokladfabrik")));
	}

	@Test
	public void assignment4() throws InterruptedException {
		assertTrue(serverUp);

		/**
		 * we retrieve a list of group relations where the group ID is "grp_inhyrda"
		 * then we use this list to get a list of the accounts of "inhyrd" staff
		 * with the accounts we can get their salary, but we only want it if they are active
		 * if they are, we add to a total
		 */

		Optional<List<GroupRelation>> groupRelations = groupService.getAllGroupRelations("grp_inhyrda");
		final int[] inHyrdaSalary = {0};
		groupRelations.ifPresent(inhyrdaRels -> {
            Optional<List<Account>> inHyrdaAccounts = accountService.convertRelationsToAccounts(inhyrdaRels);
			for(Account acc : inHyrdaAccounts.get()){
				if(acc.isActive()){
					inHyrdaSalary[0] += acc.getSalary();
				} 
			}
		});
		// check if the total salary is as expected
		assertEquals( 18408801, inHyrdaSalary[0]);
	}

	@Test
	public void assignment5() throws InterruptedException {
		assertTrue(serverUp);

		/*
		 * we start by getting all relations for the groups saljare and people in sverige

		 */

		Optional<List<GroupRelation>> saljarRelations = groupService.getAllGroupRelations("grp_saljare");
		Optional<List<GroupRelation>> sweRealations = groupService.getAllGroupRelations("grp_sverige");
       
		/*
		 * we make a set of the people found in sverige
		 * and then make a list of the saljare that are in the set of sverige members
		 * creating a list of saljare in sverige
		 */
		Set<String> sverigeIds = sweRealations.get().stream()
            .map(GroupRelation::getMemberId)
            .collect(Collectors.toSet());
       
		List<GroupRelation> sweSaljare = saljarRelations.get().stream()
            .filter(saljare -> sverigeIds.contains(saljare.getMemberId()))
            .collect(Collectors.toList());
		

		/*
		 * now we convert the list of relations to a list of accounts
		 * and then filter out the accounts that were not hired between 2019 and 2022
		 * i did not interpret the instruction as if they should be active, but we could do that here.
		 */
		List<Account> sweSaljareAccs = accountService.convertRelationsToAccounts(sweSaljare).orElse(null);

		sweSaljareAccs = sweSaljareAccs.stream()
			.filter(gr -> gr.employedSince >= 1546300800 && gr.employedSince <= 1672531199)
			.collect(Collectors.toList());
		
		/*
		 * we make a map representing the managers and their staff
		 * we take each account of the saljare in sverige
		 * get their ID
		 * retrieve their manager(s) through managerRelations and the relations account ID
		 * we put the id in the map (essentially putting the manager in the map)
		 * and if a manager is to be put into the map multiple times their staff increase by 1
		 */
		Map<String, Integer> managers = new HashMap<>();

		for(Account sweSaljAcc : sweSaljareAccs){
            String sSAID = sweSaljAcc.getId();
            Optional<List<ManagerRelation>> managerRelations = groupService.getManagersFor(sSAID);
            if(managerRelations.isPresent()){
                List<ManagerRelation> managersList = managerRelations.get();
                for(ManagerRelation mR : managersList){
                    String mID = mR.getAccountId();
                    if(managers.containsKey(mID)){
                        managers.put(mID, managers.get(mID) + 1);
                    } else {
                        managers.put(mID, 1);
                    }
                }
            }
        }

		/*
		 * now we make that map into a list sorted based on the number off staff a manager has
		 * and we make a list of staff with corresping elements to the manager list
		 */

        List<String> sortedManagers = managers.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

		List<Integer> staff = new ArrayList<>();
		for(String manager : sortedManagers){
			staff.add(managers.get(manager));
		}
		/*
		 * we assert that the managers with saljare in sverige are who is expected
		 * and in the correct order. 
		 * and likewise the staff, the amount expected and in the right order
		 */
		assertEquals("acc43", sortedManagers.get(0));
		assertEquals("acc62", sortedManagers.get(1));
		assertEquals("acc4", sortedManagers.get(2));
		assertEquals("acc706", sortedManagers.get(3));
		assertEquals("acc808", sortedManagers.get(4));
		assertEquals("acc818", sortedManagers.get(5));
		assertEquals("acc710", sortedManagers.get(6));
		assertEquals("acc802", sortedManagers.get(7));

		assertEquals(8, staff.get(0).intValue());
		assertEquals(7, staff.get(1).intValue());
		assertEquals(6, staff.get(2).intValue());
		assertEquals(3, staff.get(3).intValue());
		assertEquals(2, staff.get(4).intValue());
		assertEquals(2, staff.get(5).intValue());
		assertEquals(1, staff.get(6).intValue());
		assertEquals(1, staff.get(7).intValue());
		
		
	}
}

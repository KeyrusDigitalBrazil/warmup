/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.platformbackoffice.dao;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.platformbackoffice.model.BackofficeSavedQueryModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;


public class BackofficeSavedQueryDAOTest extends ServicelayerTransactionalTest
{
	public static final String TEST_USER_1 = "savedQueriesUser1";
	public static final String TEST_USER_2 = "savedQueriesUser2";
	public static final String TEST_USER_3 = "savedQueriesUser3";
	public static final String TEST_USER_4 = "savedQueriesUser4";
	public static final String TEST_USER_WITHOUT_GROUPS = "savedQueriesUser4WithoutGroups";
	public static final String TEST_Group_1 = "savedQueriesUserGroup1";
	public static final String TEST_Group_2 = "savedQueriesUserGroup2";
	public static final String TEST_Group_3 = "savedQueriesUserGroup3";
	public static final String TEST_Group_4 = "savedQueriesUserGroup4";
	public static final String TEST_QUERY_1 = "savedQuery1";
	public static final String TEST_QUERY_2 = "savedQuery2";
	public static final String TEST_QUERY_3 = "savedQuery3";
	public static final String TEST_QUERY_4 = "savedQuery4";
	public static final String TEST_QUERY_5 = "savedQuery5";
	@Resource
	private BackofficeSavedQueryDAO backofficeSavedQueryDAO;

	@Resource
	private ModelService modelService;

	@Resource
	private UserService userService;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		final UserGroupModel userGroup1 = createUserGroup(TEST_Group_1);
		final UserGroupModel userGroup2 = createUserGroup(TEST_Group_2);
		final UserGroupModel userGroup3 = createUserGroup(TEST_Group_3);
		final UserGroupModel userGroup4 = createUserGroup(TEST_Group_4);
		userGroup3.setGroups(Sets.newHashSet(userGroup1, userGroup2));
		modelService.save(userGroup3);

		final UserModel testUser1 = createTestUser(TEST_USER_1, userGroup1);
		final UserModel testUser2 = createTestUser(TEST_USER_2, userGroup2);
		createTestUser(TEST_USER_3, userGroup3);
		final UserModel testUser4 = createTestUser(TEST_USER_4, userGroup4);
		final UserModel testUserWithoutGroup = createTestUser(TEST_USER_WITHOUT_GROUPS);

		createTestQuery(TEST_QUERY_1, testUser1, userGroup2);
		createTestQuery(TEST_QUERY_2, testUser2, userGroup1);
		createTestQuery(TEST_QUERY_3, testUser2);
		createTestQuery(TEST_QUERY_4, testUser4);
		createTestQuery(TEST_QUERY_5, testUserWithoutGroup);
	}

	@Test
	public void testFindSavedQueries() throws Exception
	{
		List<BackofficeSavedQueryModel> savedQueries = backofficeSavedQueryDAO
				.findSavedQueries(userService.getUserForUID(TEST_USER_1));
		assertThat(savedQueries).hasSize(2);
		assertThat(getQueryByName(TEST_QUERY_1, savedQueries)).isNotNull();
		assertThat(getQueryByName(TEST_QUERY_2, savedQueries)).isNotNull();

		savedQueries = backofficeSavedQueryDAO.findSavedQueries(userService.getUserForUID(TEST_USER_2));
		assertThat(savedQueries).hasSize(3);
		assertThat(getQueryByName(TEST_QUERY_1, savedQueries)).isNotNull();
		assertThat(getQueryByName(TEST_QUERY_2, savedQueries)).isNotNull();
		assertThat(getQueryByName(TEST_QUERY_3, savedQueries)).isNotNull();

		savedQueries = backofficeSavedQueryDAO.findSavedQueries(userService.getUserForUID(TEST_USER_3));
		assertThat(savedQueries).hasSize(2);
		assertThat(getQueryByName(TEST_QUERY_1, savedQueries)).isNotNull();
		assertThat(getQueryByName(TEST_QUERY_2, savedQueries)).isNotNull();

		savedQueries = backofficeSavedQueryDAO.findSavedQueries(userService.getUserForUID(TEST_USER_4));
		assertThat(savedQueries).hasSize(1);
		assertThat(getQueryByName(TEST_QUERY_4, savedQueries)).isNotNull();
	}

	@Test
	public void tesFindSavedQueriesWithoutUserGroups()
	{
		final UserModel userWithNoGroups = userService.getUserForUID(TEST_USER_WITHOUT_GROUPS);
		final List<BackofficeSavedQueryModel> savedQueries = backofficeSavedQueryDAO.findSavedQueries(userWithNoGroups);
		assertThat(userWithNoGroups.getAllGroups()).isNullOrEmpty();
		assertThat(savedQueries).hasSize(1);
		assertThat(savedQueries.get(0).getName()).isEqualTo(TEST_QUERY_5);
	}

	private BackofficeSavedQueryModel getQueryByName(final String queryName, final List<BackofficeSavedQueryModel> queries)
	{
		return queries.stream().filter(query -> queryName.equals(query.getName())).findFirst().get();
	}

	private UserModel createTestUser(final String userId, final UserGroupModel... userGroups)
	{
		final UserModel newUser = modelService.create(UserModel.class);
		newUser.setUid(userId);
		if (userGroups != null && userGroups.length > 0)
		{
			newUser.setGroups(Sets.newHashSet(userGroups));
		}
		modelService.save(newUser);
		return newUser;
	}

	private UserGroupModel createUserGroup(final String userGroupName)
	{
		final UserGroupModel userGroup = modelService.create(UserGroupModel.class);
		userGroup.setUid(userGroupName);
		modelService.save(userGroup);
		return userGroup;
	}

	private void createTestQuery(final String queryName, final UserModel owner, final UserGroupModel... userGroups)
	{
		final BackofficeSavedQueryModel savedQuery = modelService.create(BackofficeSavedQueryModel.class);
		savedQuery.setName(queryName);
		savedQuery.setTypeCode("testTypeCode");
		savedQuery.setQueryOwner(owner);
		savedQuery.setUserGroups(Arrays.asList(userGroups));
		modelService.save(savedQuery);
	}
}

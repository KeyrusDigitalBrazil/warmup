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
package de.hybris.platform.permissionsfacades.impl;


import static org.junit.Assert.assertFalse;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.permissionsfacades.data.CatalogPermissionsData;
import de.hybris.platform.permissionsfacades.data.PermissionsData;
import de.hybris.platform.permissionsfacades.data.SyncPermissionsData;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.security.permissions.PermissionAssignment;
import de.hybris.platform.servicelayer.security.permissions.PermissionManagementService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.SetUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;


@IntegrationTest
public class DefaultPermissionsFacadeIntegrationTest extends ServicelayerTest
{
	private static final String FALSE = "false";
	private static final String TRUE = "true";
	private static final String CHANGE_PERM = "changerights";
	private static final String REMOVE = "remove";
	private static final String CREATE = "create";
	private static final String CHANGE = "change";
	private static final String READ = "read";
	private static final String WRITE = "write";
	private static final String USER_TYPECODE = "User";
	private static final String ORDER_TYPECODE = "Order";
	private static final String ITEM_OWNER_ATTRIBUTE = "Item.owner";
	private static final String ITEM_PK_ATTRIBUTE = "Item.pk";
	private static final String USER_OWNER_ATTRIBUTE = "User.owner";
	private static final String USER_PK_ATTRIBUTE = "User.pk";
	private static final String ADMIN = "admin";
	private static final String SUPERGROUP = "supergroup";
	private static final String GROUP1 = "group1";
	private static final String GROUP2 = "group2";
	private static final String SUBGROUP1 = "subgroup1";
	private static final String USER1 = "user1";
	private static final String USER2 = "user2";
	private static final String USER3 = "user3";
	private static final String NOT_EXISTING_USER = "notExistingUser";
	private static final Map<String, String> ALL_FALSE_PERMISSIONS;
	private static final Map<String, String> ALL_TRUE_PERMISSIONS;
	private static final Map<String, String> READ_TRUE_PERMISSIONS;
	private static final Map<String, String> READ_TRUE_CVPERMISSIONS;
	private static final Map<String, String> WRITE_TRUE_CVPERMISSIONS;
	private static final Map<String, String> ALL_FALSE_CVPERMISSIONS;
	private static final Map<String, String> CHANGE_FALSE_PERMISSIONS;
	private static final Map<String, String> GLOBAL_FALSE_PERMISSIONS;
	private static final Map<String, String> GLOBAL_TRUE_PERMISSIONS;
	private static final String GLOBAL = "global";
	private static final String GLOBAL_PERMISSION = "globalpermission1";
	private static final String CATALOG1 = "catalog1";
	private static final String CATALOG2 = "catalog2";
	private static final String ONLINE_VERSION = "Online";
	private static final String STAGED_VERSION = "Staged";
	private static final List<SyncPermissionsData> SYNC_PRINCIPALS_TRUE_TARGET_ONLINE_SYNCPERMISSIONS;
	private static final List<SyncPermissionsData> SYNC_PRINCIPALS_FALSE_TARGET_ONLINE_SYNCPERMISSIONS;

	static
	{
		ALL_FALSE_PERMISSIONS = new HashMap<String, String>();
		ALL_FALSE_PERMISSIONS.put(READ, FALSE);
		ALL_FALSE_PERMISSIONS.put(CHANGE, FALSE);
		ALL_FALSE_PERMISSIONS.put(CREATE, FALSE);
		ALL_FALSE_PERMISSIONS.put(REMOVE, FALSE);
		ALL_FALSE_PERMISSIONS.put(CHANGE_PERM, FALSE);

		ALL_TRUE_PERMISSIONS = new HashMap<String, String>();
		ALL_TRUE_PERMISSIONS.put(READ, TRUE);
		ALL_TRUE_PERMISSIONS.put(CHANGE, TRUE);
		ALL_TRUE_PERMISSIONS.put(CREATE, TRUE);
		ALL_TRUE_PERMISSIONS.put(REMOVE, TRUE);
		ALL_TRUE_PERMISSIONS.put(CHANGE_PERM, TRUE);

		READ_TRUE_PERMISSIONS = new HashMap<String, String>();
		READ_TRUE_PERMISSIONS.put(READ, TRUE);
		READ_TRUE_PERMISSIONS.put(CHANGE, FALSE);
		READ_TRUE_PERMISSIONS.put(CREATE, FALSE);
		READ_TRUE_PERMISSIONS.put(REMOVE, FALSE);
		READ_TRUE_PERMISSIONS.put(CHANGE_PERM, FALSE);

		CHANGE_FALSE_PERMISSIONS = new HashMap<String, String>();
		CHANGE_FALSE_PERMISSIONS.put(READ, TRUE);
		CHANGE_FALSE_PERMISSIONS.put(CHANGE, FALSE);
		CHANGE_FALSE_PERMISSIONS.put(CREATE, TRUE);
		CHANGE_FALSE_PERMISSIONS.put(REMOVE, TRUE);
		CHANGE_FALSE_PERMISSIONS.put(CHANGE_PERM, TRUE);

		GLOBAL_TRUE_PERMISSIONS = new HashMap<String, String>();
		GLOBAL_TRUE_PERMISSIONS.put(GLOBAL_PERMISSION, TRUE);

		GLOBAL_FALSE_PERMISSIONS = new HashMap<String, String>();
		GLOBAL_FALSE_PERMISSIONS.put(GLOBAL_PERMISSION, FALSE);

		READ_TRUE_CVPERMISSIONS = new HashMap<String, String>();
		READ_TRUE_CVPERMISSIONS.put(READ, TRUE);
		READ_TRUE_CVPERMISSIONS.put(WRITE, FALSE);

		WRITE_TRUE_CVPERMISSIONS = new HashMap<String, String>();
		WRITE_TRUE_CVPERMISSIONS.put(READ, TRUE);
		WRITE_TRUE_CVPERMISSIONS.put(WRITE, TRUE);

		ALL_FALSE_CVPERMISSIONS = new HashMap<String, String>();
		ALL_FALSE_CVPERMISSIONS.put(READ, FALSE);
		ALL_FALSE_CVPERMISSIONS.put(WRITE, FALSE);

		SYNC_PRINCIPALS_TRUE_TARGET_ONLINE_SYNCPERMISSIONS = new ArrayList<>();
		final SyncPermissionsData syncPermissionsData1 = new SyncPermissionsData();
		syncPermissionsData1.setCanSynchronize(true);
		syncPermissionsData1.setTargetCatalogVersion(ONLINE_VERSION);
		SYNC_PRINCIPALS_TRUE_TARGET_ONLINE_SYNCPERMISSIONS.add(syncPermissionsData1);

		SYNC_PRINCIPALS_FALSE_TARGET_ONLINE_SYNCPERMISSIONS = new ArrayList<>();
		final SyncPermissionsData syncPermissionsData2 = new SyncPermissionsData();
		syncPermissionsData2.setCanSynchronize(false);
		syncPermissionsData2.setTargetCatalogVersion(ONLINE_VERSION);
		SYNC_PRINCIPALS_FALSE_TARGET_ONLINE_SYNCPERMISSIONS.add(syncPermissionsData2);
	}

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private PermissionManagementService permissionManagementService;

	@Resource
	private DefaultPermissionsFacade defaultPermissionsFacade;

	private final List<String> userAndOrderTypeList = Arrays.asList("User", "Order");
	private final List<String> allPermissionNameList = Arrays.asList(READ, CHANGE, CREATE, REMOVE, CHANGE_PERM);
	private final List<String> itemAttributeList = Arrays.asList(ITEM_OWNER_ATTRIBUTE, ITEM_PK_ATTRIBUTE);
	private final List<String> globalPermissionList = Arrays.asList(GLOBAL_PERMISSION);

	@Before
	public void importTestData() throws ImpExException
	{
		importData(new ClasspathImpExResource("/permissionsfacades/test/testpermissions.impex", "UTF-8"));
	}

	@Test(expected = ModelNotFoundException.class)
	public void testCalculateTypesPermissionsForWrongUserName()
	{
		//when
		defaultPermissionsFacade.calculateTypesPermissions(NOT_EXISTING_USER, userAndOrderTypeList, allPermissionNameList);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testCalculateTypesPermissionsWithWrongTypeName()
	{
		//when
		defaultPermissionsFacade.calculateTypesPermissions(ADMIN, Collections.singletonList("notExistingType"),
				allPermissionNameList);
	}

	@Test
	public void testCalculateTypesPermissionsForAdmin()
	{
		//when
		final List<PermissionsData> permissionsList = defaultPermissionsFacade.calculateTypesPermissions(ADMIN,
				userAndOrderTypeList, allPermissionNameList);

		//then
		final List<PermissionsData> expected = new ArrayList<PermissionsData>();
		expected.add(generateExpectedPermissionsData(USER_TYPECODE, ALL_TRUE_PERMISSIONS));
		expected.add(generateExpectedPermissionsData(ORDER_TYPECODE, ALL_TRUE_PERMISSIONS));

		assertPermissionsListEquals(expected, permissionsList);
	}

	@Test
	public void testCalculateTypesPermissionsForSuperGroup()
	{
		//when
		final List<PermissionsData> permissionsList = defaultPermissionsFacade.calculateTypesPermissions(SUPERGROUP,
				userAndOrderTypeList, allPermissionNameList);

		//then
		final List<PermissionsData> expected = new ArrayList<PermissionsData>();
		expected.add(generateExpectedPermissionsData(USER_TYPECODE, ALL_FALSE_PERMISSIONS));
		expected.add(generateExpectedPermissionsData(ORDER_TYPECODE, ALL_FALSE_PERMISSIONS));

		assertPermissionsListEquals(expected, permissionsList);
	}

	@Test
	public void testCalculateTypesPermissionsForUser()
	{
		//when
		final List<PermissionsData> permissionsList = defaultPermissionsFacade.calculateTypesPermissions(USER1,
				userAndOrderTypeList, allPermissionNameList);


		//then
		final List<PermissionsData> expected = new ArrayList<PermissionsData>();
		expected.add(generateExpectedPermissionsData(USER_TYPECODE, READ_TRUE_PERMISSIONS));
		expected.add(generateExpectedPermissionsData(ORDER_TYPECODE, ALL_FALSE_PERMISSIONS));

		assertPermissionsListEquals(expected, permissionsList);
	}

	@Test
	public void testCalculateTypesPermissionsForGroup()
	{
		//when
		final List<PermissionsData> permissionsList = defaultPermissionsFacade.calculateTypesPermissions(GROUP1,
				userAndOrderTypeList, allPermissionNameList);


		//then
		final List<PermissionsData> expected = new ArrayList<PermissionsData>();
		expected.add(generateExpectedPermissionsData(USER_TYPECODE, READ_TRUE_PERMISSIONS));
		expected.add(generateExpectedPermissionsData(ORDER_TYPECODE, ALL_FALSE_PERMISSIONS));

		assertPermissionsListEquals(expected, permissionsList);
	}

	@Test
	public void testCalculateTypesPermissionsForSubGroup()
	{
		//when
		final List<PermissionsData> permissionsList = defaultPermissionsFacade.calculateTypesPermissions(SUBGROUP1,
				userAndOrderTypeList, allPermissionNameList);


		//then
		final List<PermissionsData> expected = new ArrayList<PermissionsData>();
		expected.add(generateExpectedPermissionsData(USER_TYPECODE, READ_TRUE_PERMISSIONS));
		expected.add(generateExpectedPermissionsData(ORDER_TYPECODE, READ_TRUE_PERMISSIONS));

		assertPermissionsListEquals(expected, permissionsList);
	}

	@Test
	public void testCalculateTypesPermissionsForUserInheritFromGroups()
	{
		//when
		final List<PermissionsData> permissionsList = defaultPermissionsFacade.calculateTypesPermissions(USER3,
				userAndOrderTypeList, allPermissionNameList);


		//then
		final List<PermissionsData> expected = new ArrayList<PermissionsData>();
		expected.add(generateExpectedPermissionsData(USER_TYPECODE, ALL_TRUE_PERMISSIONS));
		expected.add(generateExpectedPermissionsData(ORDER_TYPECODE, ALL_FALSE_PERMISSIONS));

		assertPermissionsListEquals(expected, permissionsList);
	}

	@Test
	public void testCalculateTypesPermissionsForUserWithForbiddenChange()
	{
		//when
		final List<PermissionsData> permissionsList = defaultPermissionsFacade.calculateTypesPermissions(USER2,
				userAndOrderTypeList, allPermissionNameList);


		//then
		final List<PermissionsData> expected = new ArrayList<PermissionsData>();
		expected.add(generateExpectedPermissionsData(USER_TYPECODE, CHANGE_FALSE_PERMISSIONS));
		expected.add(generateExpectedPermissionsData(ORDER_TYPECODE, ALL_FALSE_PERMISSIONS));

		assertPermissionsListEquals(expected, permissionsList);
	}


	//CalculateAttributesPermissions tests

	@Test(expected = ModelNotFoundException.class)
	public void testCalculateAttributesPermissionsForWrongUserName()
	{
		//when
		defaultPermissionsFacade.calculateAttributesPermissions(NOT_EXISTING_USER, itemAttributeList, allPermissionNameList);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testCalculateAttributesPermissionsWithWrongTypeAttributeName()
	{
		//when
		defaultPermissionsFacade.calculateAttributesPermissions(ADMIN, Collections.singletonList("notExistingType.attributeName"),
				allPermissionNameList);
	}

	@Test
	public void testCalculateAttributesPermissionsForAdmin()
	{
		//when
		final List<PermissionsData> permissionsList = defaultPermissionsFacade.calculateAttributesPermissions(ADMIN,
				itemAttributeList, allPermissionNameList);

		//then
		final List<PermissionsData> expected = new ArrayList<PermissionsData>();
		expected.add(generateExpectedPermissionsData(ITEM_PK_ATTRIBUTE, ALL_TRUE_PERMISSIONS));
		expected.add(generateExpectedPermissionsData(ITEM_OWNER_ATTRIBUTE, ALL_TRUE_PERMISSIONS));

		assertPermissionsListEquals(expected, permissionsList);
	}

	@Test
	public void testCalculateAttributesPermissionsForSuperGroup()
	{
		//when
		final List<PermissionsData> permissionsList = defaultPermissionsFacade.calculateAttributesPermissions(SUPERGROUP,
				Collections.singletonList(ITEM_OWNER_ATTRIBUTE), allPermissionNameList);

		//then
		final List<PermissionsData> expected = new ArrayList<PermissionsData>();
		expected.add(generateExpectedPermissionsData(ITEM_OWNER_ATTRIBUTE, ALL_FALSE_PERMISSIONS));

		assertPermissionsListEquals(expected, permissionsList);
	}

	@Test
	public void testCalculateAttributesPermissionsForUser()
	{
		//when
		final List<PermissionsData> permissionsList = defaultPermissionsFacade.calculateAttributesPermissions(USER1,
				itemAttributeList, allPermissionNameList);

		//then
		final List<PermissionsData> expected = new ArrayList<PermissionsData>();
		expected.add(generateExpectedPermissionsData(ITEM_OWNER_ATTRIBUTE, READ_TRUE_PERMISSIONS));
		expected.add(generateExpectedPermissionsData(ITEM_PK_ATTRIBUTE, ALL_FALSE_PERMISSIONS));

		assertPermissionsListEquals(expected, permissionsList);
	}

	@Test
	public void testCalculateAttributesPermissionsForGroup()
	{
		//when
		final List<PermissionsData> permissionsList = defaultPermissionsFacade.calculateAttributesPermissions(GROUP1,
				itemAttributeList, allPermissionNameList);

		//then
		final List<PermissionsData> expected = new ArrayList<PermissionsData>();
		expected.add(generateExpectedPermissionsData(ITEM_OWNER_ATTRIBUTE, READ_TRUE_PERMISSIONS));
		expected.add(generateExpectedPermissionsData(ITEM_PK_ATTRIBUTE, ALL_FALSE_PERMISSIONS));

		assertPermissionsListEquals(expected, permissionsList);
	}

	@Test
	public void testCalculateAttributesPermissionsForSubGroup()
	{
		//when
		final List<PermissionsData> permissionsList = defaultPermissionsFacade.calculateAttributesPermissions(SUBGROUP1,
				itemAttributeList, allPermissionNameList);

		//then
		final List<PermissionsData> expected = new ArrayList<PermissionsData>();
		expected.add(generateExpectedPermissionsData(ITEM_OWNER_ATTRIBUTE, READ_TRUE_PERMISSIONS));
		expected.add(generateExpectedPermissionsData(ITEM_PK_ATTRIBUTE, ALL_FALSE_PERMISSIONS));

		assertPermissionsListEquals(expected, permissionsList);
	}

	@Test
	public void testCalculateAttributesPermissionsForUserInheritFromGroups()
	{
		//when
		final List<PermissionsData> permissionsList = defaultPermissionsFacade.calculateAttributesPermissions(USER3,
				itemAttributeList, allPermissionNameList);

		//then
		final List<PermissionsData> expected = new ArrayList<PermissionsData>();
		expected.add(generateExpectedPermissionsData(ITEM_OWNER_ATTRIBUTE, ALL_TRUE_PERMISSIONS));
		expected.add(generateExpectedPermissionsData(ITEM_PK_ATTRIBUTE, ALL_FALSE_PERMISSIONS));

		assertPermissionsListEquals(expected, permissionsList);
	}

	@Test
	public void testCalculateAttributesPermissionsForUserWithForbiddenChange()
	{
		//when
		final List<PermissionsData> permissionsList = defaultPermissionsFacade.calculateAttributesPermissions(USER2,
				itemAttributeList, allPermissionNameList);

		//then
		final List<PermissionsData> expected = new ArrayList<PermissionsData>();
		expected.add(generateExpectedPermissionsData(ITEM_OWNER_ATTRIBUTE, CHANGE_FALSE_PERMISSIONS));
		expected.add(generateExpectedPermissionsData(ITEM_PK_ATTRIBUTE, ALL_FALSE_PERMISSIONS));

		assertPermissionsListEquals(expected, permissionsList);
	}

	@Test
	public void testCalculateAttributesPermissionsWithWildcardInAttributeName()
	{
		//when
		final List<PermissionsData> permissionsList = defaultPermissionsFacade.calculateAttributesPermissions(USER1,
				Collections.singletonList("Item.*"), allPermissionNameList);

		//then
		final List<PermissionsData> expected = new ArrayList<PermissionsData>();
		expected.add(generateExpectedPermissionsData("Item.allDocuments", ALL_FALSE_PERMISSIONS));
		expected.add(generateExpectedPermissionsData("Item.comments", ALL_FALSE_PERMISSIONS));
		expected.add(generateExpectedPermissionsData("Item.creationtime", ALL_FALSE_PERMISSIONS));
		expected.add(generateExpectedPermissionsData("Item.itemtype", ALL_FALSE_PERMISSIONS));
		expected.add(generateExpectedPermissionsData("Item.modifiedtime", ALL_FALSE_PERMISSIONS));
		expected.add(generateExpectedPermissionsData(ITEM_OWNER_ATTRIBUTE, READ_TRUE_PERMISSIONS));
		expected.add(generateExpectedPermissionsData(ITEM_PK_ATTRIBUTE, ALL_FALSE_PERMISSIONS));
		expected.add(generateExpectedPermissionsData("Item.savedValues", ALL_FALSE_PERMISSIONS));
		expected.add(generateExpectedPermissionsData("Item.synchronizationSources", ALL_FALSE_PERMISSIONS));
		expected.add(generateExpectedPermissionsData("Item.synchronizedCopies", ALL_FALSE_PERMISSIONS));


		assertPermissionsListContainAll(expected, permissionsList);
	}

	@Test
	public void testCalculateAttributesPermissionsForUserWhoHaveAccessToType()
	{
		//when
		final List<PermissionsData> permissionsList = defaultPermissionsFacade.calculateAttributesPermissions(USER1,
				Arrays.asList(USER_OWNER_ATTRIBUTE, USER_PK_ATTRIBUTE), allPermissionNameList);

		//then
		final List<PermissionsData> expected = new ArrayList<PermissionsData>();
		expected.add(generateExpectedPermissionsData(USER_OWNER_ATTRIBUTE, READ_TRUE_PERMISSIONS));
		expected.add(generateExpectedPermissionsData(USER_PK_ATTRIBUTE, READ_TRUE_PERMISSIONS));

		assertPermissionsListEquals(expected, permissionsList);
	}

	@Test
	public void testCalculateAttributesPermissionsForGroupWhichHaveAccessToType()
	{
		//when
		final List<PermissionsData> permissionsList = defaultPermissionsFacade.calculateAttributesPermissions(GROUP1,
				Arrays.asList(USER_OWNER_ATTRIBUTE, USER_PK_ATTRIBUTE), allPermissionNameList);

		//then
		final List<PermissionsData> expected = new ArrayList<PermissionsData>();
		expected.add(generateExpectedPermissionsData(USER_OWNER_ATTRIBUTE, READ_TRUE_PERMISSIONS));
		expected.add(generateExpectedPermissionsData(USER_PK_ATTRIBUTE, READ_TRUE_PERMISSIONS));

		assertPermissionsListEquals(expected, permissionsList);
	}

	@Test
	public void testCalculateAttributesPermissionsForUserIheritAccessToType()
	{
		//when
		final List<PermissionsData> permissionsList = defaultPermissionsFacade.calculateAttributesPermissions(USER3,
				Arrays.asList(USER_OWNER_ATTRIBUTE, USER_PK_ATTRIBUTE), allPermissionNameList);

		//then
		final List<PermissionsData> expected = new ArrayList<PermissionsData>();
		expected.add(generateExpectedPermissionsData(USER_OWNER_ATTRIBUTE, ALL_TRUE_PERMISSIONS));
		expected.add(generateExpectedPermissionsData(USER_PK_ATTRIBUTE, ALL_TRUE_PERMISSIONS));

		assertPermissionsListEquals(expected, permissionsList);
	}

	//Global permissions tests

	@Test(expected = ModelNotFoundException.class)
	public void testCalculateGlobalPermissionsForWrongUserName()
	{
		//when
		defaultPermissionsFacade.calculateGlobalPermissions(NOT_EXISTING_USER, globalPermissionList);
	}

	@Test
	public void testCalculateGlobalPermissionsForAdmin()
	{
		//when
		final PermissionsData permissionsData = defaultPermissionsFacade.calculateGlobalPermissions(ADMIN, globalPermissionList);

		//then
		final PermissionsData expected = generateExpectedPermissionsData(GLOBAL, GLOBAL_TRUE_PERMISSIONS);
		Assert.assertTrue(arePermissionsDataEquals(expected, permissionsData));
	}

	@Test
	public void testCalculateGlobalPermissionsForSuperGroup()
	{
		//when
		final PermissionsData permissionsData = defaultPermissionsFacade.calculateGlobalPermissions(SUPERGROUP,
				globalPermissionList);

		//then
		final PermissionsData expected = generateExpectedPermissionsData(GLOBAL, GLOBAL_FALSE_PERMISSIONS);
		Assert.assertTrue(arePermissionsDataEquals(expected, permissionsData));
	}

	@Test
	public void testCalculateGlobalPermissionsForUser()
	{
		//given
		addGlobalPermission(USER1, GLOBAL_PERMISSION);

		//when
		final PermissionsData permissionsData = defaultPermissionsFacade.calculateGlobalPermissions(USER1, globalPermissionList);

		//then
		final PermissionsData expected = generateExpectedPermissionsData(GLOBAL, GLOBAL_TRUE_PERMISSIONS);
		Assert.assertTrue(arePermissionsDataEquals(expected, permissionsData));
	}

	@Test
	public void testCalculateGlobalPermissionsForGroup()
	{
		//given
		addGlobalPermission(GROUP1, GLOBAL_PERMISSION);

		//when
		final PermissionsData permissionsData = defaultPermissionsFacade.calculateGlobalPermissions(GROUP1, globalPermissionList);

		//then
		final PermissionsData expected = generateExpectedPermissionsData(GLOBAL, GLOBAL_TRUE_PERMISSIONS);
		Assert.assertTrue(arePermissionsDataEquals(expected, permissionsData));
	}

	@Test
	public void testCalculateGlobalPermissionsForUserInheritFromGroup()
	{
		//given
		addGlobalPermission(GROUP1, GLOBAL_PERMISSION);

		//when
		final PermissionsData permissionsData = defaultPermissionsFacade.calculateGlobalPermissions(USER3, globalPermissionList);

		//then
		final PermissionsData expected = generateExpectedPermissionsData(GLOBAL, GLOBAL_TRUE_PERMISSIONS);
		Assert.assertTrue(arePermissionsDataEquals(expected, permissionsData));
	}

	@Test
	public void testCalculateGlobalPermissionsForUserWithForbiddenAccess()
	{
		//given
		addGlobalPermission(GROUP2, GLOBAL_PERMISSION);
		denyGlobalPermission(USER2, GLOBAL_PERMISSION);

		//when
		final PermissionsData permissionsData = defaultPermissionsFacade.calculateGlobalPermissions(USER2, globalPermissionList);

		//then
		final PermissionsData expected = generateExpectedPermissionsData(GLOBAL, GLOBAL_FALSE_PERMISSIONS);
		Assert.assertTrue(arePermissionsDataEquals(expected, permissionsData));
	}

	//Catalog permissions tests

	@Test(expected = ModelNotFoundException.class)
	public void testCalculateCatalogPermissionsForWrongUserName()
	{
		//when
		defaultPermissionsFacade.calculateCatalogPermissions(NOT_EXISTING_USER, Collections.singletonList(CATALOG1),
				Arrays.asList(STAGED_VERSION, ONLINE_VERSION));
	}

	@Test
	public void testCalculateCatalogPermissionsForWrongCatalogName()
	{
		//when
		final List<CatalogPermissionsData> permissionsDataList = defaultPermissionsFacade.calculateCatalogPermissions(USER1,
				Collections.singletonList("notExisitngCatalog"), Collections.singletonList(STAGED_VERSION));

		Assert.assertTrue(permissionsDataList.isEmpty());
	}

	@Test
	public void testCalculateCatalogPermissionsForWrongCatalogVersionName()
	{
		//when
		final List<CatalogPermissionsData> permissionsDataList = defaultPermissionsFacade.calculateCatalogPermissions(USER1,
				Collections.singletonList(CATALOG1), Collections.singletonList("notExisitingCatalogVersion"));

		Assert.assertTrue(permissionsDataList.isEmpty());
	}

	@Test
	public void testCalculateCatalogPermissionsForSuperGroup()
	{
		//when
		final List<CatalogPermissionsData> permissionsDataList = defaultPermissionsFacade.calculateCatalogPermissions(SUPERGROUP,
				Collections.singletonList(CATALOG1), Arrays.asList(STAGED_VERSION, ONLINE_VERSION));

		//then
		final List<CatalogPermissionsData> expected = new ArrayList<CatalogPermissionsData>();
		expected.add(generateExpectedCatalogPermissionsData(CATALOG1, STAGED_VERSION, ALL_FALSE_CVPERMISSIONS,
				SYNC_PRINCIPALS_FALSE_TARGET_ONLINE_SYNCPERMISSIONS));
		expected.add(generateExpectedCatalogPermissionsData(CATALOG1, ONLINE_VERSION, ALL_FALSE_CVPERMISSIONS,
				Collections.emptyList()));
		assertEqualsCatalogPermissionsListData(expected, permissionsDataList);
	}

	@Test
	public void testCalculateCatalogPermissionsForUser()
	{
		//when
		final List<CatalogPermissionsData> permissionsDataList = defaultPermissionsFacade.calculateCatalogPermissions(USER1,
				Collections.singletonList(CATALOG1), Arrays.asList(STAGED_VERSION, ONLINE_VERSION));

		//then
		final List<CatalogPermissionsData> expected = new ArrayList<CatalogPermissionsData>();
		expected.add(generateExpectedCatalogPermissionsData(CATALOG1, STAGED_VERSION, READ_TRUE_CVPERMISSIONS,
				SYNC_PRINCIPALS_TRUE_TARGET_ONLINE_SYNCPERMISSIONS));
		expected.add(generateExpectedCatalogPermissionsData(CATALOG1, ONLINE_VERSION, ALL_FALSE_CVPERMISSIONS,
				Collections.emptyList()));
		assertEqualsCatalogPermissionsListData(expected, permissionsDataList);
	}

	@Test
	public void testCalculateCatalogPermissionsForGroup()
	{
		//when
		final List<CatalogPermissionsData> permissionsDataList = defaultPermissionsFacade.calculateCatalogPermissions(GROUP2,
				Collections.singletonList(CATALOG1), Arrays.asList(STAGED_VERSION, ONLINE_VERSION));

		//then
		final List<CatalogPermissionsData> expected = new ArrayList<CatalogPermissionsData>();
		expected.add(generateExpectedCatalogPermissionsData(CATALOG1, STAGED_VERSION, WRITE_TRUE_CVPERMISSIONS,
				SYNC_PRINCIPALS_FALSE_TARGET_ONLINE_SYNCPERMISSIONS));
		expected.add(generateExpectedCatalogPermissionsData(CATALOG1, ONLINE_VERSION, ALL_FALSE_CVPERMISSIONS,
				Collections.emptyList()));
		assertEqualsCatalogPermissionsListData(expected, permissionsDataList);
	}

	@Test
	public void testCalculateCatalogPermissionsForUserInheritFromGroup()
	{
		//when
		final List<CatalogPermissionsData> permissionsDataList = defaultPermissionsFacade.calculateCatalogPermissions(USER3,
				Arrays.asList(CATALOG1, CATALOG2), Arrays.asList(STAGED_VERSION, ONLINE_VERSION));

		//then
		final List<CatalogPermissionsData> expected = new ArrayList<CatalogPermissionsData>();
		expected.add(generateExpectedCatalogPermissionsData(CATALOG1, STAGED_VERSION, WRITE_TRUE_CVPERMISSIONS,
				SYNC_PRINCIPALS_TRUE_TARGET_ONLINE_SYNCPERMISSIONS));
		expected.add(generateExpectedCatalogPermissionsData(CATALOG1, ONLINE_VERSION, ALL_FALSE_CVPERMISSIONS,
				Collections.emptyList()));
		expected.add(generateExpectedCatalogPermissionsData(CATALOG2, ONLINE_VERSION, READ_TRUE_CVPERMISSIONS,
				Collections.emptyList()));
		assertEqualsCatalogPermissionsListData(expected, permissionsDataList);
	}

	@Test
	public void testCalculateCatalogPermissionsDoesNotThrowNullPointerExceptionIfNoSyncJobs()
	{
		// when
		boolean npeException = false;
		try
		{
			defaultPermissionsFacade
					.calculateCatalogPermissions(GROUP1, Arrays.asList(CATALOG2), Arrays.asList(STAGED_VERSION, ONLINE_VERSION));
		}
		catch (final NullPointerException e)
		{
			npeException = true;
		}

		// then
		assertFalse("Should not throw Null Pointer Exception If No Sync Jobs provided", npeException);
	}

	protected PermissionsData generateExpectedPermissionsData(final String id, final Map<String, String> permissions)
	{
		final PermissionsData permissionsData = new PermissionsData();
		permissionsData.setId(id);
		permissionsData.setPermissions(permissions);
		return permissionsData;
	}

	protected CatalogPermissionsData generateExpectedCatalogPermissionsData(final String catalogId, final String catalogVersion,
			final Map<String, String> permissions, final List<SyncPermissionsData> syncPermissions)
	{
		final CatalogPermissionsData permissionsData = new CatalogPermissionsData();
		permissionsData.setCatalogId(catalogId);
		permissionsData.setCatalogVersion(catalogVersion);
		permissionsData.setPermissions(permissions);
		permissionsData.setSyncPermissions(syncPermissions);
		return permissionsData;
	}


	protected void assertEqualsCatalogPermissionsListData(final List<CatalogPermissionsData> expected,
			final List<CatalogPermissionsData> tested)
	{
		Assert.assertEquals("Permissions list size is not equal", expected.size(), tested.size());
		expected.stream().forEach(
				p -> Assert.assertTrue(
						"Permissions not equal : " + p.getCatalogId() + "," + p.getCatalogVersion() + "," + p.getPermissions() + "," + p
								.getSyncPermissions(), tested
								.stream()//
								.filter(t -> areCatalogPermissionsDataEquals(p, t))//
								.findAny()//
								.isPresent()));
	}

	protected boolean areCatalogPermissionsDataEquals(final CatalogPermissionsData expected, final CatalogPermissionsData tested)
	{
		return expected.getCatalogId().equals(tested.getCatalogId())
				&& expected.getCatalogVersion().equals(tested.getCatalogVersion())
				&& expected.getPermissions().equals(tested.getPermissions())
				&& SetUtils.isEqualSet(Sets.newHashSet(expected.getSyncPermissions()), Sets.newHashSet(tested.getSyncPermissions()));
	}

	protected void assertPermissionsListEquals(final List<PermissionsData> expected, final List<PermissionsData> tested)
	{
		Assert.assertEquals("Permissions list size is not equal", expected.size(), tested.size());
		assertPermissionsListContainAll(expected, tested);
	}

	protected void assertPermissionsListContainAll(final List<PermissionsData> expected, final List<PermissionsData> tested)
	{
		expected.stream().forEach(
				p -> Assert.assertTrue("Permissions not equal : " + p.getId() + "," + p.getPermissions(), tested.stream()//
						.filter(t -> arePermissionsDataEquals(p, t))//
						.findAny()//
						.isPresent()));
	}

	protected boolean arePermissionsDataEquals(final PermissionsData expected, final PermissionsData tested)
	{
		return expected.getId().equals(tested.getId()) && expected.getPermissions().equals(tested.getPermissions());
	}

	protected void addGlobalPermission(final String principalId, final String permission)
	{
		insertGlobalPermission(principalId, permission, false);
	}

	protected void denyGlobalPermission(final String principalId, final String permission)
	{
		insertGlobalPermission(principalId, permission, true);
	}

	protected void insertGlobalPermission(final String principalId, final String permission, final boolean denied)
	{
		final PrincipalModel example = new PrincipalModel();
		example.setUid(principalId);
		final PrincipalModel principal = flexibleSearchService.getModelByExample(example);
		permissionManagementService.addGlobalPermission(new PermissionAssignment(permission, principal, denied));
	}
}

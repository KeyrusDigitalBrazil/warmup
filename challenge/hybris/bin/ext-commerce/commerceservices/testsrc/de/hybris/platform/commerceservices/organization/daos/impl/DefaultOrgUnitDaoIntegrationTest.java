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
package de.hybris.platform.commerceservices.organization.daos.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.commerceservices.model.OrgUnitModel;
import de.hybris.platform.commerceservices.organization.services.impl.DefaultOrgUnitHierarchyService;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.util.CommerceSearchUtils;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;


/**
 * JUnit test suite for {@link DefaultOrgUnitDao}
 */
@IntegrationTest
public class DefaultOrgUnitDaoIntegrationTest extends BaseCommerceBaseTest
{
	private static final String NA_UNIT_UID = "northAmerica";
	private static final String USA_UNIT_UID = "usa";
	private static final String CA_UNIT_UID = "canada";
	private static final String MT_UNIT_UID = "montreal";
	private static final String CAL_UNIT_UID = "california";
	private static final String AF_UNIT_UID = "africa";
	private static final String NI_UNIT_UID = "nigeria";
	private static final String[] unitUids =
	{ USA_UNIT_UID, CA_UNIT_UID };


	@Resource
	private DefaultOrgUnitDao defaultOrgUnitDao;

	@Resource(name = "defaultOrgUnitHierarchyService")
	private DefaultOrgUnitHierarchyService defaultOrgUnitHierarchyService;

	@Resource(name = "userService")
	private UserService userService;

	private List<OrgUnitModel> unitList;

	@Before
	public void setUp() throws Exception
	{
		ServicelayerTest.createCoreData();
		importCsv("/impex/essentialdata_usergroups.impex", "UTF-8");
		importCsv("/commerceservices/test/orgUnitTestData.impex", "UTF-8");

		unitList = new ArrayList<>();
		unitList.add(userService.getUserGroupForUID(NA_UNIT_UID, OrgUnitModel.class));
		unitList.add(userService.getUserGroupForUID(AF_UNIT_UID, OrgUnitModel.class));
	}

	@Test
	public void shouldFindAllUnits()
	{
		// generate path
		userService.setCurrentUser(userService.getAdminUser());
		defaultOrgUnitHierarchyService.generateUnitPaths(OrgUnitModel.class);

		final Set<String> uids = Sets.newSet(NA_UNIT_UID, USA_UNIT_UID, CA_UNIT_UID, MT_UNIT_UID, CAL_UNIT_UID, AF_UNIT_UID,
				NI_UNIT_UID);
		final SearchPageData<OrgUnitModel> unitsPageData = defaultOrgUnitDao.findAllUnits(unitList,
				CommerceSearchUtils.getAllOnOnePagePageableData());
		Assert.assertNotNull("unitsPageData", unitsPageData);
		Assert.assertNotNull("unitsPageData", unitsPageData.getResults());
		Assert.assertEquals("unitsPageData size", uids.size(), unitsPageData.getResults().size());
		final Set<String> uidSet = unitsPageData.getResults().stream().map(OrgUnitModel::getUid).collect(Collectors.toSet());
		for (final String uid : uids)
		{
			Assert.assertTrue("uid is wrong:" + uid, uidSet.contains(uid));
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotFindAllUnitsIfUnitIsNull()
	{
		defaultOrgUnitDao.findAllUnits(null, CommerceSearchUtils.getAllOnOnePagePageableData());
	}

	@Test
	public void shouldNotFindAllUnitsIfUnitIsEmpty()
	{
		final SearchPageData<OrgUnitModel> unitsPageData = defaultOrgUnitDao.findAllUnits(Collections.emptyList(),
				CommerceSearchUtils.getAllOnOnePagePageableData());
		Assert.assertNotNull("unitsPageData", unitsPageData);
		Assert.assertNotNull("unitsPageData", unitsPageData.getResults());
		Assert.assertEquals("unitsPageData size", 0, unitsPageData.getResults().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotFindAllUnitsIfPageableDataIsNull()
	{
		defaultOrgUnitDao.findAllUnits(unitList, null);
	}

	@Test
	public void shouldFindMembersOfType()
	{
		final SearchPageData<OrgUnitModel> unitsPageData = defaultOrgUnitDao.findMembersOfType(OrgUnitModel.class,
				CommerceSearchUtils.getAllOnOnePagePageableData(), unitUids);
		Assert.assertNotNull("unitsPageData", unitsPageData);
		Assert.assertNotNull("unitsPageData", unitsPageData.getResults());
		Assert.assertEquals("unitsPageData size", 2, unitsPageData.getResults().size());
		final Set<String> orgUnitUids = Sets.newSet(CAL_UNIT_UID, MT_UNIT_UID);
		final Set<String> uidSet = unitsPageData.getResults().stream().map(OrgUnitModel::getUid).collect(Collectors.toSet());
		for (final String uid : orgUnitUids)
		{
			Assert.assertTrue("uid is wrong:" + uid, uidSet.contains(uid));
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNoteFindMembersOfTypeIfUnitsIsNull()
	{
		final String[] nullUids = null; // need this, otherwise get ambiguous type error
		defaultOrgUnitDao.findMembersOfType(OrgUnitModel.class, CommerceSearchUtils.getAllOnOnePagePageableData(), nullUids);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNoteFindMembersOfTypeIfMemberTypeIsNull()
	{
		defaultOrgUnitDao.findMembersOfType(null, CommerceSearchUtils.getAllOnOnePagePageableData(), unitUids);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNoteFindMembersOfTypeIfPageableDataIsNull()
	{
		defaultOrgUnitDao.findMembersOfType(OrgUnitModel.class, null, unitUids);
	}

}

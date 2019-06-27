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
package de.hybris.platform.commerceservices.organization.utils;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.commerceservices.model.OrgUnitModel;
import de.hybris.platform.commerceservices.organization.services.OrgUnitMemberParameter;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.util.Config;

import java.util.HashSet;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * The integration test for {@link OrgUtils}.
 */
@IntegrationTest
public class OrgUtilsTest extends BaseCommerceBaseTest
{
	private String orgRolesBackUp;

	@Before
	public void setUp() throws Exception
	{
		// Temporarily change organization related authorization properties
		orgRolesBackUp = Config.getString(CommerceServicesConstants.ORGANIZATION_ROLES, null);
	}

	@After
	public void cleanUp() throws Exception
	{
		Config.setParameter(CommerceServicesConstants.ORGANIZATION_ROLES, orgRolesBackUp);
	}

	@Test
	public void shouldGetRoleUids()
	{
		Config.setParameter(CommerceServicesConstants.ORGANIZATION_ROLES,
				"salesemployeegroup,salesmanagergroup,salesadmingroup,salesapprovergroup");

		final List<String> roleUids = OrgUtils.getRoleUids();
		Assert.assertNotNull("roleUids", roleUids);
		Assert.assertEquals("size of roleUids", 4, roleUids.size());
		Assert.assertEquals("salesemployeegroup", "salesemployeegroup", roleUids.get(0));
		Assert.assertEquals("salesmanagergroup", "salesmanagergroup", roleUids.get(1));
		Assert.assertEquals("salesadmingroup", "salesadmingroup", roleUids.get(2));
		Assert.assertEquals("salesapprovergroup", "salesapprovergroup", roleUids.get(3));
	}

	@Test
	public void shouldCreateOrgUnitMemberParameter()
	{
		final String uid = "uid";
		final HashSet<OrgUnitModel> members = new HashSet<>();
		final PageableData pageableData = new PageableData();
		final Class<OrgUnitModel> type = OrgUnitModel.class;
		final OrgUnitMemberParameter<OrgUnitModel> params = OrgUtils.createOrgUnitMemberParameter(uid, members, type, pageableData);
		Assert.assertEquals("uid", uid, params.getUid());
		Assert.assertEquals("members", members, params.getMembers());
		Assert.assertEquals("type", type, params.getType());
		Assert.assertEquals("pageableData", pageableData, params.getPageableData());
	}
}

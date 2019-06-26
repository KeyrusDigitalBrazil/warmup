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
package de.hybris.platform.b2b.services.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.B2BIntegrationTest;
import de.hybris.platform.b2b.dao.impl.BaseDao;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.ServicelayerTest;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultB2BApproverServiceIntegrationTest extends ServicelayerTest
{
	private static final String CUSTOMER_K = "customer.k@rustic-hw.com";

	private static final String CUSTOMER_C = "GC Sales US Boss";

	@Resource
	private DefaultB2BApproverService defaultB2BApproverService;

	@Resource
	private BaseDao baseDao;

	private static final String CUSTOM_RETAIL = "GC Sales US";

	@Before
	public void setup() throws Exception
	{
		B2BIntegrationTest.loadTestData();
		importCsv("/b2bapprovalprocess/test/b2borganizations.csv", "UTF-8");
		importCsv("/b2bapprovalprocess/test/usergroups.impex", "UTF-8");
	}

	@Test
	public void shouldAddApproverToUnit()
	{
		final B2BUnitModel unit = baseDao.findUniqueByAttribute(B2BUnitModel.UID, CUSTOM_RETAIL, B2BUnitModel.class);
		final B2BCustomerModel customer = baseDao.findUniqueByAttribute(B2BCustomerModel.UID, CUSTOMER_K, B2BCustomerModel.class);
		assertNotNull(unit);
		assertNotNull(customer);
		assertFalse(unit.getApprovers().contains(customer));
		defaultB2BApproverService.addApproverToUnit(CUSTOM_RETAIL, CUSTOMER_K);
		assertTrue(unit.getApprovers().contains(customer));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAddApproverToUnitWithEmptyUserId()
	{
		defaultB2BApproverService.addApproverToUnit(CUSTOM_RETAIL, StringUtils.EMPTY);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAddApproverToUnitWithEmptyUnitId()
	{
		defaultB2BApproverService.addApproverToUnit(StringUtils.EMPTY, CUSTOMER_K);
	}

	@Test
	public void shouldRemoveApproverFromUnit()
	{
		final B2BUnitModel unit = baseDao.findUniqueByAttribute(B2BUnitModel.UID, CUSTOM_RETAIL, B2BUnitModel.class);
		final B2BCustomerModel customer = baseDao.findUniqueByAttribute(B2BCustomerModel.UID, CUSTOMER_C, B2BCustomerModel.class);
		assertNotNull(unit);
		assertNotNull(customer);
		assertTrue(unit.getApprovers().contains(customer));
		defaultB2BApproverService.removeApproverFromUnit(CUSTOM_RETAIL, CUSTOMER_C);
		assertFalse(unit.getApprovers().contains(customer));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotRemoveApproverFromUnitWithEmptyUserId()
	{
		defaultB2BApproverService.removeApproverFromUnit(CUSTOM_RETAIL, StringUtils.EMPTY);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotRemoveApproverFromUnitWithEmptyUnitId()
	{
		defaultB2BApproverService.removeApproverFromUnit(StringUtils.EMPTY, CUSTOMER_K);
	}

}

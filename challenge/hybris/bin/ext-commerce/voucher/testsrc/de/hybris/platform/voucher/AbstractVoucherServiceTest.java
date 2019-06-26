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
package de.hybris.platform.voucher;

import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

import org.junit.Before;


@SuppressWarnings("PMD")
public abstract class AbstractVoucherServiceTest extends ServicelayerTransactionalTest
{

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultUsers();
		createHardwareCatalog();
		importCsv("/test/voucherServiceTestData.csv", "windows-1252");
	}

}

/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.warehousing.replacement;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commerceservices.stock.dao.impl.DefaultCommerceWarehouseDaoTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.warehousing.constants.WarehousingTestConstants;


/**
 * Re-implements test {@link DefaultCommerceWarehouseDaoTest} to provide missing information required when warehousing extensions is present
 */
@IntegrationTest(replaces = DefaultCommerceWarehouseDaoTest.class)
public class OrderManagementCommerceWarehouseDaoTest extends DefaultCommerceWarehouseDaoTest
{
	@Override
	public void setUp() throws ImpExException
	{
		super.setUp();
		insertExtraInformation();
	}

	/**
	 * Import impex during setup to add relation between warehouse and delivery mode as well as the default ATP formula for the used basestore.
	 *
	 * @throws ImpExException
	 */
	private void insertExtraInformation() throws ImpExException
	{
		importCsv("/warehousing/test/impex/replacement/replacement-add-formula-teststore.impex", WarehousingTestConstants.ENCODING);
	}

}

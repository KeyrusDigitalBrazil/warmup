/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.basket.backend.impl.erp;

import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class BasketERPTest extends SapordermanagmentBolSpringJunitTest
{

	private BasketERP classUnderTest;

	@Test
	public void testBeanInstantiation()
	{

		classUnderTest = genericFactory.getBean(SapordermgmtbolConstants.BEAN_ID_BE_CART_ERP);

		assertNotNull(classUnderTest.getAdditionalPricing());

	}


}

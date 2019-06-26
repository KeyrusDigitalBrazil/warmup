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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy;

import static org.junit.Assert.assertNotNull;

import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import org.junit.Test;


@SuppressWarnings("javadoc")
public class HeaderTextMapperTest extends SapordermanagmentBolSpringJunitTest
{

	@Test
	public void testBeanInitializtion()
	{
		final HeadTextMapper cut = (HeadTextMapper) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_HEADER_TEXT_MAPPER);
		assertNotNull(cut);
	}


}

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
package de.hybris.platform.sap.productconfig.frontend.util.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;

import org.junit.Test;


@UnitTest
public class ConstantHandlerTest
{
	@Test
	public void testGeneralGroupName()
	{
		assertEquals(InstanceModel.GENERAL_GROUP_NAME, ConstantHandler.getGeneralGroupName());
	}

}

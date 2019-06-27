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
/**
 * 
 */
package com.hybris.ymkt.segmentation.handlers;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.testframework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.ymkt.segmentation.model.CMSYmktCampaignRestrictionModel;


/**
 * 
 */
@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class CampaignRestrictionDescriptionHandlerTest
{

	private static CampaignRestrictionDescriptionHandler handler = new CampaignRestrictionDescriptionHandler();
	private static CMSYmktCampaignRestrictionModel model = new CMSYmktCampaignRestrictionModel();

	@Before
	public void setUp() throws Exception
	{
	}

	@Test
	public void getDescriptionTest_noText()
	{
		Assert.assertEquals("SAP Marketing Campaign Restriction", handler.get(model));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void setTest()
	{
		handler.set(model, "");
	}

}

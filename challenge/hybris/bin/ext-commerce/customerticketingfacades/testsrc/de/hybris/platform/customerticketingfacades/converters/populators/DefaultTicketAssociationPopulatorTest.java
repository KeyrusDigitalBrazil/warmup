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
package de.hybris.platform.customerticketingfacades.converters.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.customerticketingfacades.data.TicketAssociatedData;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Test cases for DefaultTicketAssociationPopulator class.
 */
@UnitTest
public class DefaultTicketAssociationPopulatorTest
{

	private DefaultTicketAssociationPopulator populator;

	@Mock
	private AbstractOrderModel abstractOrderModel;

	@Mock
	private BaseSiteModel baseSiteModel;

	/**
	 * Test setup.
	 */
	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		populator = new DefaultTicketAssociationPopulator();
	}

	/**
	 * Test should populate the TicketAssociatedData when site value is set.
	 */
	@Test
	public void shouldPopulateTicketAssociatedDataWithSiteValue()
	{
		final String code = "code";
		final String typeOrder = "Order";
		final Date date = new Date();
		final String uid = "UID";
		Mockito.when(abstractOrderModel.getCode()).thenReturn(code);
		Mockito.when(abstractOrderModel.getItemtype()).thenReturn(typeOrder);
		Mockito.when(abstractOrderModel.getModifiedtime()).thenReturn(date);
		Mockito.when(abstractOrderModel.getSite()).thenReturn(baseSiteModel);

		Mockito.when(baseSiteModel.getUid()).thenReturn(uid);

		final TicketAssociatedData data = new TicketAssociatedData();

		populator.populate(abstractOrderModel, data);

		Assert.assertEquals(code, data.getCode());
		Assert.assertEquals(date, data.getModifiedtime());
		Assert.assertEquals(uid, data.getSiteUid());
	}

	/**
	 * Test should populate the TicketAssociatedData when site value is not set.
	 */
	@Test
	public void shouldPopulateTicketAssociatedDataWithNoSiteValue()
	{
		final String code = "code";
		final String typeOrder = "Order";
		final Date date = new Date();
		Mockito.when(abstractOrderModel.getCode()).thenReturn(code);
		Mockito.when(abstractOrderModel.getItemtype()).thenReturn(typeOrder);
		Mockito.when(abstractOrderModel.getModifiedtime()).thenReturn(date);
		Mockito.when(abstractOrderModel.getSite()).thenReturn(null);

		final TicketAssociatedData data = new TicketAssociatedData();

		populator.populate(abstractOrderModel, data);

		Assert.assertEquals(code, data.getCode());
		Assert.assertEquals(date, data.getModifiedtime());
		Assert.assertNull(data.getSiteUid());
	}
}

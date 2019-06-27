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
package de.hybris.platform.customerticketingc4cb2bintegration.converters.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bcommercefacades.company.B2BUserFacade;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.customerticketingc4cintegration.SitePropsHolder;
import de.hybris.platform.customerticketingc4cintegration.data.ServiceRequestData;
import de.hybris.platform.customerticketingfacades.data.TicketData;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Test cases for {@link Y2C4CB2BTicketPopulator}.
 */
@UnitTest
public class Y2C4CB2BTicketPopulatorTest
{
	@InjectMocks
	private Y2C4CB2BTicketPopulator populator;
	@Mock
	private B2BUserFacade b2bUserFacade;
	@Mock
	private CustomerFacade customerFacade;
	@Mock
	private SitePropsHolder sitePropsHolder;

	private static final String CUSTOMER_ID = "customerId";

	/**
	 * Setup.
	 */
	@Before
	public void setup()
	{
		populator = new Y2C4CB2BTicketPopulator();
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Should populate the customer id when less than 10 length.
	 */
	@Test
	public void shouldPopulateWhenCustomerExternalIdLessThan10Length()
	{
		Mockito.when(sitePropsHolder.isB2C()).thenReturn(Boolean.FALSE);
		final TicketData ticketData = new TicketData();
		ticketData.setCustomerId(CUSTOMER_ID);
		final ServiceRequestData serviceRequestData = new ServiceRequestData();

		final B2BUnitData b2BUnitData = new B2BUnitData();
		b2BUnitData.setUid("less10");

		Mockito.when(customerFacade.getCurrentCustomerUid()).thenReturn("id");

		Mockito.when(b2bUserFacade.getParentUnitForCustomer("id")).thenReturn(b2BUnitData);

		populator.populate(ticketData, serviceRequestData);

		Mockito.verify(b2bUserFacade, Mockito.only()).getParentUnitForCustomer(Mockito.anyString());
		Assert.assertEquals(CUSTOMER_ID, serviceRequestData.getExternalContactID());
		Assert.assertEquals("less10", serviceRequestData.getExternalCustomerID());
	}

	/**
	 * Should populate the customer id when equals 10 length.
	 */
	@Test
	public void shouldPopulateWhenCustomerExternalIdEqualTo10Length()
	{
		Mockito.when(sitePropsHolder.isB2C()).thenReturn(Boolean.FALSE);
		final TicketData ticketData = new TicketData();
		ticketData.setCustomerId(CUSTOMER_ID);
		final ServiceRequestData serviceRequestData = new ServiceRequestData();

		final B2BUnitData b2BUnitData = new B2BUnitData();
		b2BUnitData.setUid("0123456789");

		Mockito.when(customerFacade.getCurrentCustomerUid()).thenReturn("id");

		Mockito.when(b2bUserFacade.getParentUnitForCustomer("id")).thenReturn(b2BUnitData);

		populator.populate(ticketData, serviceRequestData);

		Mockito.verify(b2bUserFacade, Mockito.only()).getParentUnitForCustomer(Mockito.anyString());
		Assert.assertEquals(CUSTOMER_ID, serviceRequestData.getExternalContactID());
		Assert.assertEquals("0123456789", serviceRequestData.getExternalCustomerID());
	}

	/**
	 * Should populate the customer id when more than 10 length.
	 */
	@Test
	public void shouldPopulateWhenCustomerExternalIdMoreThan10Length()
	{
		Mockito.when(sitePropsHolder.isB2C()).thenReturn(Boolean.FALSE);
		final TicketData ticketData = new TicketData();
		ticketData.setCustomerId(CUSTOMER_ID);
		final ServiceRequestData serviceRequestData = new ServiceRequestData();

		final B2BUnitData b2BUnitData = new B2BUnitData();
		b2BUnitData.setUid("MoreThan10Length");

		Mockito.when(customerFacade.getCurrentCustomerUid()).thenReturn("id");

		Mockito.when(b2bUserFacade.getParentUnitForCustomer("id")).thenReturn(b2BUnitData);

		populator.populate(ticketData, serviceRequestData);

		Mockito.verify(b2bUserFacade, Mockito.only()).getParentUnitForCustomer(Mockito.anyString());
		Assert.assertEquals(CUSTOMER_ID, serviceRequestData.getExternalContactID());
		Assert.assertEquals("MoreThan10", serviceRequestData.getExternalCustomerID());
	}

	/**
	 * Should not populate the customer id when is B2C.
	 */
	@Test
	public void shouldNotPopulateB2C()
	{
		Mockito.when(sitePropsHolder.isB2C()).thenReturn(Boolean.TRUE);
		final TicketData ticketData = new TicketData();
		final ServiceRequestData serviceRequestData = new ServiceRequestData();
		populator.populate(ticketData, serviceRequestData);

		Mockito.verify(b2bUserFacade, Mockito.never()).getParentUnitForCustomer(Mockito.anyString());
		Assert.assertNull(serviceRequestData.getExternalCustomerID());
	}
}

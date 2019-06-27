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
package com.hybris.cis.client.rest.tax.mock;

import com.hybris.cis.client.mock.AvsClientMock;
import com.hybris.cis.client.mock.TaxClientMock;
import com.hybris.cis.client.shared.models.CisAddress;
import com.hybris.cis.client.shared.models.CisAddressType;
import com.hybris.cis.client.shared.models.CisDecision;
import com.hybris.cis.client.shared.models.CisLineItem;
import com.hybris.cis.client.shared.models.CisOrder;
import com.hybris.cis.client.tax.models.CisTaxDoc;
import com.hybris.cis.client.tax.util.MockTaxUtils;
import de.hybris.platform.servicelayer.ServicelayerTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static junit.framework.Assert.assertEquals;


/**
 * Validates that the "out-of-the-box" spring configuration will wire in the mock client if mock mode is set.
 */
public class TaxMockClientTest extends ServicelayerTest
{
	private static final String CLIENT_ID = "TEST-ID";
	private static final String TENANT_ID = "single";

	private TaxClientMock taxClientMock;
	private MockTaxUtils mockTaxUtils;

	private CisOrder usOrder;
	private CisTaxDoc cisTaxDoc;


	@Before
	public void before() throws Exception // NOPMD
	{
		this.usOrder = createSampleOrder();

		taxClientMock = new TaxClientMock();
		mockTaxUtils = new MockTaxUtils();
		taxClientMock.setTaxUtils(mockTaxUtils);
	}

	private CisOrder createSampleOrder()
	{
		final CisOrder usOrder = new CisOrder();
		final CisAddress shipToAddress = newAddress("1700 Broadway", "New York", "NY", "10019", "US");
		shipToAddress.setType(CisAddressType.SHIP_TO);

		final CisAddress originAddress = newAddress("1295 Charleston Rd", "Mountain View", "CA", "94043-1307", "US");
		originAddress.setType(CisAddressType.SHIP_FROM);

		final CisAddress acceptanceAddress = newAddress("2-24 29th St", "Fair Lawn", "NJ", "07410-3948", "US");
		acceptanceAddress.setType(CisAddressType.ADMIN_ORIGIN);

		final CisAddress billToAddress = newAddress("1700 Broadway", "New York", "NY", "10019", "US");
		billToAddress.setFirstName("TestFirstName");
		billToAddress.setLastName("TestLastName");
		billToAddress.setEmail("test@123.com");
		billToAddress.setType(CisAddressType.BILL_TO);

		final List<CisLineItem> lineItems = new ArrayList<CisLineItem>();
		final CisLineItem lineItem = new CisLineItem();
		lineItem.setId(new Integer(12));
		lineItem.setItemCode("100");
		lineItem.setQuantity(new Integer(1));
		lineItem.setUnitPrice(BigDecimal.TEN);
		lineItem.setProductDescription("Test item");
		lineItems.add(lineItem);

		final CisLineItem lineItem2 = new CisLineItem();
		lineItem2.setId(new Integer(34));
		lineItem2.setItemCode("200");
		lineItem2.setQuantity(new Integer(2));
		lineItem2.setUnitPrice(BigDecimal.TEN);
		lineItem2.setProductDescription("Test item");
		lineItems.add(lineItem2);

		usOrder.setId("UT" + new Date().getTime() + new Random(new Date().getTime()).nextInt(100));
		usOrder.getAddresses().add(shipToAddress);
		usOrder.getAddresses().add(originAddress);
		usOrder.getAddresses().add(acceptanceAddress);
		usOrder.getAddresses().add(billToAddress);
		usOrder.setLineItems(lineItems);

		usOrder.setCurrency("USD");
		return usOrder;

	}

	private CisAddress newAddress(final String addressLine1, final String city, final String state, final String zip,
			final String country)
	{
		final CisAddress address = new CisAddress();
		address.setAddressLine1(addressLine1);
		address.setAddressLine2("line2");
		address.setCity(city);
		address.setState(state);
		address.setZipCode(zip);
		address.setCountry(country);
		return address;
	}

	@Test
	public void shouldPingSuccess()
	{
		assertEquals(Response.Status.CREATED, taxClientMock.doPing(CLIENT_ID, TENANT_ID).status());
	}

	@Test
	public void shouldPingFail()
	{
		assertEquals(Response.Status.FORBIDDEN, taxClientMock.doPing(AvsClientMock.PING_FAIL, TENANT_ID).status());
	}

	@Test
	public void shouldQuoteTax()
	{
		cisTaxDoc = taxClientMock.quote(CLIENT_ID, TENANT_ID, this.usOrder);
		Assert.assertEquals(CisDecision.ACCEPT, cisTaxDoc.getDecision());
		Assert.assertNotNull(cisTaxDoc.getId());
	}

	@Test
	public void shouldSubmitTax()
	{
		cisTaxDoc = taxClientMock.post(CLIENT_ID, TENANT_ID, this.usOrder);
		Assert.assertEquals(CisDecision.ACCEPT, cisTaxDoc.getDecision());
	}

	@Test
	public void shouldInvoiceTax()
	{
		cisTaxDoc = taxClientMock.invoice(CLIENT_ID, TENANT_ID, this.usOrder);
		Assert.assertEquals(CisDecision.ACCEPT, cisTaxDoc.getDecision());
	}

}

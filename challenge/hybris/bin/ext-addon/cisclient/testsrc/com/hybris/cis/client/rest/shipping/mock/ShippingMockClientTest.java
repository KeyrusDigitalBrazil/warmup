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
 *
 */
package com.hybris.cis.client.rest.shipping.mock;

import com.hybris.cis.client.mock.AvsClientMock;
import com.hybris.cis.client.mock.ShippingClientMock;
import com.hybris.cis.client.shared.models.CisAddress;
import com.hybris.cis.client.shared.models.CisAddressType;
import com.hybris.cis.client.shipping.models.CisPackage;
import com.hybris.cis.client.shipping.models.CisShipment;
import com.hybris.cis.client.shipping.models.CisWeightUnitsType;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.util.Date;

import static junit.framework.Assert.assertEquals;


/**
 * Validates that the "out-of-the-box" spring configuration will wire in the mock client if mock mode is set.
 */
@IntegrationTest
public class ShippingMockClientTest extends ServicelayerTest
{
	private static final String CLIENT_ID = "TEST-ID";
	private static final String TENANT_ID = "single";

	private ShippingClientMock shippingClientMock;

	private CisShipment cisShipment;

	@Before
	public void setUp()
	{
		shippingClientMock = new ShippingClientMock();
	}

	@Test
	public void shouldPingSuccess()
	{
		assertEquals(Response.Status.CREATED, shippingClientMock.doPing(CLIENT_ID, TENANT_ID).status());
	}

	@Test
	public void shouldPingFail()
	{
		assertEquals(Response.Status.FORBIDDEN, shippingClientMock.doPing(AvsClientMock.PING_FAIL, TENANT_ID).status());
	}

	@Before
	public void initShipment()
	{
		this.cisShipment = new CisShipment();
		final CisAddress originAddress = new CisAddress();
		originAddress.setFirstName("firstName");
		originAddress.setLastName("lastName");
		originAddress.setPhone("1234567890");
		originAddress.setCompany("ABC");
		originAddress.setAddressLine1("502 MAIN ST N");
		originAddress.setZipCode("H2B1A0");
		originAddress.setCity("MONTREAL");
		originAddress.setState("QC");
		originAddress.setCountry("CA");
		originAddress.setType(CisAddressType.SHIP_FROM);
		this.cisShipment.getAddresses().add(originAddress);

		final CisAddress destAddress = new CisAddress();
		destAddress.setFirstName("firstName");
		destAddress.setLastName("lastName");
		destAddress.setPhone("1234567890");
		destAddress.setCompany("hybris");
		destAddress.setAddressLine1("502 MAIN ST N");
		destAddress.setZipCode("H2B1A0");
		destAddress.setCity("MONTREAL");
		destAddress.setState("QC");
		destAddress.setCountry("CA");
		destAddress.setType(CisAddressType.SHIP_TO);
		this.cisShipment.getAddresses().add(destAddress);

		this.cisShipment.setPackage(new CisPackage());
		this.cisShipment.getPackage().setInsuredValue("100");
		this.cisShipment.getPackage().setWidth("1");
		this.cisShipment.getPackage().setLength("1");
		this.cisShipment.getPackage().setHeight("1");
		this.cisShipment.getPackage().setUnit("cm");
		this.cisShipment.getPackage().setWeight("15");
		this.cisShipment.getPackage().setWeightUnit(CisWeightUnitsType.KG);

		this.cisShipment.setShipDate(new Date());

		this.cisShipment.setServiceMethod("DOM.EP");

	}

	@Test
	public void shouldCreateShipment()
	{
		cisShipment = shippingClientMock.createShipment(CLIENT_ID, TENANT_ID, cisShipment);
		Assert.assertThat(cisShipment, CoreMatchers.notNullValue());
	}

	@Test
	public void shouldGetLabel() throws URISyntaxException
	{
		final byte[] label = shippingClientMock.getLabel(CLIENT_ID, TENANT_ID, "406951321983787352",
				"35ed62ae-fbc3-4287-9bea-9cff13f61a9e");
		Assert.assertThat(label, CoreMatchers.notNullValue());
	}


}

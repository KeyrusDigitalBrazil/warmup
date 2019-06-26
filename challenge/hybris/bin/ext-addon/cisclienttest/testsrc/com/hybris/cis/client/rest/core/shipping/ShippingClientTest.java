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
package com.hybris.cis.client.rest.core.shipping;

import com.hybris.cis.client.shared.models.CisAddress;
import com.hybris.cis.client.shared.models.CisAddressType;
import com.hybris.cis.client.shipping.ShippingClient;
import com.hybris.cis.client.shipping.models.CisPackage;
import com.hybris.cis.client.shipping.models.CisShipment;
import com.hybris.cis.client.shipping.models.CisWeightUnitsType;
import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.servicelayer.ServicelayerTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;


/**
 * Validates that the "out-of-the-box" spring configuration will wire in the mock client if mock mode is set.
 */
@ManualTest
public class ShippingClientTest extends ServicelayerTest
{
	private static final String CLIENT_ID = "TEST-ID";
	private static final String TENANT_ID = "single";

	@Resource
	private ShippingClient shippingClient;

	private CisShipment cisShipment;

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
		cisShipment = this.shippingClient.createShipment(CLIENT_ID, TENANT_ID, cisShipment);
		Assert.assertThat(cisShipment, CoreMatchers.notNullValue());
	}

	@Test
	public void shouldGetLabel()
	{
		cisShipment = this.shippingClient.createShipment(CLIENT_ID, TENANT_ID, cisShipment);
		final String[] labelLocationPath = cisShipment.getLabels().get(0).getHref().split("/");
		final String shipmentId = labelLocationPath[7];
		final String labelId = labelLocationPath[9];
		final byte[] label = this.shippingClient.getLabel(CLIENT_ID, TENANT_ID, shipmentId, labelId);
		Assert.assertThat(label, CoreMatchers.notNullValue());
	}
}

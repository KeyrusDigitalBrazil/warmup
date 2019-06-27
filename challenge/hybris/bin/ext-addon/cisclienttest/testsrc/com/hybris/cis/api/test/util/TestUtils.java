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
package com.hybris.cis.api.test.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import com.hybris.cis.client.shared.models.CisAddress;
import com.hybris.cis.client.shared.models.CisAddressType;
import com.hybris.cis.client.shared.models.CisLineItem;
import com.hybris.cis.client.shared.models.CisOrder;
import org.junit.Assert;


/**
 * Utils class for testing
 */
public class TestUtils
{

	/**
	 * Empty Constructor
	 */
	private TestUtils()
	{
	}

	/**
	 * Assert egality between {@link BigDecimal} numbers with the possibility to mention if the precision matters
	 *
	 * @param expected
	 * 		expected {@link BigDecimal} value
	 * @param actual
	 * 		actual {@link BigDecimal} value
	 * @param precisionMatters
	 * 		to specify if the precision matters
	 */
	public static void assertEquals(final BigDecimal expected, final BigDecimal actual, final boolean precisionMatters)
	{
		if (precisionMatters)
		{
			Assert.assertEquals(expected, actual);
		}
		else
		{
			Assert.assertEquals(0, expected.compareTo(actual));
		}
	}

	/**
	 * Create a sample order for testing
	 *
	 * @return the sample {@link CisOrder}
	 */
	public static CisOrder createSampleOrder()
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

	/**
	 * Returns a new {@link CisAddress}
	 *
	 * @param addressLine1
	 * 		the address line 1 value to be set
	 * @param city
	 * 		the city to be set
	 * @param state
	 * 		the state to be set
	 * @param zip
	 * 		the zip code to be set
	 * @param country
	 * 		the country to be set
	 * @return the newly constructed {@link CisAddress}
	 */
	private static CisAddress newAddress(final String addressLine1, final String city, final String state, final String zip,
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
}

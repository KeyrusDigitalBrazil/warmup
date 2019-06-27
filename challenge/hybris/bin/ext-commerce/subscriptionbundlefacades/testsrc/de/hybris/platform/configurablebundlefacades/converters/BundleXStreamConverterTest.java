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

package de.hybris.platform.configurablebundlefacades.converters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.subscriptionfacades.converters.SubscriptionXStreamAliasConverter;

import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;


@UnitTest
public class BundleXStreamConverterTest
{
	protected BundleXStreamConverter converter;

	@Before
	public void setUp()
	{
		converter = new BundleXStreamConverter();
		final SubscriptionXStreamAliasConverter xConverter = mock(SubscriptionXStreamAliasConverter.class);
		when(xConverter.getXstream()).thenReturn(mock(XStream.class));
		converter.setAliasConverter(xConverter);
	}

	@Test
	public void shouldConvertCartDataToXML()
	{
		when(converter.getXstream().fromXML(any(String.class))).thenReturn(new CartData());
		final CartData cart = converter.getCartDataFromXml("<xml></xml>");
		assertNotNull(cart);
	}

	@Test
	public void shouldHandleEmptyXML()
	{
		assertNull(converter.getCartDataFromXml(""));
	}

	@Test
	public void shouldConvertCartToXML()
	{
		when(converter.getXstream().toXML(any(CartData.class))).thenReturn("<cart/>");
		final String xml = converter.getXStreamXmlFromCartData(new CartData());
		assertEquals("<cart/>", xml);
	}

}

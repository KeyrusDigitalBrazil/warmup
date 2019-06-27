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

package de.hybris.platform.configurablebundlefacades;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.subscriptionfacades.converters.SubscriptionXStreamAliasConverter;

import org.springframework.beans.factory.annotation.Autowired;
import com.thoughtworks.xstream.XStream;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class BundleXStreamConverter
{
	private static final Logger LOG = Logger.getLogger(BundleXStreamConverter.class);

	private SubscriptionXStreamAliasConverter xStreamAliasConverter;

	public String getXStreamXmlFromCartData(final CartData cartData)
	{
		final String xml = getXstream().toXML(cartData);
		LOG.debug(xml);
		return xml;
	}

	public CartData getCartDataFromXml(final String xml)
	{
		if (StringUtils.isNotEmpty(xml))
		{
			return (CartData) getXstream().fromXML(xml);
		}

		return null;
	}

	public XStream getXstream()
	{
		return xStreamAliasConverter.getXstream();
	}

	protected SubscriptionXStreamAliasConverter getAliasConverter()
	{
		return xStreamAliasConverter;
	}

	@Required
	public void setAliasConverter(final SubscriptionXStreamAliasConverter xStreamAliasConverter)
	{
		this.xStreamAliasConverter = xStreamAliasConverter;
	}
}

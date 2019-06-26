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
package de.hybris.platform.chinesepspwechatpayservices.wechatpay;

import de.hybris.platform.jalo.numberseries.NumberSeries;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;

import org.springframework.beans.factory.annotation.Required;



/**
 * This implementation is an adapter to the {@link NumberSeries} functionality of the platform for getting unique
 * numbers in a series persisted via database. It provides several setter methods for configuration of the used number
 * series like the key of the series at database. After configuration you should call the {@link #init()} method for
 * assuring that the series is created.
 *
 * @since 4.0
 */
public class PrefixablePersistentKeyGenerator extends PersistentKeyGenerator
{

	private String prefix = "";

	@Override
	public Object generate()
	{
		return getPrefix() + super.generate();
	}

	/**
	 * @return the prefix
	 */
	protected String getPrefix()
	{
		return prefix;
	}

	/**
	 * @param prefix
	 *           the prefix to set
	 */
	@Required
	public void setPrefix(final String prefix)
	{
		this.prefix = prefix;
	}

}

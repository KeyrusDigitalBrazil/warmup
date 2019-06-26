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
package de.hybris.platform.commerceservices.util;

import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import java.util.UUID;


public class GuidKeyGenerator implements KeyGenerator
{
	@Override
	public Object generate()
	{
		return UUID.randomUUID().toString();
	}

	@Override
	public Object generateFor(final Object object)
	{
		throw new UnsupportedOperationException("Not supported, please call generate().");
	}

	@Override
	public void reset()
	{
		throw new UnsupportedOperationException("A reset of GUID generator is not supported.");
	}
}

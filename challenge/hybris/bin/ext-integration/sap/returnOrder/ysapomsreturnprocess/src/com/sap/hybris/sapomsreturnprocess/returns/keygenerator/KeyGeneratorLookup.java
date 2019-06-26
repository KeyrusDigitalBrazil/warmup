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
package com.sap.hybris.sapomsreturnprocess.returns.keygenerator;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * This class is used to generate code.
 *
 */
public class KeyGeneratorLookup
{

	private Map<String, PersistentKeyGenerator> keyGeneratorFactories;

	@Required
	public void setKeyGeneratorFactories(final Map<String, PersistentKeyGenerator> keyGeneratorFactories)
	{
		this.keyGeneratorFactories = keyGeneratorFactories;
	}

	public String lookupGenerator(final String logicalSystem)
	{

		validateParameterNotNull(logicalSystem, "Logical System name must not be null!");

		final PersistentKeyGenerator keyGenerator = keyGeneratorFactories.get(logicalSystem);

		return keyGenerator.generate().toString();

	}

}

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
package de.hybris.platform.sap.productconfig.facades.impl;

import de.hybris.platform.sap.productconfig.facades.CPQImageFormatMapping;
import de.hybris.platform.sap.productconfig.facades.CPQImageType;

import java.util.Map;


/**
 * Default implementation of the {@link CPQImageFormatMapping}.<br>
 */
public class CPQImageFormatMappingImpl implements CPQImageFormatMapping
{
	private Map<String, CPQImageType> mapping;

	/**
	 * @param mapping
	 *           image format mapping for CPQ
	 */
	public void setMapping(final Map<String, CPQImageType> mapping)
	{
		this.mapping = mapping;
	}

	@Override
	public Map<String, CPQImageType> getCPQMediaFormatQualifiers()
	{
		return mapping;
	}

}

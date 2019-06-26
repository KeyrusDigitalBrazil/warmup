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
package de.hybris.platform.acceleratorservices.dataimport.batch.converter.impl;

import de.hybris.platform.acceleratorservices.dataimport.batch.converter.ImpexRowFilter;

import java.util.Map;


/**
 * Filter implementation that does not filter any rows.
 */
public class NullImpexRowFilter implements ImpexRowFilter
{

	@Override
	public boolean filter(final Map<Integer, String> row)
	{
		return true;
	}

}

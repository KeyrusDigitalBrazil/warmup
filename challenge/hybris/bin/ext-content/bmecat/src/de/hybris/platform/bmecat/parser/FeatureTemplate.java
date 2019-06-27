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
package de.hybris.platform.bmecat.parser;

import de.hybris.bootstrap.xml.AbstractValueObject;


/**
 * Object which holds the value of a parsed &lt;FEATURETEMPLATE&gt; tag
 * 
 * 
 * 
 */
public class FeatureTemplate extends AbstractValueObject
{
	private String ftName;
	private String ftUnit;
	private Integer ftOrder;

	/**
	 * @return Returns the FTName.
	 */
	public String getFTName()
	{
		return ftName;
	}

	/**
	 * @param name
	 *           The FTName to set.
	 */
	public void setFTName(final String name)
	{
		ftName = name;
	}

	/**
	 * @return Returns the FTOrder.
	 */
	public Integer getFTOrder()
	{
		return ftOrder;
	}

	/**
	 * @param order
	 *           The FTOrder to set.
	 */
	public void setFTOrder(final Integer order)
	{
		ftOrder = order;
	}

	/**
	 * @return Returns the FTUnit.
	 */
	public String getFTUnit()
	{
		return ftUnit;
	}

	/**
	 * @param unit
	 *           The FTUnit to set.
	 */
	public void setFTUnit(final String unit)
	{
		ftUnit = unit;
	}
}

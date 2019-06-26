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
package de.hybris.platform.bmecat.xmlwriter;

import de.hybris.platform.bmecat.constants.BMECatConstants;


/**
 * Writes the &lt;Buyer&gt; tag
 * 
 * 
 */
@SuppressWarnings("deprecation")
public class BuyerTagWriter extends CompanyTagWriter
{
	/**
	 * @param parent
	 */
	public BuyerTagWriter(final XMLTagWriter parent)
	{
		super(parent, false);
	}

	/**
	 * @see de.hybris.platform.bmecat.xmlwriter.CompanyTagWriter#getIdTagName()
	 */
	@Override
	protected String getIdTagName()
	{
		return BMECatConstants.XML.TAG.BUYER_ID;
	}

	/**
	 * @see de.hybris.platform.bmecat.xmlwriter.CompanyTagWriter#getNameTagName()
	 */
	@Override
	protected String getNameTagName()
	{
		return BMECatConstants.XML.TAG.BUYER_NAME;
	}

	/**
	 * @see de.hybris.platform.bmecat.xmlwriter.CompanyTagWriter#getAddressType()
	 */
	@Override
	protected String getAddressType()
	{
		return "buyer";
	}

	/**
	 * @see de.hybris.platform.bmecat.xmlwriter.XMLTagWriter#getTagName()
	 */
	@Override
	protected String getTagName()
	{
		return BMECatConstants.XML.TAG.BUYER;
	}

}

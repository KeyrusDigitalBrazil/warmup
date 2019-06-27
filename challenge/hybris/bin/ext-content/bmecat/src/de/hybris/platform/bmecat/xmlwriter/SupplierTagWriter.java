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
 * Writes the &lt;Supplier&gt; tag
 * 
 * 
 */
@SuppressWarnings("deprecation")
public class SupplierTagWriter extends CompanyTagWriter
{
	/**
	 * @param parent
	 */
	public SupplierTagWriter(final XMLTagWriter parent)
	{
		super(parent, true);
		//super( parent, false ); // hotfix -- default value of field 'mandatory' changed to 'false';
	}

	/**
	 * @see de.hybris.platform.bmecat.xmlwriter.CompanyTagWriter#getIdTagName()
	 */
	@Override
	protected String getIdTagName()
	{
		return BMECatConstants.XML.TAG.SUPPLIER_ID;
	}

	/**
	 * @see de.hybris.platform.bmecat.xmlwriter.CompanyTagWriter#getNameTagName()
	 */
	@Override
	protected String getNameTagName()
	{
		return BMECatConstants.XML.TAG.SUPPLIER_NAME;
	}

	/**
	 * @see de.hybris.platform.bmecat.xmlwriter.CompanyTagWriter#getAddressType()
	 */
	@Override
	protected String getAddressType()
	{
		return "supplier";
	}

	/**
	 * @see de.hybris.platform.bmecat.xmlwriter.XMLTagWriter#getTagName()
	 */
	@Override
	protected String getTagName()
	{
		return BMECatConstants.XML.TAG.SUPPLIER;
	}

}

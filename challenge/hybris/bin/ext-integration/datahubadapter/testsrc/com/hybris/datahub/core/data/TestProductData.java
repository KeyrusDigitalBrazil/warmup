/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package com.hybris.datahub.core.data;

public class TestProductData
{
	private String integrationKey;
	private String isoCode;
	private String baseName;
	private String sku;
	private String unit;
	private String style;
	private String size;

	public String getIntegrationKey()
	{
		return integrationKey;
	}

	public void setIntegrationKey(final String integrationKey)
	{
		this.integrationKey = integrationKey;
	}

	public String getIsoCode()
	{
		return isoCode;
	}

	public void setIsoCode(final String isoCode)
	{
		this.isoCode = isoCode;
	}

	public String getBaseName()
	{
		return baseName;
	}

	public void setBaseName(final String baseName)
	{
		this.baseName = baseName;
	}

	public String getSku()
	{
		return sku;
	}

	public void setSku(final String sku)
	{
		this.sku = sku;
	}

	public String getUnit()
	{
		return unit;
	}

	public void setUnit(final String unit)
	{
		this.unit = unit;
	}

	public String getStyle()
	{
		return style;
	}

	public void setStyle(final String style)
	{
		this.style = style;
	}

	public String getSize()
	{
		return size;
	}

	public void setSize(final String size)
	{
		this.size = size;
	}
}
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
package com.hybris.cis.client.shipping.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Container for a package.
 */
@XmlRootElement(name = "package")
@XmlAccessorType(XmlAccessType.FIELD)
public class CisPackage
{
	/** The insured value of the package. */
	@XmlElement(name = "insuredValue")
	private String insuredValue;

	/** The value of the package. */
	@XmlElement(name = "value")
	private String value;

	/** The width of the package. */
	@XmlElement(name = "width")
	private String width;

	/** The length of the package. */
	@XmlElement(name = "length")
	private String length;

	/** The height of the package. */
	@XmlElement(name = "height")
	private String height;

	/** The girth of the package. */
	@XmlElement(name = "girth")
	private String girth;

	/** The measurement unit of the package. */
	@XmlElement(name = "unit")
	private String unit;

	/** The weight of the package. */
	@XmlElement(name = "weight")
	private String weight;

	/** The weight unit of the package. */
	@XmlElement(name = "weightUnit")
	private CisWeightUnitsType weightUnit;

	/** The description of the package. */
	@XmlElement(name = "description")
	private String description;

	/** The quantity of packages. */
	@XmlElement(name = "quantity")
	private Integer quantity;

	/**
	 * Type of the packaging (ex : letter, large box etc...), this field will have different values based on vendor
	 * implementations.
	 */
	@XmlElement(name = "type")
	private String type;

	public String getInsuredValue()
	{
		return this.insuredValue;
	}

	public void setInsuredValue(final String insuredValue)
	{
		this.insuredValue = insuredValue;
	}

	public String getValue()
	{
		return this.value;
	}

	public void setValue(final String value)
	{
		this.value = value;
	}

	public String getWidth()
	{
		return this.width;
	}

	public void setWidth(final String width)
	{
		this.width = width;
	}

	public String getLength()
	{
		return this.length;
	}

	public void setLength(final String length)
	{
		this.length = length;
	}

	public String getHeight()
	{
		return this.height;
	}

	public void setHeight(final String height)
	{
		this.height = height;
	}

	public String getUnit()
	{
		return this.unit;
	}

	public void setUnit(final String unit)
	{
		this.unit = unit;
	}

	public String getWeight()
	{
		return this.weight;
	}

	public void setWeight(final String weight)
	{
		this.weight = weight;
	}

	public CisWeightUnitsType getWeightUnit()
	{
		return this.weightUnit;
	}

	public void setWeightUnit(final CisWeightUnitsType weightUnit)
	{
		this.weightUnit = weightUnit;
	}

	public String getGirth()
	{
		return this.girth;
	}

	public void setGirth(final String girth)
	{
		this.girth = girth;
	}

	public String getDescription()
	{
		return this.description;
	}

	public void setDescription(final String description)
	{
		this.description = description;
	}

	public Integer getQuantity()
	{
		return this.quantity;
	}

	public void setQuantity(final Integer quantity)
	{
		this.quantity = quantity;
	}

	public String getType()
	{
		return this.type;
	}

	public void setType(final String type)
	{
		this.type = type;
	}
}

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
package de.hybris.platform.commerceservices.address;

import java.util.HashMap;
import java.util.Map;


/**
 * Enum for the various the address fields.
 */
public enum AddressFieldType
{
	TITLE_CODE("titleCode"), FIRST_NAME("firstName"), LAST_NAME("lastName"), ADDRESS_LINE1("addressline1"), ADDRESS_LINE2(
			"addressline2"), CITY("city"), REGION("region"), ZIP_CODE("zipcode"), COUNTRY("country"), UNKNOWN("unknown");

	private String typeString;
	private static final Map<String, AddressFieldType> LOOKUPMAP = new HashMap<String, AddressFieldType>();

	static
	{
		for (final AddressFieldType field : AddressFieldType.values())
		{
			LOOKUPMAP.put(field.getTypeString(), field);
		}
	}

	private AddressFieldType(final String typeString)
	{
		this.typeString = typeString;
	}

	public String getTypeString()
	{
		return typeString;
	}

	public static AddressFieldType lookup(final String typeKey)
	{
		AddressFieldType fieldType = LOOKUPMAP.get(typeKey);
		if (fieldType == null)
		{
			fieldType = UNKNOWN;
		}
		return fieldType;
	}
}

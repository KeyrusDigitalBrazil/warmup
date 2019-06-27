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
package de.hybris.platform.commercefacades.catalog;


/**
 * Options for catalog facade. BASIC - only basic informations. CATEGORIES - also informations about categories.
 * PRODUCTS - works with CATEGORIES option to get informations about products as well.
 */
public enum CatalogOption
{
	BASIC("BASIC"), CATEGORIES("CATEGORIES"), PRODUCTS("PRODUCTS"), SUBCATEGORIES("SUBCATEGORIES");

	private final String code;

	private CatalogOption(final String code)
	{
		this.code = code;
	}

}

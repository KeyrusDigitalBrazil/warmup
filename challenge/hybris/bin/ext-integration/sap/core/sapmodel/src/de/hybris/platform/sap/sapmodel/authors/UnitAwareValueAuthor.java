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
package de.hybris.platform.sap.sapmodel.authors;

import de.hybris.platform.catalog.jalo.classification.ClassificationAttributeUnit;
import de.hybris.platform.catalog.jalo.classification.impex.UnitAwareValue;


public class UnitAwareValueAuthor extends UnitAwareValue
{
	private String valueAuthor;

	public String getValueAuthor()
	{
		return valueAuthor;
	}

	public void setValueAuthor(final String valueAuthor)
	{
		this.valueAuthor = valueAuthor;
	}

	public UnitAwareValueAuthor(final Object value, final ClassificationAttributeUnit unit)
	{
		super(value, unit);
	}

	public UnitAwareValueAuthor(final Object value, final ClassificationAttributeUnit unit, final String author)
	{
		super(value, unit);
		this.valueAuthor = author;
	}
}

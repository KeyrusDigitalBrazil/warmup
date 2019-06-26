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
package de.hybris.platform.cmsfacades.common.predicate;

import de.hybris.platform.cmsfacades.common.service.ProductCatalogItemModelFinder;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Required;


/**
 * Predicate to test if a collection of categoryCodes each individually map to a Category.
 * <p>
 * Returns <tt>TRUE</tt> if the restriction exists; <tt>FALSE</tt> otherwise.
 * </p>
 */
public class CategoryExistsPredicate implements Predicate<String>
{
	private ProductCatalogItemModelFinder productCatalogItemModelFinder;

	/*
	 * Suppress sonar warning (squid:S1166 | Exception handlers should preserve the original exceptions) : It is
	 * perfectly acceptable not to handle "e" here
	 */
	@SuppressWarnings("squid:S1166")
	@Override
	public boolean test(final String compositeCategoryKey)
	{
		boolean result = true;
		try
		{
			getProductCatalogItemModelFinder().getCategoryForCompositeKey(compositeCategoryKey);
		}
		catch (final UnknownIdentifierException | ConversionException e)
		{
			result = false;
		}
		return result;
	}

	protected ProductCatalogItemModelFinder getProductCatalogItemModelFinder()
	{
		return productCatalogItemModelFinder;
	}

	@Required
	public void setProductCatalogItemModelFinder(final ProductCatalogItemModelFinder productCatalogItemModelFinder)
	{
		this.productCatalogItemModelFinder = productCatalogItemModelFinder;
	}

}

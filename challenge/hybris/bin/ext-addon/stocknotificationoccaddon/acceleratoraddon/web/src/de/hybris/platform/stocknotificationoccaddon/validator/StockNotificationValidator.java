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
package de.hybris.platform.stocknotificationoccaddon.validator;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.stocknotificationoccaddon.constants.ErrorMessageConstants;
import de.hybris.platform.stocknotificationoccaddon.exceptions.StockNotificationException;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Required;


/**
 * StockLevelStatus validator. Checks if the product for product code is out-of stock.
 */
public class StockNotificationValidator
{
	private ProductFacade productFacade;

	public void validateProuctStockLevel(final String productCode) throws StockNotificationException
	{
		try
		{
			if (productCode == null)
			{
				throw new RequestParameterException(ErrorMessageConstants.PARAMETER_PRODUCTCODE_REQUIRED_MESSAGE,
						ErrorMessageConstants.PARAMETER_PRODUCTCODE_REQUIRED);
			}
			final ProductData product = getProductFacade().getProductForCodeAndOptions(productCode,
				Arrays.asList(ProductOption.BASIC, ProductOption.STOCK));

			if (product != null && !StockLevelStatus.OUTOFSTOCK.equals(product.getStock().getStockLevelStatus()))
			{
				throw new StockNotificationException(ErrorMessageConstants.NORMAL_PRODUCT_MESSAGE,
						StockNotificationException.NORMAL_PRODUCT, productCode);
			}
		}
		catch (final UnknownIdentifierException e)
		{
			throw new NotFoundException(ErrorMessageConstants.NO_PRODUCT_FOUND_MESSAGE,
					ErrorMessageConstants.PRODUCT_NOT_FOUND,productCode);
		}
	}

	protected ProductFacade getProductFacade()
	{
		return productFacade;
	}

	@Required
	public void setProductFacade(final ProductFacade productFacade)
	{
		this.productFacade = productFacade;
	}

}

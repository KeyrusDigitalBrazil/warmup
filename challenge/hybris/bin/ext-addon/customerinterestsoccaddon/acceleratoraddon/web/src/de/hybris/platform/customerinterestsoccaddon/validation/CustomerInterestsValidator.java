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
package de.hybris.platform.customerinterestsoccaddon.validation;

import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.customerinterestsfacades.data.ProductInterestData;
import de.hybris.platform.customerinterestsfacades.productinterest.ProductInterestFacade;
import de.hybris.platform.customerinterestsoccaddon.constants.ErrorMessageConstants;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * validate if product and product interest exist
 */
public class CustomerInterestsValidator
{

	private ProductFacade productFacade;

	private ProductInterestFacade productInterestFacade;

	private List<NotificationType> notificationTypeList;

	public void checkIfProductExist(final String productCode)
	{
		try
		{
			productFacade.getProductForCodeAndOptions(productCode, Arrays.asList(ProductOption.BASIC));
		}
		catch (final UnknownIdentifierException e)
		{
			throw new NotFoundException(ErrorMessageConstants.NO_PRODUCT_FOUND_MESSAGE, ErrorMessageConstants.NO_PRODUCT_FOUND,
					productCode);
		}
	}

	public void checkIfProductInterestsExist(final String productCode)
	{
		final List<Optional> notifacationAndInterestsDataList = new ArrayList();
		notificationTypeList.forEach(n -> {
			final Optional<ProductInterestData> optional = productInterestFacade.getProductInterestDataForCurrentCustomer(productCode, n);
			if (optional.isPresent())
			{
				notifacationAndInterestsDataList.add(optional);
			}
		});

		if (CollectionUtils.isEmpty(notifacationAndInterestsDataList))
		{
			throw new NotFoundException(ErrorMessageConstants.NO_PRODUCT_INTERESTS_FOUND_MESSAGE,
					ErrorMessageConstants.NO_PRODUCT_INTERESTS,
					productCode);
		}
	}

	public void checkIfPageSizeCorrect(final int pageSize)
	{
		if (pageSize <= 0)
		{
			throw new RequestParameterException(ErrorMessageConstants.PAGESIZE_INVALID_MESSAGE,
					ErrorMessageConstants.PAGESIZE_INVALID);
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

	protected ProductInterestFacade getProductInterestFacade()
	{
		return productInterestFacade;
	}

	@Required
	public void setProductInterestFacade(final ProductInterestFacade productInterestFacade)
	{
		this.productInterestFacade = productInterestFacade;
	}

	protected List<NotificationType> getNotificationTypeList()
	{
		return notificationTypeList;
	}

	@Required
	public void setNotificationTypeList(final List<NotificationType> notificationTypeList)
	{
		this.notificationTypeList = notificationTypeList;
	}

}

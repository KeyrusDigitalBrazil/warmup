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
package de.hybris.platform.ordermanagementwebservices.validators;

import de.hybris.platform.ordermanagementfacades.order.data.OrderEntryRequestData;
import de.hybris.platform.ordermanagementwebservices.dto.order.OrderEntryRequestWsDTO;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.storelocator.pos.PointOfServiceService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * Default Ordermanagement point of service validator {@link PointOfServiceModel}. Checks if point of service with given name exist.
 */
public class PointOfServiceValidator implements Validator
{
	private PointOfServiceService pointOfServiceService;
	private String fieldPath;

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return String.class.equals(clazz) || OrderEntryRequestData.class.isAssignableFrom(clazz) || OrderEntryRequestWsDTO.class
				.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors)
	{
		final String storeName = getFieldPath() == null ? (String) target : (String) errors.getFieldValue(getFieldPath());

		if (!StringUtils.isEmpty(storeName))
		{
			final PointOfServiceModel pointOfServiceModel = getPointOfServiceService().getPointOfServiceForName(storeName);
			if (pointOfServiceModel == null)
			{
				errors.reject("pointOfService.notExists");
			}
		}
	}

	protected PointOfServiceService getPointOfServiceService()
	{
		return pointOfServiceService;
	}

	@Required
	public void setPointOfServiceService(final PointOfServiceService pointOfServiceService)
	{
		this.pointOfServiceService = pointOfServiceService;
	}

	protected String getFieldPath()
	{
		return fieldPath;
	}

	@Required
	public void setFieldPath(final String fieldPath)
	{
		this.fieldPath = fieldPath;
	}

}

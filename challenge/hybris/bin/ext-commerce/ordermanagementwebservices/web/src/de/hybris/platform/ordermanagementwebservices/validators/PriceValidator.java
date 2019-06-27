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

import de.hybris.platform.commercewebservicescommons.dto.product.PriceWsDTO;

import java.math.BigDecimal;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;


/**
 * Default price validator {@link PriceWsDTO}. Checks if a price is valid.
 */
public class PriceValidator implements Validator
{
	private String fieldPath;

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return PriceWsDTO.class.equals(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors)
	{
		validateParameterNotNullStandardMessage("Errors", errors);
		final Object priceObject = this.fieldPath == null ? target : errors.getFieldValue(this.fieldPath);
		if (priceObject instanceof PriceWsDTO)
		{
			this.validatePriceWsDTO((PriceWsDTO) priceObject, errors);
		}
		else
		{
			errors.rejectValue(this.fieldPath, "field.greaterThanZero", new String[] { this.fieldPath },
					"This field must be greater than 0.");
		}
	}

	/**
	 * Validation for price of type PriceWsDTO
	 *
	 * @param price
	 * @param errors
	 */
	protected void validatePriceWsDTO(final PriceWsDTO price, final Errors errors)
	{
		if (price.getValue() == null || price.getValue().compareTo(BigDecimal.ZERO) <= 0)
		{
			errors.rejectValue(this.fieldPath, "field.greaterThanZero", new String[] { this.fieldPath },
					"This field must be greater than 0.");
		}
	}

	protected String getFieldPath()
	{
		return fieldPath;
	}

	public void setFieldPath(final String fieldPath)
	{
		this.fieldPath = fieldPath;
	}
}

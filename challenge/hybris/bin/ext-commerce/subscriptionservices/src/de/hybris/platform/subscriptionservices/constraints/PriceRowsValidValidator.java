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
package de.hybris.platform.subscriptionservices.constraints;


import de.hybris.platform.core.model.product.ProductModel;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * Validates that the price rows of the given {@link ProductModel} are configured correctly.
 */
public class PriceRowsValidValidator implements ConstraintValidator<PriceRowsValid, Object>
{

	private static final Logger LOG = Logger.getLogger(PriceRowsValidValidator.class.getName());

	@Override
	public void initialize(final PriceRowsValid constraintAnnotation)
	{
		if (StringUtils.isEmpty(constraintAnnotation.priceRowType()))
		{
			throw new IllegalArgumentException("parameter 'priceRowType' must not be empty");
		}
	}


	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context)
	{
		boolean valid = false;

		if (value instanceof Boolean)
		{
			valid = (Boolean) value;
		}
		else
		{
			LOG.error("Provided object is not an instance of Boolean: " + value.getClass());
		}

		return valid;
	}

}

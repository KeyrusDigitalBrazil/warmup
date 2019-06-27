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

package de.hybris.platform.configurablebundleservices.constraints;

import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.collections.CollectionUtils;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;


/**
 * Validates that the given {@link BundleTemplateModel} has any child templates OR any products.
 */
public class BundleTemplateProductsAssignedValidator
		extends BasicBundleTemplateValidator<BundleTemplateProductsAssigned>
{
	@Override
	public boolean isValid(final BundleTemplateModel value, final ConstraintValidatorContext context)
	{
		validateParameterNotNull(value, "Validating object is null");
		return value.getPk() == null
				|| CollectionUtils.isNotEmpty(value.getChildTemplates())
				|| CollectionUtils.isNotEmpty(value.getProducts());
	}
}

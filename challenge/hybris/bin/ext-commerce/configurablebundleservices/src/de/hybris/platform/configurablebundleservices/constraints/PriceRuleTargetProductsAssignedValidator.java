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

import de.hybris.platform.configurablebundleservices.model.AbstractBundleRuleModel;
import org.apache.commons.collections.CollectionUtils;

import javax.validation.ConstraintValidatorContext;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;


/**
 * Validates that at least 1 target product is assigned to the given
 * {@link de.hybris.platform.configurablebundleservices.model.ChangeProductPriceBundleRuleModel}.
 */
public class PriceRuleTargetProductsAssignedValidator
		extends BasicBundleRuleValidator<PriceRuleTargetProductsAssigned>
{
	@Override
	public boolean isValid(final AbstractBundleRuleModel value, final ConstraintValidatorContext context)
	{
        validateParameterNotNull(value, "Validating object is null");
		if (CollectionUtils.isEmpty(value.getTargetProducts()))
		{
			buildErrorMessage(AbstractBundleRuleModel.TARGETPRODUCTS, context);
			return false;
		}
		return true;
	}

}

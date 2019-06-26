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
 * Validates that conditional product list of a price rule has at least 1 product.
 */
public class PriceRuleConditionalProductsAssignedValidator
    extends BasicBundleRuleValidator<PriceRuleConditionalProductsAssigned>
{
    @Override
    public boolean isValid(final AbstractBundleRuleModel value, final ConstraintValidatorContext context)
    {
        validateParameterNotNull(value, "Validating object is null");
        if (CollectionUtils.isEmpty(value.getConditionalProducts()))
        {
            buildErrorMessage(AbstractBundleRuleModel.CONDITIONALPRODUCTS, context);
            return false;
        }
        return true;
    }
}

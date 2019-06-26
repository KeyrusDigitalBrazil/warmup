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
import de.hybris.platform.configurablebundleservices.model.ChangeProductPriceBundleRuleModel;
import de.hybris.platform.configurablebundleservices.model.DisableProductBundleRuleModel;

import javax.validation.ConstraintValidatorContext;

import org.apache.commons.collections.CollectionUtils;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;


/**
 * Validates that the given {@link BundleTemplateModel}'s bundle rules have all at least 1 target product.
 * <p>The validator is deprecated.</p>
 * @see PriceRuleTargetProductsAssignedValidator
 * @see AbstractBundleRuleTargetProductsAssigned
 * 
 * @deprecated Since 6.0
 */
@Deprecated
public class BundleTemplateRulesTargetProductsAssignedValidator
        extends BasicBundleTemplateValidator<BundleTemplateRulesTargetProductsAssigned>
{
    @Override
	 // NO SONAR
    public boolean isValid(final BundleTemplateModel value, final ConstraintValidatorContext context)
    {
        validateParameterNotNull(value, "Validating object is null");
        // parent templates do not have rules
        if (value.getParentTemplate() == null)
        {
            return true;
        }
        else
        {
            if (value.getDisableProductBundleRules() != null)
            {
                for (final DisableProductBundleRuleModel disableRule : value.getDisableProductBundleRules())
                {
                    if (CollectionUtils.isEmpty(disableRule.getTargetProducts()))
                    {
                        return false;
                    }
                }
            }

            if (value.getChangeProductPriceBundleRules() != null)
            {
                for (final ChangeProductPriceBundleRuleModel priceRule : value.getChangeProductPriceBundleRules())
                {
                    if (CollectionUtils.isEmpty(priceRule.getTargetProducts()))
                    {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}

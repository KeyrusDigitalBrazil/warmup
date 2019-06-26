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
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.ChangeProductPriceBundleRuleModel;
import de.hybris.platform.configurablebundleservices.model.DisableProductBundleRuleModel;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;


/**
 * Validates that at least 1 product is assigned to the {@link AbstractBundleRuleModel}'s component
 * <p>Deprecated, because this functionality is covered by other validators.</p>
 * @see BundleTemplateProductsAssignedValidator
 * 
 * @deprecated Since 6.0
 */
@Deprecated
public class AbstractBundleRuleComponentProductsAssignedValidator implements
        ConstraintValidator<AbstractBundleRuleComponentProductsAssigned, Object>
{
    private static final Logger LOG = Logger.getLogger(AbstractBundleRuleComponentProductsAssignedValidator.class.getName());

    @Override
    public void initialize(final AbstractBundleRuleComponentProductsAssigned constraintAnnotation)
    {
        // empty
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context)
    {
        BundleTemplateModel component = null;

        if (value instanceof DisableProductBundleRuleModel)
        {
            component = ((DisableProductBundleRuleModel) value).getBundleTemplate();
        }
        else if (value instanceof ChangeProductPriceBundleRuleModel)
        {
            component = ((ChangeProductPriceBundleRuleModel) value).getBundleTemplate();
        }
        else
        {
            LOG.error("Provided object is not an instance of DisableProductBundleRuleModel nor ChangeProductPriceBundleRuleModel: "
                    + value.getClass());
        }

        if (component != null)
        {
            return CollectionUtils.isNotEmpty(component.getProducts());
        }

        return false;
    }
}

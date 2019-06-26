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
import org.apache.commons.collections.CollectionUtils;

import javax.validation.ConstraintValidatorContext;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Triggers when {@link de.hybris.platform.configurablebundleservices.model.BundleTemplateModel#getChildTemplates()}
 * is not empty AND {@link de.hybris.platform.configurablebundleservices.model.BundleTemplateModel#getBundleSelectionCriteria()}
 * is not empty.
 */
public class BundleTemplateSelectionCriteriaValidator
        extends BasicBundleTemplateValidator<BundleTemplateSelectionCriteria>
{
    @Override
    public boolean isValid(final BundleTemplateModel value, final ConstraintValidatorContext context)
    {
        validateParameterNotNull(value, "Validating object is null");
        return !(CollectionUtils.isNotEmpty(value.getChildTemplates()) && value.getBundleSelectionCriteria() != null);
    }
}

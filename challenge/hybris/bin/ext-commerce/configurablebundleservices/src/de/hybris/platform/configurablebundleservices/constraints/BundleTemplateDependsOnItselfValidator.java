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

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Triggers when one of the
 * {@link BundleTemplateModel#getDependentBundleTemplates()}
 * is is the model itself.
 */
public class BundleTemplateDependsOnItselfValidator extends BasicBundleTemplateValidator<BundleTemplateDependsOnItself>
{
    @Override
    public boolean isValid(final BundleTemplateModel value, final ConstraintValidatorContext context)
    {
        validateParameterNotNull(value, "Validating object is null");
        if (value.getDependentBundleTemplates() != null && value.getDependentBundleTemplates().contains(value))
        {
            buildErrorMessage(BundleTemplateModel.DEPENDENTBUNDLETEMPLATES, context, value.getId());
            return false;
        }
        return true;
    }
}


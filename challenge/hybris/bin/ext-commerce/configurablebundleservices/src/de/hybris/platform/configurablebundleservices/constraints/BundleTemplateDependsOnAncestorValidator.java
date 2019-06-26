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
 * is an ancestor of the model.
 */
public class BundleTemplateDependsOnAncestorValidator
        extends BasicBundleTemplateValidator<BundleTemplateDependsOnAncestor>
{
    @Override
    public boolean isValid(final BundleTemplateModel value, final ConstraintValidatorContext context)
    {
        validateParameterNotNull(value, "Validating object is null");
        BundleTemplateModel ancestor = value.getParentTemplate();
        while (ancestor != null)
        {
            if (value.getDependentBundleTemplates() != null && value.getDependentBundleTemplates().contains(ancestor))
            {
                buildErrorMessage(BundleTemplateModel.DEPENDENTBUNDLETEMPLATES, context, ancestor.getId());
                return false;
            }
            ancestor = ancestor.getParentTemplate();
        }
        return true;
    }
}

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

import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.jalo.JaloSession;

import javax.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.Set;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Triggers when one of the
 * {@link BundleTemplateModel#getRequiredBundleTemplates()}
 * is an ancestor of the model.
 */
public class BundleTemplateRequiresAncestorValidator
        extends BasicBundleTemplateValidator<BundleTemplateRequiresAncestor>
{
    @Override
    public boolean isValid(final BundleTemplateModel value, final ConstraintValidatorContext context)
    {
		 if(CatalogManager.isSyncInProgress(JaloSession.getCurrentSession().getSessionContext()))
		 {
			 return true;
		 }
        validateParameterNotNull(value, "Validating object is null");
        final Set<BundleTemplateModel> ancestors = new HashSet<>();
        BundleTemplateModel ancestor = value;
        while (ancestor != null)
        {
            ancestors.add(ancestor);
            ancestor = ancestor.getParentTemplate();
        }
        final boolean[] res = {true};
        if (value.getRequiredBundleTemplates() != null) { // NOSONAR
            value.getRequiredBundleTemplates().stream()
                    .filter(required -> !ancestors.add(required))
                    .forEach(required -> {
                        buildErrorMessage(BundleTemplateModel.REQUIREDBUNDLETEMPLATES, context, required.getId());
                        res[0] = false;
                    });
        }

        return res[0];
    }
}

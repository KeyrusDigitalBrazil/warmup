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
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintValidatorContext;

import java.util.Collection;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Triggers when one of the
 * {@link de.hybris.platform.configurablebundleservices.model.BundleTemplateModel#getRequiredBundleTemplates()}
 * does not belong to the parent package of the model.
 */
public class BundleTemplateNonLeafValidator extends BasicBundleTemplateValidator<BundleTemplateNonLeaf>
{
    @Override
    public boolean isValid(final BundleTemplateModel value, final ConstraintValidatorContext context)
    {
        validateParameterNotNull(value, "Validating object is null");
        return isRelationValid(value.getRequiredBundleTemplates(), context, BundleTemplateModel.REQUIREDBUNDLETEMPLATES) &
                isRelationValid(value.getDependentBundleTemplates(), context, BundleTemplateModel.DEPENDENTBUNDLETEMPLATES);
    }

    /**
     * Checks if relations contain non-leaf nodes, and for each non-leaf node add error message.
     * @param collection the relations
     * @param context the ConstraintValidatorContext
     * @param fieldName the name of relation.
     * @return true if relations has no non-leaf nodes, false - otherwise.
     */
    protected boolean isRelationValid(final Collection<BundleTemplateModel> collection,
                                      final ConstraintValidatorContext context, final String fieldName)
    {
        final boolean[] result = {true};
        if (!CollectionUtils.isEmpty(collection))
        {
            collection.stream()
                    .filter(item -> item != null)
                    .filter(this::isNonLeaf)
                    .forEach(item -> {
                        result[0] = false;
                        buildErrorMessage(fieldName, context, item.getId());
                    });
        }
        return result[0];
    }

    /**
     * Checks if item is non-leaf.
     * @param item - the BundleTemplateModel to check
     * @return true if item is non-leaf, false - otherwise.
     */
    protected boolean isNonLeaf(final BundleTemplateModel item)
    {
        return !CollectionUtils.isEmpty(item.getChildTemplates());
    }
}

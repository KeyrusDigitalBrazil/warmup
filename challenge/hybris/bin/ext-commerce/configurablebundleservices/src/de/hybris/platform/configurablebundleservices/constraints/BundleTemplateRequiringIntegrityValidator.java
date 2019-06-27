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

import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import javax.annotation.Resource;
import javax.validation.ConstraintValidatorContext;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Triggers when one of the
 * {@link de.hybris.platform.configurablebundleservices.model.BundleTemplateModel#getRequiredBundleTemplates()}
 * does not belong to the parent package of the model.
 */
public class BundleTemplateRequiringIntegrityValidator
        extends BasicBundleTemplateValidator<BundleTemplateRequiringIntegrity>
{
    private static final Logger LOG = Logger.getLogger(BundleTemplateRequiringIntegrityValidator.class);

    @Resource(name = "bundleTemplateService")
    private BundleTemplateService bundleTemplateService;

    @Override
    public boolean isValid(final BundleTemplateModel value, final ConstraintValidatorContext context)
    {
        if (bundleTemplateService == null)
        {
            LOG.warn(getClass().getSimpleName() + " is not initialized.");
        }
        else
        {
            validateParameterNotNull(value, "Validating object is null");
            final BundleTemplateModel root = getBundleTemplateService().getRootBundleTemplate(value);
            if (CollectionUtils.isNotEmpty(value.getRequiredBundleTemplates()))
            {
                final boolean[] result = {true};
                value.getRequiredBundleTemplates().stream()
                        .filter(item -> item != null)
                        .filter(item -> !root.equals(getBundleTemplateService().getRootBundleTemplate(item)))
                        .forEach(item -> {
                            result[0] = false;
                            buildErrorMessage(BundleTemplateModel.REQUIREDBUNDLETEMPLATES, context, item.getId());
                        });
                return result[0];
            }
        }
        return true;
    }

    public BundleTemplateService getBundleTemplateService()
    {
        return bundleTemplateService;
    }
}

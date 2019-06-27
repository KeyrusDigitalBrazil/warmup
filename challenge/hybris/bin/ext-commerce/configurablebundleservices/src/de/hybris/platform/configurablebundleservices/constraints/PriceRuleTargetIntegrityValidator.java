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
import de.hybris.platform.core.model.product.ProductModel;
import org.apache.commons.collections.CollectionUtils;

import javax.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Triggers when {@link de.hybris.platform.configurablebundleservices.model.ChangeProductPriceBundleRuleModel#getTargetProducts()}
 * contains a product is not a part of
 * {@link BundleTemplateModel#getProducts()}.
 */
public class PriceRuleTargetIntegrityValidator extends BasicBundleRuleValidator<PriceRuleTargetIntegrity>
{
    @Override
    public boolean isValid(final AbstractBundleRuleModel value, final ConstraintValidatorContext context)
    {
        validateParameterNotNull(value, "Validating object is null");
        final BundleTemplateModel bundleTemplate = getBundleTemplate(value);
        if (bundleTemplate != null)
        {
            final boolean[] result = {true};
            final Collection<ProductModel> targetProducts = value.getTargetProducts();
            if (targetProducts == null)
            {
                return true;
            }
            final List<ProductModel> bundleProducts = bundleTemplate.getProducts();
            CollectionUtils.subtract(
                    targetProducts,
                    bundleProducts == null ? Collections.emptyList() : bundleProducts)
                    .forEach(invalidProduct -> {
                        result[0] = false;
                        buildErrorMessage(AbstractBundleRuleModel.TARGETPRODUCTS, context, ((ProductModel) invalidProduct).getCode());
                    });
            return result[0];
        }
        return true;
    }
}

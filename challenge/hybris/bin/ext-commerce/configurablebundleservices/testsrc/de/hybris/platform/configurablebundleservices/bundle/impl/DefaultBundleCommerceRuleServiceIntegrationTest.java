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

package de.hybris.platform.configurablebundleservices.bundle.impl;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.configurablebundleservices.model.DisableProductBundleRuleModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Collections;

import javax.annotation.Resource;

import org.junit.Test;


@IntegrationTest
public class DefaultBundleCommerceRuleServiceIntegrationTest extends ServicelayerTest
{
	@Resource
	private DefaultBundleCommerceRuleService defaultBundleCommerceRuleService;

	@Resource
	ModelService modelService;

	@Test
	public void testCreateMessageForDisableRule()
	{
		final ProductModel targetProduct = modelService.create(ProductModel.class);
		targetProduct.setName("Target product");

		final ProductModel conditionalProduct1 = modelService.create(ProductModel.class);
		conditionalProduct1.setName("Conditional product 1");

		final ProductModel conditionalProduct2 = modelService.create(ProductModel.class);
		conditionalProduct2.setName("Conditional product 2");

		final DisableProductBundleRuleModel disableRule = modelService.create(DisableProductBundleRuleModel.class);
		disableRule.setTargetProducts(Collections.singletonList(targetProduct));
		disableRule.setConditionalProducts(Arrays.asList(conditionalProduct1, conditionalProduct2));

		final String message = defaultBundleCommerceRuleService.createMessageForDisableRule(disableRule, targetProduct);

		assertThat(message).isEqualTo("'Target product' is not available with 'Conditional product 1', 'Conditional product 2'");
	}
}

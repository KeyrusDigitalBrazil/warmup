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
package de.hybris.platform.customercouponservices.compiler.strategies.impl;

import de.hybris.platform.customercouponservices.daos.CustomerCouponDao;
import de.hybris.platform.customercouponservices.model.CustomerCouponForPromotionSourceRuleModel;
import de.hybris.platform.promotionengineservices.compiler.strategies.ConditionResolutionStrategy;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;
import de.hybris.platform.promotionengineservices.model.RuleBasedPromotionModel;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Creates relationship between customer coupon and promotion source rule for finding promotion by customer coupon code
 */
public class DefaultCouponConditionResolutionStrategy implements ConditionResolutionStrategy
{

	private ModelService modelService;
	private CustomerCouponDao customerCouponDao;

	@Override
	public void getAndStoreParameterValues(final RuleConditionData condition, final PromotionSourceRuleModel rule,
			final RuleBasedPromotionModel promotion)
	{
		final RuleParameterData couponRuleParams = condition.getParameters().get("coupons");
		if (couponRuleParams != null)
		{
			final List<String> couponCodes = couponRuleParams.getValue();
			if (CollectionUtils.isNotEmpty(couponCodes))
			{
				couponCodes.forEach(code -> {
					final CustomerCouponForPromotionSourceRuleModel cusCouponForRule = getModelService().create(
							CustomerCouponForPromotionSourceRuleModel.class);
					cusCouponForRule.setCustomerCouponCode(code);
					cusCouponForRule.setRule(rule);
					cusCouponForRule.setPromotion(promotion);

					getModelService().save(cusCouponForRule);
				});
			}
		}
	}

	@Override
	public void cleanStoredParameterValues(final RuleCompilerContext context)
	{
		getModelService().removeAll(
				getCustomerCouponDao().findAllCusCouponForSourceRules((PromotionSourceRuleModel) context.getRule(),
						context.getModuleName()));
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected CustomerCouponDao getCustomerCouponDao()
	{
		return customerCouponDao;
	}

	@Required
	public void setCustomerCouponDao(final CustomerCouponDao customerCouponDao)
	{
		this.customerCouponDao = customerCouponDao;
	}

}

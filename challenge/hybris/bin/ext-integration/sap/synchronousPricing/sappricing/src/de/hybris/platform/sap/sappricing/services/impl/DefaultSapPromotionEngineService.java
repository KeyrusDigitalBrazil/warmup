/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sappricing.services.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.promotionengineservices.promotionengine.impl.DefaultPromotionEngineService;
import de.hybris.platform.promotions.jalo.PromotionsManager.AutoApplyMode;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.promotions.result.PromotionOrderResults;
import de.hybris.platform.ruleengine.RuleEvaluationResult;
import de.hybris.platform.sap.sappricing.services.SapPricingEnablementService;

/**
 * Default implementation of Sap Promotion Engine Service.
 */
public class DefaultSapPromotionEngineService extends DefaultPromotionEngineService {

	private SapPricingEnablementService sapPricingEnablementService;

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSapPromotionEngineService.class);

	@Override
	public RuleEvaluationResult evaluate(ProductModel product, Collection<PromotionGroupModel> promotionGroups) {

		if (getSapPricingEnablementService().isCatalogPricingEnabled()) {

			String msg = String.format(
					"The product [%s] promotions are not supported in the SAP synchronous pricing scenario!",
					product.getCode());
			LOGGER.warn(msg);
			RuleEvaluationResult ruleEvaluationResult = new RuleEvaluationResult();
			ruleEvaluationResult.setEvaluationFailed(true);
			ruleEvaluationResult.setErrorMessage(msg);
			return ruleEvaluationResult;

		} else {

			return super.evaluate(product, promotionGroups);

		}
	}

	@Override
	public List<? extends AbstractPromotionModel> getAbstractProductPromotions(
			Collection<PromotionGroupModel> promotionGroups, ProductModel product, boolean evaluateRestrictions,
			Date date) {

		if (getSapPricingEnablementService().isCatalogPricingEnabled()) {

			LOGGER.warn(String.format(
					"The product [%s] promotions are not supported in the SAP synchronous pricing scenario!",
					product.getCode()));
			return Collections.emptyList();

		} else {

			return super.getAbstractProductPromotions(promotionGroups, product, evaluateRestrictions, date);

		}

	}

	@Override
	public PromotionOrderResults updatePromotions(Collection<PromotionGroupModel> promotionGroups,
			AbstractOrderModel order) {

		if (getSapPricingEnablementService().isCartPricingEnabled()) {
			LOGGER.warn(String.format(
					"The order [%s] promotions update is not supported in the SAP synchronous pricing scenario!",
					order.getCode()));
			return new PromotionOrderResults(null, null, Collections.emptyList(), 0d);

		} else {

			return super.updatePromotions(promotionGroups, order);

		}

	}

	@Override
	public PromotionOrderResults updatePromotions(Collection<PromotionGroupModel> promotionGroups,
			AbstractOrderModel order, boolean evaluateRestrictions, AutoApplyMode productPromotionMode,
			AutoApplyMode orderPromotionMode, Date date) {

		if (getSapPricingEnablementService().isCartPricingEnabled()) {

			LOGGER.warn(String.format(
					"The order [%s] promotions update is not supported in the SAP synchronous pricing scenario!",
					order.getCode()));
			return new PromotionOrderResults(null, null, Collections.emptyList(), 0d);

		} else {

			return super.updatePromotions(promotionGroups, order, evaluateRestrictions, productPromotionMode,
					orderPromotionMode, date);
		}

	}

	@Override
	public PromotionOrderResults getPromotionResults(AbstractOrderModel order) {

		if (getSapPricingEnablementService().isCartPricingEnabled()) {

			LOGGER.warn(String.format(
					"The order [%s] promotions are not supported in the SAP synchronous pricing scenario!",
					order.getCode()));
			return new PromotionOrderResults(null, null, Collections.emptyList(), 0d);

		} else {

			return super.getPromotionResults(order);
		}

	}

	@Override
	public PromotionOrderResults getPromotionResults(Collection<PromotionGroupModel> promotionGroups,
			AbstractOrderModel order, boolean evaluateRestrictions, AutoApplyMode productPromotionMode,
			AutoApplyMode orderPromotionMode, Date date) {

		if (getSapPricingEnablementService().isCartPricingEnabled()) {

			LOGGER.warn(String.format(
					"The order [%s] promotions are not supported in the SAP synchronous pricing scenario!",
					order.getCode()));
			return new PromotionOrderResults(null, null, Collections.emptyList(), 0d);

		} else {

			return super.getPromotionResults(promotionGroups, order, evaluateRestrictions, productPromotionMode,
					orderPromotionMode, date);
		}

	}

	@Override
	public void cleanupCart(CartModel cart) {

		if (getSapPricingEnablementService().isCartPricingEnabled()) {

			LOGGER.warn(String.format(
					"The cart [%s] promotions cleanup is not supported in the SAP synchronous pricing scenario!",
					cart.getCode()));
			return;

		} else {

			super.cleanupCart(cart);

		}

	}

	@Override
	public void transferPromotionsToOrder(AbstractOrderModel source, OrderModel target,
			boolean onlyTransferAppliedPromotions) {

		if (getSapPricingEnablementService().isCartPricingEnabled()) {

			LOGGER.warn("Promotions transfer is not supported in the SAP synchronous pricing scenario!");
			return;

		} else {

			super.transferPromotionsToOrder(source, target, onlyTransferAppliedPromotions);

		}

	}

	protected SapPricingEnablementService getSapPricingEnablementService() {
		return sapPricingEnablementService;
	}

	@Required
	public void setSapPricingEnablementService(SapPricingEnablementService sapPricingEnablementService) {
		this.sapPricingEnablementService = sapPricingEnablementService;
	}

}

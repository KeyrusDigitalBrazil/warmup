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
package de.hybris.platform.sap.saprevenuecloudorder.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.sap.saprevenuecloudorder.constants.SaprevenuecloudorderConstants;
import de.hybris.platform.sap.saprevenuecloudorder.service.SapRevenueCloudSplitOrderService;

/**
 * 
 * Default implementation for {@link DefaultSapRevenueCloudSplitOrderService}
 *
 */
public class DefaultSapRevenueCloudSplitOrderService implements SapRevenueCloudSplitOrderService
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSapRevenueCloudSplitOrderService.class);
	

	/**
	 * Implementation for authorization amount split between SAP Subscription Billing and SAP S4HANA systems
	 * The split is based on these assumptions
	 * 
	 * <br> No tax, promotion and discounts are considered subscription products from SAP Subscription Billing
	 * <br> All the tax, promotions and discounts added to the cart are related to products replicated from S4HANA
	 * 
	 * The authorization amount for SAP Subscription Billing is the total price of all subscription products from Revenue Cloud
	 * The authorization amount for S4HANA is the cart total minus the total authorization amount for SAP Subscription Billing
	 * 
	 * @param cart
	 * 			- customer's current cart
	 * @return {@link Map<String,BigDecimal>}
	 * 			- split authorized amount for system
	 * 
	 */
	
	@Override
	public Map<String,BigDecimal> getAuthorizationAmountListFromCart(CartModel cart) {
		LOG.info(String.format("Splitting the cart authorization amount for target systems [%s] and [%s]", SaprevenuecloudorderConstants.PAYMENT_AUTH_SPLIT_TARGET_REVENUE_CLOUD, SaprevenuecloudorderConstants.PAYMENT_AUTH_SPLIT_TARGET_S4HANA));
		Map<String,BigDecimal> authAmountMap = new HashMap<>();
		double authAmountForRC =  cart.getEntries().stream().filter(e -> StringUtils.isNotEmpty(e.getProduct().getSubscriptionCode())).mapToDouble(AbstractOrderEntryModel::getTotalPrice).sum();
		final BigDecimal authAmountForRCBD = BigDecimal.valueOf(authAmountForRC).setScale(2, RoundingMode.HALF_EVEN);
		authAmountMap.put(SaprevenuecloudorderConstants.PAYMENT_AUTH_SPLIT_TARGET_REVENUE_CLOUD, authAmountForRCBD);
		authAmountMap.put(SaprevenuecloudorderConstants.PAYMENT_AUTH_SPLIT_TARGET_S4HANA, calculateAuthAmount(cart).subtract(authAmountForRCBD));
		return authAmountMap;
	}
	
	/**
	 * 
	 * @param cartModel
	 * 			- cart for calculating the total amount
	 * @return {@link BigDecimal}
	 * 			- cart total imcluding tax
	 */
	private BigDecimal calculateAuthAmount(final CartModel cartModel)
	{
		final Double totalPrice = cartModel.getTotalPrice();
		final Double totalTax = (cartModel.getNet().booleanValue() && cartModel.getStore() != null
				&& cartModel.getStore().getExternalTaxEnabled().booleanValue()) ? cartModel.getTotalTax() : Double.valueOf(0d);
		final BigDecimal totalPriceWithoutTaxBD = BigDecimal.valueOf(totalPrice == null ? 0d : totalPrice.doubleValue()).setScale(2,
				RoundingMode.HALF_EVEN);
		final BigDecimal totalPriceBD = BigDecimal.valueOf(totalTax == null ? 0d : totalTax.doubleValue())
				.setScale(2, RoundingMode.HALF_EVEN).add(totalPriceWithoutTaxBD);
		LOG.info(String.format("Total authorization amount for cart [%s]  is [%s]", cartModel.getCode(), totalPriceBD));
		return totalPriceBD;
	}

}

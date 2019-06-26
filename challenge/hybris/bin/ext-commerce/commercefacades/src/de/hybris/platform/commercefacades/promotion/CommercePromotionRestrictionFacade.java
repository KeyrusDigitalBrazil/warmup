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
package de.hybris.platform.commercefacades.promotion;


import de.hybris.platform.commerceservices.promotion.CommercePromotionRestrictionException;


/**
 * PromotionRestriction facade interface.
 */
public interface CommercePromotionRestrictionFacade
{
	/**
	 * Enables OrderPromotion by adding current cart to PromotionOrderRestriction
	 * 
	 * @param promotionCode
	 *           promotion
	 * @throws {@link CommercePromotionRestrictionException}
	 */
	void enablePromotionForCurrentCart(String promotionCode) throws CommercePromotionRestrictionException;

	/**
	 * Disables OrderPromotion by removing current cart from PromotionOrderRestriction
	 * 
	 * @param promotionCode
	 *           promotion
	 * @throws {@link CommercePromotionRestrictionException}
	 */
	void disablePromotionForCurrentCart(String promotionCode) throws CommercePromotionRestrictionException;
}

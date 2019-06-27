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
package de.hybris.platform.commerceservices.promotion.dao;


import de.hybris.platform.commerceservices.model.promotions.PromotionOrderRestrictionModel;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.promotions.model.AbstractPromotionRestrictionModel;
import de.hybris.platform.servicelayer.internal.dao.Dao;

import java.util.List;


/**
 * Dao for {@link AbstractPromotionRestrictionModel} access.
 * 
 * @spring.bean commercePromotionRestrictionDao
 * 
 */
public interface CommercePromotionRestrictionDao extends Dao
{
	/**
	 * Finds all promotion restrictions of given promotion
	 * 
	 * @param promotion
	 *           promotion model
	 * @return list of {@link AbstractPromotionRestrictionModel}
	 */
	List<AbstractPromotionRestrictionModel> findPromotionRestriction(AbstractPromotionModel promotion);

	/**
	 * Finds all order promotion restrictions of given promotion
	 * 
	 * @param promotion
	 *           promotion model
	 * @return list of {@link PromotionOrderRestrictionModel}
	 */
	List<PromotionOrderRestrictionModel> findPromotionOrderRestriction(AbstractPromotionModel promotion);
}

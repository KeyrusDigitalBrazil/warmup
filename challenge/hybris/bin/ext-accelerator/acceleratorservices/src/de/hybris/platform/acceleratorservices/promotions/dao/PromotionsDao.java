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
package de.hybris.platform.acceleratorservices.promotions.dao;

import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.servicelayer.internal.dao.Dao;


public interface PromotionsDao extends Dao
{
	AbstractPromotionModel getPromotionForCode(String code);
}

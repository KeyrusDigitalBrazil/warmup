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
package de.hybris.platform.b2b.dao;


import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;


/**
 * A data access object around {@link B2BBudgetModel}
 * 
 */
public interface B2BBudgetDao extends GenericDao<B2BBudgetModel>
{

	/**
	 * @param code
	 *           the code
	 * @return budget matching the code
	 */
	B2BBudgetModel findBudgetByCode(String code);
}

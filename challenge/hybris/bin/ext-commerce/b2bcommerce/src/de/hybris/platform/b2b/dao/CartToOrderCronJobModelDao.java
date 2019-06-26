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

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.orderscheduling.model.CartToOrderCronJobModel;

import java.util.List;


/**
 * The interface CartToOrderCronJobModelDao provides methods for finding {@link CartToOrderCronJobModel} objects.
 * 
 * @spring.bean cartToOrderCronJobModelDao
 */
public interface CartToOrderCronJobModelDao
{
	/**
	 * Finds CartToOrderCronJob for a given code.
	 * 
	 * @param code
	 *           the cronjob code.
	 * @return the matching cronjob.
	 */
	CartToOrderCronJobModel findCartToOrderCronJob(String code);

	/**
	 * Finds CartToOrderCronJob for a given user.
	 * 
	 * @param user
	 *           A user who had a cart scheduled for replenishment.
	 * @return A list of cronjobs responsible for replenishment of users cart.
	 */
	List<CartToOrderCronJobModel> findCartToOrderCronJobs(UserModel user);
}

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
package de.hybris.platform.sap.sapproductavailability.service.impl;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.sapproductavailability.service.SapCustomerDeterminationService;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * retrive the sap Customer based on the ID
 *
 */
public class DefaultSapCustomerDeterminationService implements SapCustomerDeterminationService {

	private UserService userService;

	@Override
	public String readSapCustomerID() {

		if (!getUserService().isAnonymousUser(getUserService().getCurrentUser())) {
			final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
			final String customerId = currentCustomer != null ? currentCustomer.getCustomerID() : null;
			return customerId;
		}

		return null;

	}

	protected UserService getUserService() {
		return userService;
	}

	@Required
	public void setUserService(final UserService userService) {
		this.userService = userService;
	}

}

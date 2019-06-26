/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousing.util.models;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.warehousing.util.builder.CustomerModelBuilder;
import de.hybris.platform.warehousing.util.dao.impl.CustomerDaoImpl;
import org.springframework.beans.factory.annotation.Required;


public class Customers extends AbstractItems<CustomerModel>
{
	public static final String UID_POLO = "polo@polo.com";
	public static final String NAME_POLO = "polo";

	private CustomerDaoImpl customerDao;

	public UserModel polo()
	{
		return getOrCreateUser(UID_POLO, NAME_POLO);
	}

	protected UserModel getOrCreateUser(final String uid, final String name)
	{
		return getOrSaveAndReturn(() -> getCustomerDao().getByCode(uid), () -> CustomerModelBuilder.aModel().withUid(uid)
				.withName(name).build());
	}

	public CustomerDaoImpl getCustomerDao()
	{
		return customerDao;
	}

	@Required
	public void setCustomerDao(final CustomerDaoImpl customerDao)
	{
		this.customerDao = customerDao;
	}


}

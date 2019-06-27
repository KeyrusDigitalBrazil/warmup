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

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.warehousing.util.builder.PaymentInfoModelBuilder;

public class PaymentInfos {

	private Users users;

	public PaymentInfoModel PaymentInfoForNancy(final String code) {
		return PaymentInfoModelBuilder.aModel().withCode(code).withUser(getUsers().Nancy()).build();
	}

	public Users getUsers() {
		return users;
	}

	@Required
	public void setUsers(final Users users) {
		this.users = users;
	}

}

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
package de.hybris.platform.warehousing.util.builder;

import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.UserModel;

public class PaymentInfoModelBuilder {
	private final PaymentInfoModel model;

	private PaymentInfoModelBuilder() {
		model = new PaymentInfoModel();
	}

	private PaymentInfoModel getModel() {
		return this.model;
	}

	public static PaymentInfoModelBuilder aModel() {
		return new PaymentInfoModelBuilder();
	}

	public PaymentInfoModel build() {
		return getModel();
	}

	public PaymentInfoModelBuilder withCode(final String code) {
		getModel().setCode(code);
		return this;
	}

	public PaymentInfoModelBuilder withUser(final UserModel user) {
		getModel().setUser(user);
		return this;
	}

}

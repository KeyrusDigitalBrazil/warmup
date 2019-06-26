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

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;

import java.util.ArrayList;

public class ReturnRequestModelBuilder {
	private final ReturnRequestModel model;

	private ReturnRequestModelBuilder() {
		model = new ReturnRequestModel();
	}

	private ReturnRequestModel getModel() {
		return this.model;
	}

	public static ReturnRequestModelBuilder aModel() {
		return new ReturnRequestModelBuilder();
	}

	public ReturnRequestModel build() {
		return getModel();
	}

	public ReturnRequestModelBuilder withCode(final String code) {
		getModel().setCode(code);
		return this;
	}

	public ReturnRequestModelBuilder withReturnEntries(final ReturnEntryModel... returnEntries) {
		getModel().setReturnEntries(new ArrayList<ReturnEntryModel>());
		return this;
	}

	public ReturnRequestModelBuilder withOrder(final OrderModel order) {
		getModel().setOrder(order);
		return this;
	}

}

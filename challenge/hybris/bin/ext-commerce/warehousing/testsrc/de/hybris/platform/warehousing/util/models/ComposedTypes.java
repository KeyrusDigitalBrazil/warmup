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

import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.warehousing.util.builder.ComposedTypeModelBuilder;
import org.springframework.beans.factory.annotation.Required;


public class ComposedTypes extends AbstractItems<ComposedTypeModel>
{
	public static final String CS_CUSTOMER_EVENT = "CsCustomerEvent";

	private TypeService typeService;

	public ComposedTypeModel customerEvent()
	{
		return getOrSaveAndReturn(() -> getTypeService().getComposedTypeForCode(CS_CUSTOMER_EVENT),
				() -> ComposedTypeModelBuilder.aModel()
						.withCode(CS_CUSTOMER_EVENT)
						.withName(CS_CUSTOMER_EVENT)
						.build());
	}

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

}

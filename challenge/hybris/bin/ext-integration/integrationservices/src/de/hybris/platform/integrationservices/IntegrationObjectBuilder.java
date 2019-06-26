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
 */
package de.hybris.platform.integrationservices;

import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;

import java.util.HashSet;
import java.util.Set;

/**
 * A builder for {@link IntegrationObjectModel}
 */
public class IntegrationObjectBuilder
{
	private String code;
	private Set<IntegrationObjectItemModel> items = new HashSet<>();

	/**
	 * Get a new instance of the builder
	 *
	 * @return IntegrationObjectBuilder
	 */
	public static IntegrationObjectBuilder integrationObject()
	{
		return new IntegrationObjectBuilder();
	}

	public IntegrationObjectBuilder withCode(final String code)
	{
		this.code = code;
		return this;
	}

	/**
	 * Adds an {@link IntegrationObjectItemModel} from the specification to the integration object
	 *
	 * @param spec Specification to an IntegrationObjectItemModel
	 * @return IntegrationObjectBuilder
	 */
	public IntegrationObjectBuilder addIntegrationObjectItem(final IntegrationObjectItemBuilder spec)
	{
		return addIntegrationObjectItem(spec.build());
	}

	/**
	 * Adds an {@link IntegrationObjectItemModel} to the integration object
	 * 
	 * @param item Item to add
	 * @return IntegrationObjectBuilder
	 */
	public IntegrationObjectBuilder addIntegrationObjectItem(final IntegrationObjectItemModel item)
	{
		items.add(item);
		return this;
	}

	/**
	 * Each time build() is called, a new instance of the the {@link IntegrationObjectModel} is returned
	 * with the same properties that were set.
	 *
	 * @return IntegrationObjectModel
	 */
	public IntegrationObjectModel build()
	{
		final IntegrationObjectModel obj = new IntegrationObjectModel();
		items.forEach(integrationObjectItemModel -> integrationObjectItemModel.setIntegrationObject(obj));
		obj.setCode(code);
		obj.setItems(items);
		return obj;
	}
}

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

import de.hybris.platform.basecommerce.enums.PointOfServiceTypeEnum;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import com.google.common.collect.Lists;


/**
 * You cannot set the relation to {@link AddressModel} from this builder, instead you have to use
 * {@link AddressModelBuilder#withOwner()}.
 */
public class PointOfServiceModelBuilder
{
	private final PointOfServiceModel model;

	private PointOfServiceModelBuilder()
	{
		model = new PointOfServiceModel();
	}

	private PointOfServiceModel getModel()
	{
		return this.model;
	}

	public static PointOfServiceModelBuilder aModel()
	{
		return new PointOfServiceModelBuilder();
	}

	public PointOfServiceModel build()
	{
		return getModel();
	}

	public PointOfServiceModelBuilder withName(final String name)
	{
		getModel().setName(name);
		return this;
	}

	public PointOfServiceModelBuilder withType(final PointOfServiceTypeEnum type)
	{
		getModel().setType(type);
		return this;
	}

	public PointOfServiceModelBuilder withBaseStore(final BaseStoreModel baseStore)
	{
		getModel().setBaseStore(baseStore);
		return this;
	}

	public PointOfServiceModelBuilder withWarehouses(final WarehouseModel... warehouses)
	{
		getModel().setWarehouses(Lists.newArrayList(warehouses));
		return this;
	}

	public PointOfServiceModelBuilder withAddress(final AddressModel address)
	{
		getModel().setAddress(address);
		return this;
	}

	public PointOfServiceModelBuilder withLatitude(final Double latitude)
	{
		getModel().setLatitude(latitude);
		return this;
	}

	public PointOfServiceModelBuilder withLongitude(final Double longitude)
	{
		getModel().setLongitude(longitude);
		return this;
	}

}

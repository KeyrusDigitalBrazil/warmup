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

import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Arrays;
import java.util.Locale;

import com.google.common.collect.Sets;


/**
 * You cannot set the relation to {@link PointOfServiceModel} from this builder, instead you have to use
 * {@link PointOfServiceModelBuilder#withWarehouses()}.
 */
public class WarehouseModelBuilder
{
	private final WarehouseModel model;

	private WarehouseModelBuilder()
	{
		this.model = new WarehouseModel();
	}

	private WarehouseModelBuilder(final WarehouseModel model)
	{
		this.model = model;
	}

	private WarehouseModel getModel()
	{
		return this.model;
	}

	public static WarehouseModelBuilder aModel()
	{
		return new WarehouseModelBuilder();
	}

	public static WarehouseModelBuilder fromModel(final WarehouseModel model)
	{
		return new WarehouseModelBuilder(model);
	}

	public WarehouseModel build()
	{
		return getModel();
	}

	public WarehouseModelBuilder withCode(final String code)
	{
		getModel().setCode(code);
		return this;
	}

	public WarehouseModelBuilder withIsAllowRestock(final Boolean isAllowRestock)
	{
		getModel().setIsAllowRestock(isAllowRestock);
		return this;
	}

	public WarehouseModelBuilder withName(final String name, final Locale locale)
	{
		getModel().setName(name, locale);
		return this;
	}

	public WarehouseModelBuilder withVendor(final VendorModel vendor)
	{
		getModel().setVendor(vendor);
		return this;
	}

	public WarehouseModelBuilder withDefault(final Boolean _default)
	{
		getModel().setDefault(_default);
		return this;
	}

	public WarehouseModelBuilder withBaseStores(final BaseStoreModel... baseStores)
	{
		getModel().setBaseStores(Arrays.asList(baseStores));
		return this;
	}

	public WarehouseModelBuilder withDeliveryModes(final DeliveryModeModel... deliveryModes)
	{
		getModel().setDeliveryModes(Sets.newHashSet(deliveryModes));
		return this;
	}

	public WarehouseModelBuilder withExternal(final Boolean isExternal)
	{
		getModel().setExternal(isExternal);
		return this;
	}

}

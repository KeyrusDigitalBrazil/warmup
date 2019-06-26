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

import java.util.Locale;
import java.util.UUID;
import de.hybris.platform.ordersplitting.daos.WarehouseDao;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.warehousing.util.builder.WarehouseModelBuilder;
import org.springframework.beans.factory.annotation.Required;


public class Warehouses extends AbstractItems<WarehouseModel>
{
	public static final String CODE_MONTREAL = "montreal";
	public static final String CODE_BOSTON = "boston";
	public static final String CODE_TORONTO = "toronto";
	public static final String CODE_GRIFFINTOWN = "griffintown";
	public static final String CODE_PARIS = "paris";
	public static final String CODE_MONTREAL_EXTERNAL = "montrealExternal";


	private WarehouseDao warehouseDao;
	private BaseStores baseStores;
	private Vendors vendors;
	private DeliveryModes deliveryModes;

	public WarehouseModel Montreal()
	{
		return getFromCollectionOrSaveAndReturn(() -> getWarehouseDao().getWarehouseForCode(CODE_MONTREAL),
				() -> WarehouseModelBuilder.fromModel(Default())
						.withCode(CODE_MONTREAL)
						.withName(CODE_MONTREAL, Locale.ENGLISH)
						.withDeliveryModes(getDeliveryModes().Pickup(), getDeliveryModes().Regular())
						.build());
	}

	public WarehouseModel Toronto()
	{
		return getFromCollectionOrSaveAndReturn(() -> getWarehouseDao().getWarehouseForCode(CODE_TORONTO),
				() -> WarehouseModelBuilder.fromModel(Default())
						.withCode(CODE_TORONTO)
						.withName(CODE_TORONTO, Locale.ENGLISH)
						.withDeliveryModes(getDeliveryModes().Pickup(), getDeliveryModes().Regular())
						.build());
	}

	public WarehouseModel Boston()
	{
		return getFromCollectionOrSaveAndReturn(() -> getWarehouseDao().getWarehouseForCode(CODE_BOSTON),
				() -> WarehouseModelBuilder.fromModel(Default())
						.withCode(CODE_BOSTON)
						.withName(CODE_BOSTON, Locale.ENGLISH)
						.withDeliveryModes(getDeliveryModes().Pickup(), getDeliveryModes().Regular())
						.build());
	}

	public WarehouseModel Paris()
	{
		return getFromCollectionOrSaveAndReturn(() -> getWarehouseDao().getWarehouseForCode(CODE_PARIS),
				() -> WarehouseModelBuilder.fromModel(Default())
						.withCode(CODE_PARIS)
						.withName(CODE_PARIS, Locale.ENGLISH)
						.withDeliveryModes(getDeliveryModes().Pickup(), getDeliveryModes().Regular())
						.withExternal(true)
						.build());
	}

	public WarehouseModel Griffintown()
	{
		return getFromCollectionOrSaveAndReturn(() -> getWarehouseDao().getWarehouseForCode(CODE_GRIFFINTOWN),
				() -> WarehouseModelBuilder.fromModel(Default())
						.withCode(CODE_GRIFFINTOWN)
						.withName(CODE_GRIFFINTOWN, Locale.ENGLISH)
						.withDeliveryModes(getDeliveryModes().Pickup())
						.build());
	}

	public WarehouseModel Montreal_External()
	{
		return getFromCollectionOrSaveAndReturn(() -> getWarehouseDao().getWarehouseForCode(CODE_MONTREAL_EXTERNAL),
				() -> WarehouseModelBuilder.fromModel(Default())
						.withCode(CODE_MONTREAL_EXTERNAL)
						.withName(CODE_MONTREAL_EXTERNAL, Locale.ENGLISH)
						.withDeliveryModes(getDeliveryModes().Pickup(), getDeliveryModes().Regular())
						.withExternal(true)
						.build());
	}

	public WarehouseModel Random()
	{
		final String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		return getFromCollectionOrSaveAndReturn(() -> getWarehouseDao().getWarehouseForCode(uuid), //
				() -> WarehouseModelBuilder.fromModel(Default()) //
						.withCode(uuid) //
						.withName(uuid, Locale.ENGLISH) //
						.withDeliveryModes(getDeliveryModes().Pickup(), getDeliveryModes().Regular()) //
						.build());
	}

	protected WarehouseModel Default()
	{
		return WarehouseModelBuilder.aModel()
				.withBaseStores(getBaseStores().NorthAmerica())
				.withDefault(Boolean.TRUE)
				.withIsAllowRestock(false)
				.withVendor(getVendors().Hybris())
				.build();
	}

	public WarehouseDao getWarehouseDao()
	{
		return warehouseDao;
	}

	@Required
	public void setWarehouseDao(final WarehouseDao warehouseDao)
	{
		this.warehouseDao = warehouseDao;
	}

	public BaseStores getBaseStores()
	{
		return baseStores;
	}

	@Required
	public void setBaseStores(final BaseStores baseStores)
	{
		this.baseStores = baseStores;
	}

	public Vendors getVendors()
	{
		return vendors;
	}

	@Required
	public void setVendors(final Vendors vendors)
	{
		this.vendors = vendors;
	}

	public DeliveryModes getDeliveryModes()
	{
		return deliveryModes;
	}

	@Required
	public void setDeliveryModes(final DeliveryModes deliveryModes)
	{
		this.deliveryModes = deliveryModes;
	}

}

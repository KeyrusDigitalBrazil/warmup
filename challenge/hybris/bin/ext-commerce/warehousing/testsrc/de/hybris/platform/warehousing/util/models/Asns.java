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

import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.warehousing.enums.AsnStatus;
import de.hybris.platform.warehousing.model.AdvancedShippingNoticeEntryModel;
import de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel;
import de.hybris.platform.warehousing.util.builder.AsnModelBuilder;
import de.hybris.platform.warehousing.util.dao.impl.AsnDaoImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


public class Asns extends AbstractItems<AdvancedShippingNoticeModel>
{
	public static final String INTERNAL_ID_CAMERA_BOSTON = "B0006";
	public static final String INTERNAL_ID_CAMERA_MEMORY_CARD_BOSTON = "B0002";
	public static final String INTERNAL_ID_MEMORY_CARD_BOSTON = "B0003";
	public static final String INTERNAL_ID_CAMERA_MONTREAL = "B0004";
	public static final String INTERNAL_ID_MEMORY_CARD_MONTREAL = "B0005";
	public static final String INTERNAL_ID = "B0001";

	public static final String EXTERNAL_ID = "EXT123";
	public static final AsnStatus STATUS = AsnStatus.CREATED;


	private AsnDaoImpl asnsDao;
	private PointsOfService pointsOfService;
	private Warehouses warehouses;
	private AsnEntries asnEntries;

	public AdvancedShippingNoticeModel CameraAsn_Boston()
	{
		final AdvancedShippingNoticeModel advancedShippingNotice = getOrCreateAsn(INTERNAL_ID_CAMERA_BOSTON, new Date(),
				getWarehouses().Boston());
		advancedShippingNotice.setAsnEntries(Collections.singletonList(asnEntries.CameraEntry()));
		advancedShippingNotice.getAsnEntries().forEach(asnEntry -> asnEntry.setAsn(advancedShippingNotice));
		return advancedShippingNotice;
	}

	public AdvancedShippingNoticeModel CameraAsn_Montreal()
	{
		final AdvancedShippingNoticeModel advancedShippingNotice = getOrCreateAsn(INTERNAL_ID_CAMERA_MONTREAL, new Date(),
				getWarehouses().Montreal());
		advancedShippingNotice.setAsnEntries(Collections.singletonList(asnEntries.CameraEntry()));
		advancedShippingNotice.getAsnEntries().forEach(asnEntry -> asnEntry.setAsn(advancedShippingNotice));
		return advancedShippingNotice;
	}

	public AdvancedShippingNoticeModel CameraAndMemoryCardAsn_Boston()
	{
		final AdvancedShippingNoticeModel advancedShippingNotice = getOrCreateAsn(INTERNAL_ID_CAMERA_MEMORY_CARD_BOSTON, new Date(),
				getWarehouses().Boston());
		final List<AdvancedShippingNoticeEntryModel> asnEntriesList = new ArrayList<>();
		asnEntriesList.add(asnEntries.CameraEntry());
		asnEntriesList.add(asnEntries.MemoryCardEntry());
		advancedShippingNotice.setAsnEntries(asnEntriesList);
		advancedShippingNotice.getAsnEntries().forEach(asnEntry -> asnEntry.setAsn(advancedShippingNotice));
		return advancedShippingNotice;
	}

	public AdvancedShippingNoticeModel EXT123(final Date date)
	{
		return getOrCreateAsn(INTERNAL_ID, date, getWarehouses().Boston());
	}

	protected AdvancedShippingNoticeModel getOrCreateAsn(final String internalId, final Date releaseDate,
			final WarehouseModel warehouse)
	{
		return getOrSaveAndReturn(() -> getAsnsDao().getByCode(internalId),
				() -> AsnModelBuilder.aModel().withInternalId(internalId).withExternalId(EXTERNAL_ID).withStatus(STATUS)
						.withPoS(getPointsOfService().Boston()).withWarehouse(warehouse).withReleaseDate(releaseDate).build());
	}

	protected AsnDaoImpl getAsnsDao()
	{
		return asnsDao;
	}

	@Required
	public void setAsnsDao(final AsnDaoImpl asnsDao)
	{
		this.asnsDao = asnsDao;
	}

	protected PointsOfService getPointsOfService()
	{
		return pointsOfService;
	}

	@Required
	public void setPointsOfService(final PointsOfService pointsOfService)
	{
		this.pointsOfService = pointsOfService;
	}

	protected Warehouses getWarehouses()
	{
		return warehouses;
	}

	@Required
	public void setWarehouses(final Warehouses warehouses)
	{
		this.warehouses = warehouses;
	}

	protected AsnEntries getAsnEntries()
	{
		return asnEntries;
	}

	@Required
	public void setAsnEntries(final AsnEntries asnEntries)
	{
		this.asnEntries = asnEntries;
	}
}

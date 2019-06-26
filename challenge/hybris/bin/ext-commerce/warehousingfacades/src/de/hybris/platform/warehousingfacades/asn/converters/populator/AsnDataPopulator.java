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
package de.hybris.platform.warehousingfacades.asn.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.warehousing.model.AdvancedShippingNoticeEntryModel;
import de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel;
import de.hybris.platform.warehousingfacades.asn.data.AsnData;
import de.hybris.platform.warehousingfacades.asn.data.AsnEntryData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populator for populating {@link AsnEntryData} for the given {@link AdvancedShippingNoticeEntryModel}
 */
public class AsnDataPopulator implements Populator<AdvancedShippingNoticeModel, AsnData>
{
	private AbstractConverter<AdvancedShippingNoticeEntryModel, AsnEntryData> asnEntryDataConverter;

	@Override
	public void populate(final AdvancedShippingNoticeModel source, final AsnData target) throws ConversionException
	{
		if (source != null && target != null)
		{
			target.setExternalId(source.getExternalId());
			populateComment(source, target);
			populatePointOfService(source, target);
			target.setReleaseDate(source.getReleaseDate());
			populateWarehouse(source, target);
			populateAsnEntries(source, target);
			target.setInternalId(source.getInternalId());
			target.setStatus(source.getStatus());
		}
	}

	/**
	 * Populates {@link PointOfServiceModel} from {@link AdvancedShippingNoticeModel}
	 *
	 * @param source
	 * 		the {@link AdvancedShippingNoticeModel}
	 * @param target
	 * 		the {@link AsnData}
	 */
	protected void populatePointOfService(final AdvancedShippingNoticeModel source, final AsnData target)
	{
		if (source.getPointOfService() != null)
		{
			target.setPointOfServiceName(source.getPointOfService().getName());
		}
	}

	/**
	 * Populates {@link WarehouseModel} from {@link AdvancedShippingNoticeModel}
	 *
	 * @param source
	 * 		the {@link AdvancedShippingNoticeModel}
	 * @param target
	 * 		the {@link AsnData}
	 */
	protected void populateWarehouse(final AdvancedShippingNoticeModel source, final AsnData target)
	{
		if (source.getWarehouse() != null)
		{
			target.setWarehouseCode(source.getWarehouse().getCode());
		}
	}

	/**
	 * Populates comment from {@link AdvancedShippingNoticeModel}
	 *
	 * @param source
	 * 		the {@link AdvancedShippingNoticeModel}
	 * @param target
	 * 		the {@link AsnData}
	 */
	protected void populateComment(final AdvancedShippingNoticeModel source, final AsnData target)
	{
		if (CollectionUtils.isNotEmpty(source.getComments()))
		{
			target.setComment(source.getComments().get(0).getText());
		}
	}

	/**
	 * Populates {@link AsnEntryData} from {@link AdvancedShippingNoticeModel}
	 *
	 * @param source
	 * 		the {@link AdvancedShippingNoticeModel}
	 * @param target
	 * 		the {@link AsnData}
	 */
	protected void populateAsnEntries(final AdvancedShippingNoticeModel source, final AsnData target)
	{
		List<AsnEntryData> asnEntries = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(source.getAsnEntries()))
		{
			asnEntries = source.getAsnEntries().stream().map(getAsnEntryDataConverter()::convert).collect(Collectors.toList());
		}
		target.setAsnEntries(asnEntries);
	}

	protected AbstractConverter<AdvancedShippingNoticeEntryModel, AsnEntryData> getAsnEntryDataConverter()
	{
		return asnEntryDataConverter;
	}

	@Required
	public void setAsnEntryDataConverter(
			final AbstractConverter<AdvancedShippingNoticeEntryModel, AsnEntryData> asnEntryDataConverter)
	{
		this.asnEntryDataConverter = asnEntryDataConverter;
	}
}

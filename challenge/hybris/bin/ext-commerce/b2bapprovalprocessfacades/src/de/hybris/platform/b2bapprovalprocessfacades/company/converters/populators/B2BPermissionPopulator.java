/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.b2bapprovalprocessfacades.company.converters.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.b2b.enums.B2BPermissionTypeEnum;
import de.hybris.platform.b2b.model.B2BOrderThresholdPermissionModel;
import de.hybris.platform.b2b.model.B2BOrderThresholdTimespanPermissionModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionData;
import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionTypeData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;


/**
 * Converts {@link B2BPermissionModel} to {@link B2BPermissionData}.
 */
public class B2BPermissionPopulator implements Populator<B2BPermissionModel, B2BPermissionData>
{
	private Converter<CurrencyModel, CurrencyData> currencyConverter;
	private Converter<B2BPermissionTypeEnum, B2BPermissionTypeData> b2BPermissionTypeConverter;

	@Override
	public void populate(final B2BPermissionModel source, final B2BPermissionData target)
	{
		validateParameterNotNull(source, "Parameter source cannot be null.");
		validateParameterNotNull(target, "Parameter target cannot be null.");

		target.setCode(source.getCode());
		target.setActive(Boolean.TRUE.equals(source.getActive()));
		target.setB2BPermissionTypeData(getB2BPermissionTypeConverter()
				.convert(B2BPermissionTypeEnum.valueOf(source.getItemtype())));

		if (source.getUnit() != null)
		{
			populateB2BUnit(source, target);
		}

		if (source instanceof B2BOrderThresholdTimespanPermissionModel)
		{
			populateOrderThresholdPermission((B2BOrderThresholdPermissionModel) source, target);
			populateOrderThresholdTimeSpanPermission((B2BOrderThresholdTimespanPermissionModel) source, target);
		}
		else if (source instanceof B2BOrderThresholdPermissionModel)
		{
			populateOrderThresholdPermission((B2BOrderThresholdPermissionModel) source, target);
		}
	}

	protected void populateB2BUnit(final B2BPermissionModel source, final B2BPermissionData target)
	{
		final B2BUnitModel unit = source.getUnit();
		final B2BUnitData b2BUnitData = new B2BUnitData();
		b2BUnitData.setUid(unit.getUid());
		b2BUnitData.setName(unit.getLocName());
		b2BUnitData.setActive(Boolean.TRUE.equals(unit.getActive()));
		target.setUnit(b2BUnitData);
	}

	protected void populateOrderThresholdTimeSpanPermission(final B2BOrderThresholdTimespanPermissionModel source,
			final B2BPermissionData target)
	{
		if (source.getRange() != null)
		{
			target.setTimeSpan(source.getRange().name());
			target.setPeriodRange(source.getRange());
		}
	}

	protected void populateOrderThresholdPermission(final B2BOrderThresholdPermissionModel source, final B2BPermissionData target)
	{
		target.setCurrency(getCurrencyConverter().convert(source.getCurrency()));
		target.setValue(source.getThreshold());
	}

	protected Converter<CurrencyModel, CurrencyData> getCurrencyConverter()
	{
		return currencyConverter;
	}

	@Required
	public void setCurrencyConverter(final Converter<CurrencyModel, CurrencyData> currencyConverter)
	{
		this.currencyConverter = currencyConverter;
	}

	protected Converter<B2BPermissionTypeEnum, B2BPermissionTypeData> getB2BPermissionTypeConverter()
	{
		return b2BPermissionTypeConverter;
	}

	@Required
	public void setB2BPermissionTypeConverter(
			final Converter<B2BPermissionTypeEnum, B2BPermissionTypeData> b2bPermissionTypeConverter)
	{
		b2BPermissionTypeConverter = b2bPermissionTypeConverter;
	}
}

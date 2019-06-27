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
package de.hybris.platform.warehousingfacades.asn.impl;

import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.ordermanagementfacades.OmsBaseFacade;
import de.hybris.platform.util.localization.Localization;
import de.hybris.platform.warehousing.asn.service.AsnService;
import de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel;
import de.hybris.platform.warehousingfacades.asn.WarehousingAsnFacade;
import de.hybris.platform.warehousingfacades.asn.data.AsnData;

import java.util.Date;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.servicelayer.util.ServicesUtil.*;
import static org.springframework.util.Assert.isTrue;


/**
 * Default implementation of {@link WarehousingAsnFacade}
 */
public class DefaultWarehousingAsnFacade extends OmsBaseFacade implements WarehousingAsnFacade
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultWarehousingAsnFacade.class);

	private AbstractConverter<AsnData, AdvancedShippingNoticeModel> asnModelConverter;
	private AbstractConverter<AdvancedShippingNoticeModel, AsnData> asnDataConverter;
	private AsnService asnService;

	@Override
	public AsnData createAsn(final AsnData asnData)
	{
		validateAsnData(asnData);
		final AdvancedShippingNoticeModel asn = getAsnModelConverter().convert(asnData);
		getAsnService().processAsn(asn);
		getModelService().save(asn);
		LOG.debug("AdvancedShippingNoticeModel object created with internalId: {}", asn.getInternalId());
		return getAsnDataConverter().convert(asn);
	}

	@Override
	public AsnData confirmAsnReceipt(final String internalId)
	{
		validateParameterNotNull(internalId,
				Localization.getLocalizedString("warehousingfacade.advancedshippingnotice.receipt.validation.null.internalid"));
		final AdvancedShippingNoticeModel asn = getAsnService().confirmAsnReceipt(internalId);
		return getAsnDataConverter().convert(asn);
	}

	@Override
	public AsnData cancelAsn(final String internalId)
	{
		validateParameterNotNull(internalId,
				Localization.getLocalizedString("warehousingfacade.advancedshippingnotice.cancel.validation.null.internalid"));
		final AdvancedShippingNoticeModel asn = getAsnService().cancelAsn(internalId);
		return getAsnDataConverter().convert(asn);
	}

	/**
	 * Validates for null check and mandatory fields in {@link AsnData}
	 *
	 * @param asnData
	 * 		{@link AsnData} to be validated
	 */
	protected void validateAsnData(final AsnData asnData)
	{
		validateParameterNotNull(asnData,
				Localization.getLocalizedString("warehousingfacade.advancedshippingnotice.validation.null.asndata"));
		validateParameterNotNull(asnData.getExternalId(),
				Localization.getLocalizedString("warehousingfacade.advancedshippingnotice.validation.null.externalid"));
		validateParameterNotNull(asnData.getPointOfServiceName(),
				Localization.getLocalizedString("warehousingfacade.advancedshippingnotice.validation.null.pointofservicename"));
		final Date currentDate = new Date();
		isTrue(asnData.getReleaseDate() != null && currentDate.before(asnData.getReleaseDate()),
				Localization.getLocalizedString("warehousingfacade.advancedshippingnotice.validation.null.releasedate"));
		isTrue(CollectionUtils.isNotEmpty(asnData.getAsnEntries()),
				Localization.getLocalizedString("warehousingfacade.advancedshippingnotice.validation.null.asnentries"));
	}

	protected AsnService getAsnService()
	{
		return asnService;
	}

	@Required
	public void setAsnService(final AsnService asnService)
	{
		this.asnService = asnService;
	}

	protected AbstractConverter<AdvancedShippingNoticeModel, AsnData> getAsnDataConverter()
	{
		return asnDataConverter;
	}

	@Required
	public void setAsnDataConverter(final AbstractConverter<AdvancedShippingNoticeModel, AsnData> asnDataConverter)
	{
		this.asnDataConverter = asnDataConverter;
	}

	protected AbstractConverter<AsnData, AdvancedShippingNoticeModel> getAsnModelConverter()
	{
		return asnModelConverter;
	}

	@Required
	public void setAsnModelConverter(final AbstractConverter<AsnData, AdvancedShippingNoticeModel> asnModelConverter)
	{
		this.asnModelConverter = asnModelConverter;
	}
}

/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sapinvoiceaddon.facade.impl;

import de.hybris.platform.accountsummaryaddon.document.data.B2BDocumentData;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.sap.sapinvoiceaddon.document.service.B2BInvoiceService;
import de.hybris.platform.sap.sapinvoiceaddon.exception.SapInvoiceException;
import de.hybris.platform.sap.sapinvoiceaddon.facade.B2BInvoiceFacade;
import de.hybris.platform.sap.sapinvoiceaddon.model.SapB2BDocumentModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.store.services.BaseStoreService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;


/**
 *
 */
public class B2BInvoiceFacadeImpl implements B2BInvoiceFacade
{
	private static final Logger LOG = Logger.getLogger(B2BInvoiceFacadeImpl.class.getName());
	private B2BInvoiceService b2BInvoiceService;

	private B2BCommerceUnitService b2BCommerceUnitService;

	private BaseStoreService baseStoreService;


	private final String delimiterCharacter = "_";


	private Converter<SapB2BDocumentModel, B2BDocumentData> b2bInvoiceDocumentConverter;

	@Override
	public SapB2BDocumentModel getOrderForCode(final String invoiceDocumentNumber) throws SapInvoiceException
	{


		final SapB2BDocumentModel invoice = getB2BInvoiceService().getInvoiceForDocumentNumber(invoiceDocumentNumber);

		if (invoice == null)
		{
			throw new SapInvoiceException("Invoice with document number " + invoiceDocumentNumber
					+ " not found for current user in current B2Bunit");
		}
		if (invoice.getUnit().getUid() != null && !invoice.getUnit().getUid().equals(determaineSalesAreaUnitID()))
		{
			throw new SapInvoiceException("Invoice with document number " + invoiceDocumentNumber + " not Valid for the user");
		}
		return invoice;
	}

	//determine sales area unit of current customer
	private String determaineSalesAreaUnitID()
	{
		final B2BUnitModel parentUnit = getB2BCommerceUnitService().getRootUnit();
		if (baseStoreService.getCurrentBaseStore().getSAPConfiguration() == null)
		{
			LOG.debug("Sap Base Store Configuration Is Not Present");
			return null;
		}
		final String salesOrgSuffix = baseStoreService.getCurrentBaseStore().getSAPConfiguration().getSapcommon_salesOrganization();
		final String salesDistributionChannel = baseStoreService.getCurrentBaseStore().getSAPConfiguration()
				.getSapcommon_distributionChannel();
		final String salesDivision = baseStoreService.getCurrentBaseStore().getSAPConfiguration().getSapcommon_division();
		if(StringUtils.isEmpty(salesOrgSuffix) || StringUtils.isEmpty(salesDistributionChannel)|| StringUtils.isEmpty(salesDivision))
		{
			return parentUnit != null ? parentUnit.getUid() : null;
		}
		return parentUnit != null ? parentUnit.getUid() + delimiterCharacter + salesOrgSuffix + delimiterCharacter
				+ salesDistributionChannel + delimiterCharacter + salesDivision : null;
	}

	@Override
	public B2BDocumentData convertInvoiceData(final SapB2BDocumentModel invoice)

	{
		return getB2bInvoiceDocumentConverter().convert(invoice);
	}

	/**
	 * @return the b2bInvoiceDocumentConverter
	 */
	public Converter<SapB2BDocumentModel, B2BDocumentData> getB2bInvoiceDocumentConverter()
	{
		return b2bInvoiceDocumentConverter;
	}

	/**
	 * @param b2bInvoiceDocumentConverter
	 *           the b2bInvoiceDocumentConverter to set
	 */
	@Required
	public void setB2bInvoiceDocumentConverter(final Converter<SapB2BDocumentModel, B2BDocumentData> b2bInvoiceDocumentConverter)
	{
		this.b2bInvoiceDocumentConverter = b2bInvoiceDocumentConverter;
	}

	/**
	 * @return the b2BInvoiceService
	 */
	public B2BInvoiceService getB2BInvoiceService()
	{
		return b2BInvoiceService;
	}

	/**
	 * @param b2bInvoiceService
	 *           the b2BInvoiceService to set
	 */
	@Required
	public void setB2BInvoiceService(final B2BInvoiceService b2bInvoiceService)
	{
		b2BInvoiceService = b2bInvoiceService;
	}

	/**
	 * @return the b2BCommerceUnitService
	 */
	public B2BCommerceUnitService getB2BCommerceUnitService()
	{
		return b2BCommerceUnitService;
	}

	/**
	 * @param b2bCommerceUnitService
	 *           the b2BCommerceUnitService to set
	 */
	public void setB2BCommerceUnitService(final B2BCommerceUnitService b2bCommerceUnitService)
	{
		b2BCommerceUnitService = b2bCommerceUnitService;
	}

	/**
	 * @return the baseStoreService
	 */
	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}




}

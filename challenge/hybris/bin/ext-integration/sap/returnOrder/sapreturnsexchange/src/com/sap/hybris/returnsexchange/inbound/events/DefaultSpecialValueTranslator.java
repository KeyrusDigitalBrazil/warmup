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
package com.sap.hybris.returnsexchange.inbound.events;

import com.sap.hybris.returnsexchange.constants.SapreturnsexchangeConstants;
import com.sap.hybris.returnsexchange.inbound.DataHubInboundOrderHelper;

import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.header.HeaderValidationException;
import de.hybris.platform.impex.jalo.header.SpecialColumnDescriptor;
import de.hybris.platform.impex.jalo.translators.AbstractSpecialValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.security.JaloSecurityException;

public class DefaultSpecialValueTranslator extends AbstractSpecialValueTranslator{

	@SuppressWarnings("javadoc")
	public static final String HELPER_BEAN = "sapDataHubInboundReturnOrderHelper";
	private final String helperBeanName;
	private DataHubInboundOrderHelper inboundHelper;


	public DefaultSpecialValueTranslator()
	{
		this.helperBeanName = HELPER_BEAN;
	}


	@SuppressWarnings("unchecked")
	@Override
	public void init(final SpecialColumnDescriptor columnDescriptor) throws HeaderValidationException
	{
		if (getInboundHelper() == null)
		{
			setInboundHelper((DataHubInboundOrderHelper) Registry.getApplicationContext().getBean(helperBeanName));
		}
	}
	
	
	protected String getOrderCode(final Item processedItem) throws ImpExException
	{
		String orderCode = null;

		try
		{
			orderCode = processedItem.getAttribute(SapreturnsexchangeConstants.CODE).toString();
		}
		catch (final JaloSecurityException e)
		{
			throw new ImpExException(e);
		}
		return orderCode;
		
	}

	@Override
	public void validate(final String paramString) throws HeaderValidationException
	{
		// Nothing to do
	}

	@Override
	public String performExport(final Item paramItem) throws ImpExException
	{
		return null;
	}
	@Override
	public void performImport(String cellValue, Item processedItem) throws ImpExException {
		//Nothing to do
	}

	@Override
	public boolean isEmpty(final String paramString)
	{
		return false;
	}
	
	protected void setInboundHelper(final DataHubInboundOrderHelper service)
	{
		this.inboundHelper = service;
	}

	protected DataHubInboundOrderHelper getInboundHelper()
	{
		return inboundHelper;
	}
}

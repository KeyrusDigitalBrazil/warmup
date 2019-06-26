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
package de.hybris.platform.sap.sapinvoicebol.businessobject.impl;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

import de.hybris.platform.sap.core.bol.businessobject.BackendInterface;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectBase;
import de.hybris.platform.sap.core.configuration.SAPConfigurationService;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapinvoicebol.backend.SapInvoiceBackend;
import de.hybris.platform.sap.sapinvoicebol.businessobject.SapInvoiceBO;


/**
 *
 */
@BackendInterface(SapInvoiceBackend.class)
public class SapInvoiceBOImpl extends BusinessObjectBase implements SapInvoiceBO
{

	private final String prefix = "sapInvoiceBackendType";
	
	private String backendType;

	private SAPConfigurationService configurationService;

	/**
	 * @return the sapInvoiceBackend
	 * @throws BackendException
	 */
	public SapInvoiceBackend getSapInvoiceBackend() throws BackendException
	{
		return (SapInvoiceBackend) getBackendBusinessObject();
	}

	@Override
	public byte[] getPDF(final String billingDocNumber) throws BackendException
	{
		byte[] invoicePdfByteArray = null;
		//backed type can be send as property , default taking from sapbasesoteconfiguration->coredata
		if(StringUtils.isEmpty(backendType)){
					backendType = configurationService.getBackendType();
		}
		if (null != backendType && !backendType.isEmpty())
		{

			final SapInvoiceBackend sapInvoiceBackendType = (SapInvoiceBackend) genericFactory.getBean(prefix.concat(backendType));
			invoicePdfByteArray = sapInvoiceBackendType.getInvoiceInByte(billingDocNumber);
		}


		return invoicePdfByteArray;
	}
	

	@Required
	public void setConfigurationService(SAPConfigurationService configurationService) {
		this.configurationService = configurationService;
	}
	
	public String getBackendType() {
		return backendType;
	}
	
	public void setBackendType(String backendType) {
		this.backendType = backendType;
	}
}

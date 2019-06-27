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
package de.hybris.platform.sap.sappricingbol.converter;

/**
 Interface for conversion
 *
 */
public interface ConversionService
{
/**
 * Method to get SAP unit for ISO
 * 
 * @param code value
 * @return sap unit for ISO
 */
public String getSAPUnitforISO(String code);
/**
 * Method to get ISO Unit for SAP
 * @param code value
 * @return iso unit for SAP
 */
public String getISOUnitforSAP(String code);
}

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
package de.hybris.platform.sap.sappricingbol.businessobject.interf;
public interface SapPricingPartnerFunction {

	public abstract void setSoldTo(String soldTo);

	public abstract String getSoldTo();

	public abstract void setCurrency(String currency);

	public abstract String getCurrency();

	public abstract void setLanguage(String language);

	public abstract String getLanguage();
	
	

}
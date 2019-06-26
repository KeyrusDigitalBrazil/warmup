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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp;

/**
 * Triggers explicit pricing calls to SD. Is called from ERP cart or order backend objects. <br>
 *
 * @version 1.0
 */
public interface AdditionalPricing
{

	/**
	 * For identifying the implementation in backendobject-config.xml.
	 */
	String TYPE = "AdditionalPricing";

	/**
	 * Do we do an additional pricing call in cart?<br>
	 * 
	 * @return Additional call required
	 */
	boolean isPricingCallCart();

	/**
	 * Do we do an additional pricing call in order?<br>
	 * 
	 * @return Additional call required
	 */
	boolean isPricingCallOrder();

	/**
	 * Returns price type for additional SD pricing calls. Example: <br>
	 * 'H' for freight redetermination <br>
	 * 'B' carry out new pricing <br>
	 * 
	 * @return Price type as in domain KNPRS in SD
	 */
	String getPriceType();

}

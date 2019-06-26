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
package de.hybris.platform.warehousingfacades.asn;

import de.hybris.platform.warehousingfacades.asn.data.AsnData;


/**
 * Warehousing facade exposing CRUD operations on {@link de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel}
 */
public interface WarehousingAsnFacade
{
	/**
	 * API to create a {@link de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel}
	 *
	 * @param asnData
	 * 		the {@link AsnData} to create {@link de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel} in the system
	 * @return the {@link AsnData} converted from the newly created {@link de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel}
	 */
	AsnData createAsn(AsnData asnData);

	/**
	 * API to confirm receipt of the {@link de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel}
	 *
	 * @param internalId
	 * 		the {@link de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel#INTERNALID}, which needs to be confirmed
	 * @return the updated {@link AsnData}
	 */
	AsnData confirmAsnReceipt(String internalId);

	/**
	 * API to cancel  {@link de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel}
	 *
	 * @param internalId
	 * 		the {@link de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel#INTERNALID}, which needs to be cancelled
	 * @return the cancelled {@link AsnData}
	 */
	AsnData cancelAsn(String internalId);
}

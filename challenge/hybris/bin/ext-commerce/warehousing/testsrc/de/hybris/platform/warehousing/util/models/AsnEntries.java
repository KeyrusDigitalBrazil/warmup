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
package de.hybris.platform.warehousing.util.models;

import de.hybris.platform.warehousing.model.AdvancedShippingNoticeEntryModel;
import de.hybris.platform.warehousing.util.builder.AsnEntryModelBuilder;


public class AsnEntries extends AbstractItems<AdvancedShippingNoticeEntryModel>
{
	public static final String CAMERA_CODE = "camera";
	public static final String MEMORY_CARD_CODE = "memorycard";
	public static final int CAMERA_QTY = 3;
	public static final int MEMORY_CARD_QTY = 2;



	public AdvancedShippingNoticeEntryModel CameraEntry()
	{
		return getOrCreateAsnEntry(CAMERA_CODE, CAMERA_QTY);
	}
	public AdvancedShippingNoticeEntryModel MemoryCardEntry()
	{
		return getOrCreateAsnEntry(MEMORY_CARD_CODE, MEMORY_CARD_QTY);
	}

	protected AdvancedShippingNoticeEntryModel getOrCreateAsnEntry(final String productCode, final int qty)
	{
		return AsnEntryModelBuilder.aModel().withProductCode(productCode).withQuantity(qty)
						.build();
	}
}

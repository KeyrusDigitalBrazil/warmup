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
package de.hybris.platform.acceleratorservices.process.strategies;


import de.hybris.platform.acceleratorservices.cartfileupload.data.SavedCartFileUploadReportData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.CartModel;

import java.io.IOException;

public interface SavedCartFileUploadStrategy
{
    SavedCartFileUploadReportData createSavedCartFromFile(MediaModel mediaModel, CartModel cartModel) throws IOException;
}

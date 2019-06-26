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
package de.hybris.platform.sap.sapmodel.inbound;

import de.hybris.platform.jalo.Item;

public interface SapClassificationAttributeHelper {
    /**
     * Remove the product current classification attribute values before importing the new ones
     * @param cellValue
     * @param processedItem
     */
    void removeClassificationAttributeValues(String cellValue, Item processedItem);

}

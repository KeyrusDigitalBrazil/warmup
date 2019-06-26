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
package de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf;

/**
 * Interface representing the back-end view of a list of <code>Item</code>.
 * Basically this is just a convenience interface to type the
 * <code>ItemListBase</code> with <code>Item</code> as Item Type.
 * 
 * @stereotype collection
 */
public interface ItemList extends ItemListBase<Item> {

    /**
     * A bridge method between Collection and Array. The same as calling
     * <code>myList.toArray(new Item[myList.size()])</code>
     * 
     * @return ItemList as typed Array
     */
    public Item[] toItemArray();
}

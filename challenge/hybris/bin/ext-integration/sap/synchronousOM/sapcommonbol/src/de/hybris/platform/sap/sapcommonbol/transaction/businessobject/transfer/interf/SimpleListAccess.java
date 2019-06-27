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
package de.hybris.platform.sap.sapcommonbol.transaction.businessobject.transfer.interf;

import java.util.Iterator;


/**
 * Some older list based business objects are working with an underlying array list but do neither implement the List
 * interface, nor do they provide direct access to the underlying list in in a common way. Still they have the method of
 * this interface in common. So they can now be accessed in a unified way using this interface.
 *
 * @param <T>
 */

public interface SimpleListAccess<T> extends Iterable<T> {

    /**
     * returns the size of the list
     *
     * @return size of the list
     */
    int size();

    /**
     * returns an iterator
     *
     * @return iterator
     */
    
    @Override
    Iterator<T> iterator();

    /**
     * checks if the list is empty
     *
     * @return returns true, only if is the list is empty
     */
    boolean isEmpty();
}

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
package de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf;

import java.util.Map;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.Text;


/**
 * This base class encapsulates the most common features of a header (e.g. a description). <br>
 * 
 */
public interface SimpleHeader extends BusinessObject, Cloneable
{

	/**
	 * Drops the state of the object. All reference fields, except partnerList, are set to null, all primitive types are
	 * set to the default values they would have after the creation of a new instance. Use this method to reset the state
	 * to the state a newly created object would have. The advantage is, that the overhead caused by the normal object
	 * creation is omitted.
	 */
	void clear();

	/**
	 * get the dirty flag
	 * 
	 * @return isDirty must the header be read from the backend true/false
	 */
	boolean isDirty();

	/**
	 * Set the dirty flag
	 * 
	 * @param isDirty
	 *           must the header be read from the backend true/false
	 */
	void setDirty(boolean isDirty);

	/**
	 * Get description added on the header level.
	 * 
	 * @return description
	 */
	String getDescription();

	/**
	 * Get text on the header level of the document.
	 * 
	 * @return the text
	 */
	Text getText();

	/**
	 * Sets the description on the header level.
	 * 
	 * @param description
	 *           the description
	 */
	void setDescription(String description);

	/**
	 * Set the text on the header level of the document.
	 * 
	 * @param text
	 *           the text to be set
	 */
	void setText(Text text);

	/**
	 * Type safe getter for the extension map<br>
	 * 
	 * @return extension map attached to this header
	 */
	Map<String, Object> getTypedExtensionMap();

}

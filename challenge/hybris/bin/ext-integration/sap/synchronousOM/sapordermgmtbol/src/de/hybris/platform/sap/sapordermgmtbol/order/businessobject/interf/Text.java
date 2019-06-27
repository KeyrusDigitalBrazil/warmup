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
package de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf;

/**
 * Representation of user configurable texts. A text consists of an id identifying the type of the text and the textual
 * information itself.
 *
 */
public interface Text extends Cloneable
{

	/**
	 * Sets the type of the text
	 *
	 * @param id
	 *           The type to be set
	 */
	void setId(String id);

	/**
	 * Sets the text
	 *
	 * @param text
	 *           Text to be set
	 */
	void setText(String text);

	/**
	 * Reads the text
	 *
	 * @return Text
	 */
	String getText();

	/**
	 * Reads the type of the text
	 *
	 * @return Type of text
	 */
	String getId();

	/**
	 * Reads the text handle
	 *
	 * @return handle representing this text
	 */
	String getHandle();

	/**
	 * Sets the handle (which is a temporary key to identify the object)
	 *
	 * @param handle
	 *           representing this text
	 */
	void setHandle(String handle);

	/**
	 * Clears all the fields in the test object
	 */
	void clear();

	/**
	 *
	 * @return clone
	 * @see java.lang.Object#clone
	 */
	@SuppressWarnings("squid:S1161")
	Text clone();

	/**
	 * checks whether the text or the id has been changed since the last invocation of
	 * {@link Text#resetTextChanged()}<br>
	 *
	 * @return <code>true</code>, only if the text has been changed
	 */
	boolean hasTextChanged();

	/**
	 * resets the text changed status to <code>false</code><br>
	 */
	void resetTextChanged();

}
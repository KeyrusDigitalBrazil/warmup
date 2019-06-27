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
package de.hybris.platform.sap.productconfig.runtime.interf.model;

import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;


/**
 * Represents the characteristic value model.
 */
//Refactoring the constants below into an Enum or own class would be a incompatible change, which we want to avoid.
public interface CsticValueModel extends BaseModel
{

	/**
	 * value indicating that this cstic is selected, in case of yes/no decissions
	 */
	String TRUE = "Y";
	/**
	 * Indicating that this value was automatically assigned by the configuration engine.
	 */
	String AUTHOR_SYSTEM = CsticModel.AUTHOR_SYSTEM;
	/**
	 * Indicating that this value was assigned by the user, either manually, or via constraints.
	 */
	String AUTHOR_USER = CsticModel.AUTHOR_USER;
	/**
	 * Indicating that this value was assigned manually by the user.
	 */
	String AUTHOR_EXTERNAL_USER = " ";
	/**
	 * Indicating that this value was defaulted.
	 */
	String AUTHOR_EXTERNAL_DEFAULT = "8";

	/**
	 * @return the characteristic value name
	 */
	String getName();

	/**
	 * @param name
	 *           characteristic value name
	 */
	void setName(String name);

	/**
	 * @return the language dependent characteristic value name
	 */
	String getLanguageDependentName();

	/**
	 * @param languageDependentName
	 *           language dependent characteristic value name
	 */
	void setLanguageDependentName(String languageDependentName);

	/**
	 * @return true if the value is a domain value
	 */
	boolean isDomainValue();

	/**
	 * @param domainValue
	 *           flag indicating whether this value is a domain value
	 */
	void setDomainValue(boolean domainValue);

	/**
	 * @param author
	 *           characteristic value author
	 */
	void setAuthor(String author);

	/**
	 * @return the characteristic value author
	 */
	String getAuthor();

	/**
	 * @return true if the value is selectable
	 */
	boolean isSelectable();

	/**
	 * @param selectable
	 *           Flag indicating whether this value is selectable. Not supported in all provider implementations, we
	 *           don't set in the current SSC implementation.
	 */
	void setSelectable(boolean selectable);

	/**
	 * @param authorExternal
	 *           external characteristic value author - engine representation
	 */
	void setAuthorExternal(String authorExternal);

	/**
	 * @return the external characteristic value author - engine representation
	 */
	String getAuthorExternal();


	/**
	 * @return the delta price for this option, compared to the selected option
	 */
	PriceModel getDeltaPrice();

	/**
	 * @param deltaPrice
	 *           delta price for this option, compared to the selected option
	 */
	void setDeltaPrice(PriceModel deltaPrice);

	/**
	 * @return the absolute value price for this option
	 */
	PriceModel getValuePrice();

	/**
	 * @param valuePrice
	 *           absolute value price for this option
	 */
	void setValuePrice(PriceModel valuePrice);

	/**
	 * @param b
	 *           Characteristic value is of numeric type
	 */
	void setNumeric(boolean b);

	/**
	 * @return Characteristic value is of numeric type
	 */
	boolean isNumeric();

	/**
	 * @return messages valid for this characteristic value
	 */
	default Set<ProductConfigMessage> getMessages()
	{
		return Collections.emptySet();
	}

	/**
	 * @param messages
	 *           valid for this characteristic value
	 */
	default void setMessages(final Set<ProductConfigMessage> messages)
	{
		throw new NotImplementedException();
	}

	/**
	 * Get the long text description for a cstic value, which will be displayed under the cstic name in the UI
	 *
	 * @return The long text value
	 */
	default String getLongText()
	{
		return "";
	}

	/**
	 * Set the long text, which will be displayed under the Cstic value name in the UI
	 *
	 * @param longText
	 *           Description for the cstic
	 */
	default void setLongText(final String longText)
	{
		throw new NotImplementedException();
	}


}

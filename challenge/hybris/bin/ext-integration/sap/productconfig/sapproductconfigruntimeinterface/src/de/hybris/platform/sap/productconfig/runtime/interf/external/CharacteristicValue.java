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
package de.hybris.platform.sap.productconfig.runtime.interf.external;

/**
 * 
 * External representation of a characteristic value.
 */
public interface CharacteristicValue
{

	/**
	 * Indicate whether value is invisible
	 * 
	 * @param invisible
	 */
	void setInvisible(boolean invisible);

	/**
	 * @return Value invisible?
	 */
	boolean isInvisible();

	/**
	 * Sets instance author. Following values are possible:<br>
	 * 
	 * 1 - action<br>
	 * 2 - selection condition <br>
	 * 3 - classification <br>
	 * 4 - constraint<br>
	 * 5 - dynamic database<br>
	 * 6 - static knowledgebase (e.g. bill of materials)<br>
	 * 7 - procedure<br>
	 * 8 - default<br>
	 * A - monitoring rule<br>
	 * B - reevaluating rule<br>
	 * X - external system<br>
	 * 
	 * 
	 * @param author
	 */
	void setAuthor(String author);

	/**
	 * @return Value author
	 */
	String getAuthor();

	/**
	 * Sets language dependent text for value
	 * 
	 * @param valueText
	 */
	void setValueText(String valueText);

	/**
	 * @return Language dependent text
	 */
	String getValueText();

	/**
	 * Sets value
	 * 
	 * @param value
	 */
	void setValue(String value);

	/**
	 * @return Value
	 */
	String getValue();

	/**
	 * Sets language dependent characteristic text
	 * 
	 * @param characteristicText
	 */
	void setCharacteristicText(String characteristicText);

	/**
	 * @return Language dependent characteristic text
	 */
	String getCharacteristicText();

	/**
	 * Sets characteristic name
	 * 
	 * @param characteristic
	 */
	void setCharacteristic(String characteristic);

	/**
	 * @return Characteristic name
	 */
	String getCharacteristic();

	/**
	 * Sets ID of instance the value belongs to
	 * 
	 * @param instId
	 */
	void setInstId(String instId);

	/**
	 * 
	 * @return ID of instance the value belongs to
	 */
	String getInstId();
}

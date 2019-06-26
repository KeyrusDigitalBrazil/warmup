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

import java.util.Date;


/**
 * A general message within the context of CPQ.<br>
 * The message can be uniquely identified with key and source.
 */
public interface ProductConfigMessage
{

	/**
	 * @return the key of this message, which is unique for a given message source
	 */
	String getKey();

	/**
	 * @return localized message
	 */
	String getMessage();

	/**
	 * @return source of the message
	 */
	ProductConfigMessageSource getSource();

	/**
	 * @return severity of the message
	 */
	ProductConfigMessageSeverity getSeverity();

	/**
	 * @return sub type of the message source
	 */
	ProductConfigMessageSourceSubType getSourceSubType();

	/**
	 * @return localized extended message
	 */
	String getExtendedMessage();

	/**
	 * @return endDate of the message
	 */
	Date getEndDate();

	/**
	 *
	 * @return message type for promotion( is it oppotunity or applied promotion)
	 */
	ProductConfigMessagePromoType getPromoType();
}

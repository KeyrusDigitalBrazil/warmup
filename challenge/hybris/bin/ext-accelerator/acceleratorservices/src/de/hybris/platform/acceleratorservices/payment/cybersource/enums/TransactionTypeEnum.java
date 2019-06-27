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
package de.hybris.platform.acceleratorservices.payment.cybersource.enums;

/**
 * This Enum represents the different types of transactions that can be requested from the CyberSource Hosted Order Page Service.
 */
public enum TransactionTypeEnum
{
	// Constant names cannot be changed due to their usage in dependant extensions, thus nosonar
	authorization, // NOSONAR
	sale, // NOSONAR
	subscription, // NOSONAR
	subscription_modify, // NOSONAR
	subscription_credit, // NOSONAR
	authReversal // NOSONAR
}

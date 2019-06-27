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
package de.hybris.platform.sap.orderexchange.constants;

/**
 * Constants for Sales Conditions CSV Columns
 */
public class SalesConditionCsvColumns
{
	private SalesConditionCsvColumns() {
		throw new IllegalStateException("Utility class");
	}

	@SuppressWarnings("javadoc")
	public static final String CONDITION_COUNTER = "conditionCounter";

	@SuppressWarnings("javadoc")
	public static final String CONDITION_PRICE_QUANTITY = "conditionPriceQuantity";

	@SuppressWarnings("javadoc")
	public static final String CONDITION_UNIT_CODE = "conditionUnitCode";

	@SuppressWarnings("javadoc")
	public static final String ABSOLUTE = "absolute";

	@SuppressWarnings("javadoc")
	public static final String CONDITION_VALUE = "conditionValue";

	@SuppressWarnings("javadoc")
	public static final String CONDITION_CURRENCY_ISO_CODE = "conditionCurrencyIsoCode";

	@SuppressWarnings("javadoc")
	public static final String CONDITION_CODE = "conditionCode";

	@SuppressWarnings("javadoc")
	public static final String CONDITION_ENTRY_NUMBER = "conditionEntryNumber";
}

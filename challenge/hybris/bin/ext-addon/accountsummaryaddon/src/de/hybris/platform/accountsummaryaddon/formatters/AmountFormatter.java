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
package de.hybris.platform.accountsummaryaddon.formatters;

import java.math.BigDecimal;
import java.util.Locale;

import de.hybris.platform.core.model.c2l.CurrencyModel;

public interface AmountFormatter
{
	public String formatAmount( final BigDecimal value, final CurrencyModel currency, final Locale locale );
}

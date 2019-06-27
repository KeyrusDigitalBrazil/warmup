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
package de.hybris.platform.assistedservicefacades.util;

/**
 * "Time since" functionality utility class.
 */
public enum TimeSince
{
    MOMENT,
    SECOND,
    SECONDS,
    MINUTE,
    MINUTES,
    HOUR,
    HOURS,
    DAY,
    DAYS,
    MONTH,
    MONTHS,
    YEAR,
    YEARS;

    private long value;

    public TimeSince setValue(long value)
    {
        this.value = value;
        return this;
    }

    public long getValue()
    {
        return value;
    }
}
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
package de.hybris.platform.customerticketingc4cintegration.converters;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;

/**
 * Special strategy for json-fields naming
 * OData have special pascal format for all objects, but it ignores some special cases, like 'd' and 'results',
 *  also if we expect 2or move capitals letters from the beginning(like ID)
 */
public class ODataCaseStrategy extends PropertyNamingStrategy.UpperCamelCaseStrategy
{
    @Override
    public String translate(String input)
    {
        if ("d".equals(input) || "results".equals(input))
        {
            return input;
        }
        else if ("ID".equalsIgnoreCase(input))
        {
            return "ID";
        }
        else
        {
            return super.translate(input);
        }
    }
}

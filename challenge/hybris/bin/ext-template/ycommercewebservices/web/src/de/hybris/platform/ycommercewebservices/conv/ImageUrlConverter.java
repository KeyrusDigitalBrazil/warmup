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
package de.hybris.platform.ycommercewebservices.conv;

import com.thoughtworks.xstream.converters.SingleValueConverter;
import de.hybris.platform.ycommercewebservices.constants.YcommercewebservicesConstants;

import java.util.Optional;

public class ImageUrlConverter implements SingleValueConverter
{
    @Override
    public String toString(Object o)
    {
        return Optional.ofNullable(o)
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(this::addRootContext)
                .orElseGet(() -> null);
    }

    protected String addRootContext(final String imageUrl){
        return new StringBuilder(YcommercewebservicesConstants.V1_ROOT_CONTEXT)
                .append(imageUrl)
                .toString();
    }

    @Override
    public Object fromString(String s)
    {
        return null;
    }

    @Override
    public boolean canConvert(Class type)
    {
        return type == String.class;
    }
}

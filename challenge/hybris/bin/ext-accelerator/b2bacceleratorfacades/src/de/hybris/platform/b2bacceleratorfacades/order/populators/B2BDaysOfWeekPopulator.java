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
package de.hybris.platform.b2bacceleratorfacades.order.populators;

import de.hybris.platform.b2b.model.B2BCommentModel;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BCommentData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BDaysOfWeekData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.cronjob.enums.DayOfWeek;
import de.hybris.platform.servicelayer.type.TypeService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populates {@link B2BCommentModel} to {@link B2BCommentData}.
 */
public class B2BDaysOfWeekPopulator implements Populator<DayOfWeek, B2BDaysOfWeekData>
{
    private TypeService typeService;

    @Override
    public void populate(final DayOfWeek source, final B2BDaysOfWeekData target)
    {
        target.setCode(source.getCode());
        target.setName(getTypeService().getEnumerationValue(source).getName());
    }

    protected TypeService getTypeService()
    {
        return typeService;
    }

    @Required
    public void setTypeService(final TypeService typeService)
    {
        this.typeService = typeService;
    }
}

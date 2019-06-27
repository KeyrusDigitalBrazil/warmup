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
package de.hybris.platform.customerticketingc4cintegration.converters.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.customerticketingc4cintegration.constants.Customerticketingc4cintegrationConstants;
import de.hybris.platform.customerticketingc4cintegration.data.Note;
import de.hybris.platform.customerticketingfacades.data.TicketData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * TicketData -> Note populator,
 * Used for update calls
 *
 * @param <SOURCE>
 * @param <TARGET>
 */
public class Y2TicketMessageUpdatePopulator <SOURCE extends TicketData, TARGET extends Note> implements Populator<SOURCE , TARGET>
{
    @Override
    public void populate(SOURCE source, TARGET target) throws ConversionException
    {
        target.setLanguageCode(Customerticketingc4cintegrationConstants.LANGUAGE);
        target.setParentObjectID(source.getId());
        target.setText(source.getMessage());
        target.setTypeCode(Customerticketingc4cintegrationConstants.TYPECODE_10007);
    }
}

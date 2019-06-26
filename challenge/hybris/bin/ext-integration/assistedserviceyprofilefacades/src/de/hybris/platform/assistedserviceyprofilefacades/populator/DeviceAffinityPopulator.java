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
package de.hybris.platform.assistedserviceyprofilefacades.populator;

import de.hybris.platform.assistedserviceyprofilefacades.data.TechnologyUsedData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.yaasyprofileconnect.yaas.UserAgent;

import java.util.Map;

/**
 *
 * Populator for device affinity data.
 *
 * @param <SOURCE>
 *           Map.Entry<String,UserAgent>
 * @param <TARGET>
 *           TechnologyUsedData
 */
public class DeviceAffinityPopulator<SOURCE extends Map.Entry<String,UserAgent>, TARGET extends TechnologyUsedData>
        implements Populator<SOURCE, TARGET>
{
    @Override
    public void populate(final SOURCE affinityData, final TARGET deviceData)
    {
        final UserAgent userAgent = affinityData.getValue();
        deviceData.setBrowser(userAgent.getBrowserNoVersion());
        deviceData.setDevice(userAgent.getDeviceType());
        deviceData.setOperatingSystem(userAgent.getOperatingSystemNoVersion());
    }
}

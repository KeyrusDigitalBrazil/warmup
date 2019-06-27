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
package de.hybris.platform.assistedservicefacades;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicefacades.util.AssistedServiceUtils;
import de.hybris.platform.assistedservicefacades.util.TimeSince;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static de.hybris.platform.assistedservicefacades.util.AssistedServiceUtils.getTimeSince;


@UnitTest
public class AssistedServiceUtilsTest
{
    @Test
    public void shouldCutCardNumberCorrectly()
    {
        String number = "4444444444442424";
        String accepted = "2424";

        CreditCardPaymentInfoModel creditCardPaymentInfoModel = new CreditCardPaymentInfoModel();
        creditCardPaymentInfoModel.setNumber(number);
        CustomerModel customerModel = new CustomerModel();
        customerModel.setDefaultPaymentInfo(creditCardPaymentInfoModel);

        Assert.assertEquals(accepted, AssistedServiceUtils.getCardLastFourDigits(customerModel));
    }

    @Test
    public void getTimeSinceTest()
    {
        // moments
        TimeSince timeSince = getTimeSince(new Date(System.currentTimeMillis() - 100));
        Assert.assertEquals(timeSince, TimeSince.MOMENT);

        // second
        timeSince = getTimeSince(new Date(System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(1)));
        Assert.assertEquals(timeSince, TimeSince.SECOND);

        // seconds
        timeSince = getTimeSince(new Date(System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(59)));
        Assert.assertEquals(timeSince, TimeSince.SECONDS);
        Assert.assertEquals(timeSince.getValue(), 59);

        // minute
        timeSince = getTimeSince(new Date(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(1)));
        Assert.assertEquals(timeSince, TimeSince.MINUTE);

        // minutes
        timeSince = getTimeSince(new Date(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(59)));
        Assert.assertEquals(timeSince, TimeSince.MINUTES);
        Assert.assertEquals(timeSince.getValue(), 59);

        // day
        timeSince = getTimeSince(new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)));
        Assert.assertEquals(timeSince, TimeSince.DAY);

        // days
        timeSince = getTimeSince(new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(29)));
        Assert.assertEquals(timeSince, TimeSince.DAYS);
        Assert.assertEquals(timeSince.getValue(), 29);

        // month
        timeSince = getTimeSince(new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(33)));
        Assert.assertEquals(timeSince, TimeSince.MONTH);

        // months
        timeSince = getTimeSince(new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(65)));
        Assert.assertEquals(timeSince, TimeSince.MONTHS);
        Assert.assertEquals(timeSince.getValue(), 2);

        // year
        timeSince = getTimeSince(new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(380)));
        Assert.assertEquals(timeSince, TimeSince.YEAR);

        // years
        timeSince = getTimeSince(new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1100)));
        Assert.assertEquals(timeSince, TimeSince.YEARS);
        Assert.assertEquals(timeSince.getValue(), 3);
    }
}

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
package de.hybris.platform.personalizationpromotionsweb.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.personalizationpromotions.model.CxPromotionActionModel;
import de.hybris.platform.personalizationpromotionsweb.data.CxPromotionActionData;
import org.junit.Assert;
import org.junit.Test;

@UnitTest
public class CxPromotionActionPopulatorTest
{
    private static final String PROMOTION_ID = "testPromotionID";

    private final CxPromotionActionPopulator cxPromotionActionPopulator = new CxPromotionActionPopulator();

    @Test
    public void shouldPopulateValue()
    {
        //given
        final CxPromotionActionData target = new CxPromotionActionData();
        final CxPromotionActionModel source = new CxPromotionActionModel();
        source.setPromotionId(PROMOTION_ID);

        //when
        cxPromotionActionPopulator.populate(source, target);

        //then
        Assert.assertEquals(PROMOTION_ID, target.getPromotionId());
    }

    @Test
    public void shouldPopulateNull()
    {
        //given
        final CxPromotionActionData target = new CxPromotionActionData();
        final CxPromotionActionModel source = new CxPromotionActionModel();

        //when
        cxPromotionActionPopulator.populate(source, target);

        //then
        Assert.assertNull(target.getPromotionId());
    }
}

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
public class CxPromotionActionReversePopulatorTest
{

    private static final String PROMOTION_ID = "testPromotionID";

    private final CxPromotionActionReversePopulator cxPromotionActionReversePopulator = new CxPromotionActionReversePopulator();


    @Test
    public void shouldPopulate()
    {
        final CxPromotionActionData source = new CxPromotionActionData();
        source.setPromotionId(PROMOTION_ID);
        final CxPromotionActionModel target = new CxPromotionActionModel();
        cxPromotionActionReversePopulator.populate(source, target);

        Assert.assertEquals(PROMOTION_ID, target.getPromotionId());
    }
}

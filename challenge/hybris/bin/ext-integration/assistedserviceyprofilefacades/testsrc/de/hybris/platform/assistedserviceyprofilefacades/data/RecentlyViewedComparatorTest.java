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
package de.hybris.platform.assistedserviceyprofilefacades.data;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.yaasyprofileconnect.yaas.Affinity;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.Map;

import static org.junit.Assert.*;

@UnitTest
public class RecentlyViewedComparatorTest
{
    private RecentlyViewedComparator comparator = new RecentlyViewedComparator();

    @Test
    public void compare() throws Exception
    {
        Affinity firstAff = new Affinity();
        Affinity secondAff = new Affinity();

        firstAff.setRecentScore(new BigDecimal(1));
        secondAff.setRecentScore(new BigDecimal(2));
        final AbstractMap.SimpleEntry<String, Affinity> firstAffEntry = new AbstractMap.SimpleEntry<>("key", firstAff);
        final AbstractMap.SimpleEntry<String, Affinity> secondAffEntry = new AbstractMap.SimpleEntry<>("key", secondAff);

        assertTrue(comparator.compare(firstAffEntry, secondAffEntry) == secondAff.getRecentScore().compareTo(firstAff.getRecentScore()));
    }

}
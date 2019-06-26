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
package de.hybris.platform.personalizationservices.trigger.dao.impl;

import com.google.common.collect.Sets;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.personalizationservices.AbstractCxServiceTest;
import de.hybris.platform.personalizationservices.model.CxSegmentModel;
import de.hybris.platform.personalizationservices.model.CxVariationModel;
import de.hybris.platform.personalizationservices.segment.CxSegmentService;
import de.hybris.platform.personalizationservices.trigger.dao.CxSegmentTriggerDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@IntegrationTest
public class DefaultCxSegmentTriggerIntegrationTest extends AbstractCxServiceTest {


    private static final String VARIATION_1 = "variation1";
    private static final String VARIATION_2 = "variation2";
    private static final String VARIATION_3 = "variation3";
    private static final String VARIATION_4 = "variation4";
    private static final String VARIATION_5 = "variation5";
    private static final String VARIATION_6 = "variation6";
    private static final String VARIATION_7 = "variation7";
    private static final String VARIATION_8 = "variation8";


    private static final String SEGMENT_1 = "segment1";
    private static final String SEGMENT_2 = "segment2";
    private static final String SEGMENT_3 = "segment3";

    @Resource(name = "cxSegmentTriggerDao")
    private CxSegmentTriggerDao cxSegmentTriggerDao;

    @Resource(name = "cxSegmentService")
    private CxSegmentService cxSegmentService;

    @Resource
    private CatalogVersionService catalogVersionService;

    private CatalogVersionModel catalogVersion;

    @Before
    public void setup()
    {
        catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");
    }


    @Test
    public void testFindVariationsForSegment1()
    {

        //given
        final Collection<CxSegmentModel> segmentsForCodes = cxSegmentService.getSegmentsForCodes(Sets.newHashSet(SEGMENT_1));

        //when
        final Collection<CxVariationModel> applicableVariations = cxSegmentTriggerDao.findApplicableVariations(segmentsForCodes, catalogVersion);

        //then
        assertVariations(applicableVariations, VARIATION_1, VARIATION_2, VARIATION_3, VARIATION_4);
    }

    @Test
    public void testFindVariationsForSegment2()
    {
        //given
        final Collection<CxSegmentModel> segmentsForCodes = cxSegmentService.getSegmentsForCodes(Sets.newHashSet(SEGMENT_2));

        //when
        final Collection<CxVariationModel> applicableVariations = cxSegmentTriggerDao.findApplicableVariations(segmentsForCodes, catalogVersion);

        //then
        assertVariations(applicableVariations, VARIATION_3, VARIATION_5);
    }

    @Test
    public void testFindVariationsForSegment3()
    {
        //given
        final Collection<CxSegmentModel> segmentsForCodes = cxSegmentService.getSegmentsForCodes(Sets.newHashSet(SEGMENT_3));

        //when
        final Collection<CxVariationModel> applicableVariations = cxSegmentTriggerDao.findApplicableVariations(segmentsForCodes, catalogVersion);

        //then
        assertVariations(applicableVariations, VARIATION_4, VARIATION_5);
    }

    @Test
    public void testFindVariationsForSegment1AndSegment2()
    {
        //given
        final Collection<CxSegmentModel> segmentsForCodes = cxSegmentService.getSegmentsForCodes(Sets.newHashSet(SEGMENT_1, SEGMENT_2));

        //when
        final Collection<CxVariationModel> applicableVariations = cxSegmentTriggerDao.findApplicableVariations(segmentsForCodes, catalogVersion);

        //then
        assertVariations(applicableVariations, VARIATION_1, VARIATION_2, VARIATION_3, VARIATION_4, VARIATION_5, VARIATION_6);
    }

    @Test
    public void testFindVariationsForSegment1AndSegment3()
    {
        //given
        final Collection<CxSegmentModel> segmentsForCodes = cxSegmentService.getSegmentsForCodes(Sets.newHashSet(SEGMENT_1, SEGMENT_3));

        //when
        final Collection<CxVariationModel> applicableVariations = cxSegmentTriggerDao.findApplicableVariations(segmentsForCodes, catalogVersion);

        //then
        assertVariations(applicableVariations, VARIATION_1, VARIATION_2, VARIATION_3, VARIATION_4, VARIATION_5, VARIATION_7);
    }

    @Test
    public void testFindVariationsForSegment2AndSegment3()
    {
        //given
        final Collection<CxSegmentModel> segmentsForCodes = cxSegmentService.getSegmentsForCodes(Sets.newHashSet(SEGMENT_2, SEGMENT_3));

        //when
        final Collection<CxVariationModel> applicableVariations = cxSegmentTriggerDao.findApplicableVariations(segmentsForCodes, catalogVersion);

        //then
        assertVariations(applicableVariations, VARIATION_3, VARIATION_4, VARIATION_5, VARIATION_8);
    }

    private void assertVariations(final Collection<CxVariationModel> actual, final String... expected)
    {
        final Set<String> expectedCodes = Sets.newHashSet(expected);
        Assert.assertEquals(expectedCodes, actual.stream().map(CxVariationModel::getCode).collect(Collectors.toSet()));
    }

}

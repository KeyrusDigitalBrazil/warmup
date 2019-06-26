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
/**
 *
 */
package de.hybris.platform.personalizationservices.job;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.personalizationservices.model.CxUserToSegmentModel;
import de.hybris.platform.processing.model.FlexibleSearchRetentionRuleModel;
import de.hybris.platform.retention.ItemToCleanup;
import de.hybris.platform.retention.impl.FlexibleSearchRetentionItemsProvider;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


public class CxCustomerSegmentRetentionRuleIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String RULE_CODE = "cxCustomerSegmentRetentionRule";
	private static final String USER_P1 = "p1@hybris.com";
	private static final String USER_P5 = "p5@hybris.com";
	private static final String USER_P6 = "p6@hybris.com";
	private static final String USER_P7 = "p7@hybris.com";
	private static final String USER_P8 = "p8@hybris.com";
	private static final String USER_P9 = "p9@hybris.com";
	private static final String SEGMENT1 = "segment1";
	private static final String SEGMENT2 = "segment2";
	private static final String BASESITE2 = "testSite2";
	private static final String BASESITE3 = "testSite3";
	private static final String BASESITE5 = "testSite5";

	@Resource
	ModelService modelService;

	@Resource
	FlexibleSearchService flexibleSearchService;

	private FlexibleSearchRetentionItemsProvider itemProvider;

	@Before
	public void setup() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importData(new ClasspathImpExResource("/personalizationservices/test/testdata_consent.impex", "UTF-8"));
		importData(new ClasspathImpExResource("/impex/essentialdata_personalizationservices_cron.impex", "UTF-8"));

		itemProvider = getItemProvider();
	}

	private FlexibleSearchRetentionItemsProvider getItemProvider()
	{
		final FlexibleSearchRetentionRuleModel example = new FlexibleSearchRetentionRuleModel();
		example.setCode(RULE_CODE);
		final FlexibleSearchRetentionRuleModel ruleModel = flexibleSearchService.getModelByExample(example);

		final FlexibleSearchRetentionItemsProvider provider = new FlexibleSearchRetentionItemsProvider(ruleModel);
		provider.setFlexibleSearchService(flexibleSearchService);
		provider.setBatchSize(100);
		return provider;
	}

	@Test
	public void test()
	{
		//when
		final List<ItemToCleanup> itemsToCleanup = itemProvider.nextItemsForCleanup();

		//then
		assertNotNull(itemsToCleanup);
		assertEquals(12, itemsToCleanup.size());

		assertUserToSegmentEquals(USER_P1, SEGMENT1, BASESITE2, itemsToCleanup);
		assertUserToSegmentEquals(USER_P1, SEGMENT2, BASESITE2, itemsToCleanup);
		assertUserToSegmentEquals(USER_P5, SEGMENT1, BASESITE2, itemsToCleanup);
		assertUserToSegmentEquals(USER_P5, SEGMENT2, BASESITE2, itemsToCleanup);
		assertUserToSegmentEquals(USER_P6, SEGMENT1, BASESITE2, itemsToCleanup);
		assertUserToSegmentEquals(USER_P6, SEGMENT2, BASESITE2, itemsToCleanup);
		assertUserToSegmentEquals(USER_P7, SEGMENT1, BASESITE3, itemsToCleanup);
		assertUserToSegmentEquals(USER_P7, SEGMENT2, BASESITE3, itemsToCleanup);
		assertUserToSegmentEquals(USER_P8, SEGMENT1, BASESITE3, itemsToCleanup);
		assertUserToSegmentEquals(USER_P8, SEGMENT2, BASESITE3, itemsToCleanup);
		assertUserToSegmentEquals(USER_P9, SEGMENT1, BASESITE5, itemsToCleanup);
		assertUserToSegmentEquals(USER_P9, SEGMENT2, BASESITE5, itemsToCleanup);
	}

	private void assertUserToSegmentEquals(final String expectedUser, final String expectedSegment, final String expectedBaseSite,
			final List<ItemToCleanup> items)
	{
		final Optional<CxUserToSegmentModel> model = items.stream() //
				.filter(i -> CxUserToSegmentModel._TYPECODE.equals(i.getItemType())) //
				.map(i -> (CxUserToSegmentModel) modelService.get(i.getPk())) //
				.filter(m -> expectedUser.equals(m.getUser().getUid())) //
				.filter(m -> expectedSegment.equals(m.getSegment().getCode())) //
				.filter(m -> expectedBaseSite.equals(m.getBaseSite().getUid())) //
				.findAny();

		assertTrue("Could not find item [" + expectedUser + "," + expectedSegment + "," + expectedBaseSite + "]",
				model.isPresent());
	}
}

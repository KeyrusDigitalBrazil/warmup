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
package de.hybris.platform.cms2.version.converter.impl;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cms2.common.functions.Converter;
import de.hybris.platform.cms2.model.CMSVersionModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2.version.AbstractCMSVersionIntegrationTest;
import de.hybris.platform.core.model.ItemModel;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.junit.Assert;
import org.junit.Test;

@IntegrationTest
public class DefaultCMSVersionToItemModelRollbackConverterIntegrationTest
		extends AbstractCMSVersionIntegrationTest
{
	private static final String MAIN_TEMPLATE_UID = "mainTemplate";
	private static final String BODY_SLOT = "BodySlot";
	private static final String LINK_UUID = "LinkInSlot";

	private static final String GENERATED_UID_PREFIX = "comp_";
	private static final String SHARED_SLOT_ID = "SharedSlot";
	private static final String PAGE_SLOT_ID = "BodySlot";
	private static final String LINK_COMPONENT_UID = "LinkInSlot";
	private static final String LINK_COMPONENT_NAME = "LinkInSlotName";

	@Resource
	private Converter<CMSVersionModel, ItemModel> cmsVersionToItemModelRollbackConverter;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private CMSPageService cmsPageService;

	@Test
	public void testWillConvertCMSVersionModelToContentPageModel()
	{
		// GIVEN
		contentPageCMSVersion = cmsVersionService.createRevisionForItem(contentPage);
		modelService.save(contentPageCMSVersion);

		// WHEN
		final ContentPageModel contentPageFromVersion = (ContentPageModel) cmsVersionToItemModelRollbackConverter
				.convert(contentPageCMSVersion);

		// THEN
		Assert.assertThat(contentPageFromVersion.getUid(), is(HOMEPAGE));
		Assert.assertThat(contentPageFromVersion.getMasterTemplate().getUid(), is(MAIN_TEMPLATE_UID));
		Assert.assertThat(contentPageFromVersion.getCatalogVersion().getVersion(), is(CATALOG_VERSION));
		Assert.assertThat(contentPageFromVersion.getCatalogVersion().getCatalog().getId(), is(CMS_CATALOG));
	}

	@Test
	public void testWillConvertCMSVersionModelToContentPageModelWithCustomAttributes()
	{
		// GIVEN
		contentPageCMSVersion = cmsVersionService.createRevisionForItem(contentPage);
		modelService.saveAll(contentPageCMSVersion);

		// WHEN
		final ContentPageModel contentPageFromVersion = (ContentPageModel) cmsVersionToItemModelRollbackConverter
				.convert(contentPageCMSVersion);
		final Collection<ContentSlotData> contentSlotsForPage = cmsPageService.getContentSlotsForPage(contentPageFromVersion);
		final Optional<ContentSlotData> optContentSlot = contentSlotsForPage.stream().findAny();
		final Optional<AbstractCMSComponentModel> optBannerComponent = optContentSlot.get().getCMSComponents().stream().findAny();

		// THEN
		Assert.assertThat(optContentSlot.get().getUid(), is(BODY_SLOT));
		Assert.assertThat(optBannerComponent.get().getUid(), is(LINK_UUID));
	}

	@Test
	public void givenComponentIsShared_WhenPageIsConverted_ThenTheComponentGetsCloned()
	{
		// GIVEN
		addComponentToSlot(LINK_COMPONENT_UID, SHARED_SLOT_ID); // Make component shared

		contentPageCMSVersion = cmsVersionService.createRevisionForItem(contentPage);
		modelService.save(contentPageCMSVersion);

		// WHEN
		cmsVersionToItemModelRollbackConverter.convert(contentPageCMSVersion);
		modelService.saveAll();

		// THEN
		final ContentSlotModel slot = getSlotById(PAGE_SLOT_ID);
		final List<AbstractCMSComponentModel> components = slot.getCmsComponents();
		final AbstractCMSComponentModel component = components.iterator().next();

		assertThat(component.getUid(), is(not(LINK_COMPONENT_UID)));
		assertTrue(component.getUid().startsWith(GENERATED_UID_PREFIX));
		assertTrue(component.getName().startsWith(LINK_COMPONENT_NAME + " "));
	}

	@Test
	public void givenComponentIsNotShared_WhenPageIsConverted_ThenTheComponentGetsRollbacked()
	{
		// GIVEN
		contentPageCMSVersion = cmsVersionService.createRevisionForItem(contentPage);
		modelService.save(contentPageCMSVersion);

		// WHEN
		cmsVersionToItemModelRollbackConverter.convert(contentPageCMSVersion);
		modelService.saveAll();

		// THEN
		final AbstractCMSComponentModel expectedComponent = getComponentById(LINK_COMPONENT_UID);
		final ContentSlotModel slot = getSlotById(PAGE_SLOT_ID);
		final List<AbstractCMSComponentModel> components = slot.getCmsComponents();
		assertThat(components, containsInAnyOrder(expectedComponent));
	}

	protected void addComponentToSlot(final String componentId, final String slotId)
	{
		final AbstractCMSComponentModel component = getComponentById(componentId);
		final ContentSlotModel slotModel = getSlotById(slotId);

		slotModel.setCmsComponents(Collections.singletonList(component));
		modelService.saveAll();
	}

	protected ContentSlotModel getSlotById(final String slotId)
	{
		ContentSlotModel contentSlot = new ContentSlotModel();
		contentSlot.setUid(slotId);
		contentSlot = flexibleSearchService.getModelByExample(contentSlot);

		return contentSlot;
	}

	protected AbstractCMSComponentModel getComponentById(final String componentId)
	{
		AbstractCMSComponentModel componentModel = new AbstractCMSComponentModel();
		componentModel.setUid(componentId);
		componentModel = flexibleSearchService.getModelByExample(componentModel);

		return componentModel;
	}
}


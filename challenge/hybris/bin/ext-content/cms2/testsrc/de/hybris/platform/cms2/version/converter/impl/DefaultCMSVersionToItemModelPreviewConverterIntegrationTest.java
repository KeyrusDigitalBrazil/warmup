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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cms2.common.functions.Converter;
import de.hybris.platform.cms2.model.CMSVersionModel;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cms2.version.AbstractCMSVersionIntegrationTest;
import de.hybris.platform.cms2.version.service.CMSVersionSessionContextProvider;
import de.hybris.platform.core.model.ItemModel;

import javax.annotation.Resource;

import org.junit.Test;


@IntegrationTest
public class DefaultCMSVersionToItemModelPreviewConverterIntegrationTest
		extends AbstractCMSVersionIntegrationTest
{
	private static final String MAIN_TEMPLATE_UID = "mainTemplate";
	private static final String CONTENT_SLOT_0_UID = "BodySlot";
	private static final String CONTENT_SLOT_POSITION = "0";
	private static final String LINK_UID = "LinkInSlot";
	@Resource
	private Converter<CMSVersionModel, ItemModel> cmsVersionToItemModelPreviewConverter;

	@Resource
	private CMSVersionSessionContextProvider cmsVersionSessionContextProvider;

	@Test
	public void testWillConvertCMSVersionModelToContentPageModel()
	{
		// GIVEN
		contentPageCMSVersion = cmsVersionService.createRevisionForItem(contentPage);
		modelService.save(contentPageCMSVersion);

		// WHEN
		final ContentPageModel contentPageFromVersion = (ContentPageModel) cmsVersionToItemModelPreviewConverter
				.convert(contentPageCMSVersion);

		// THEN
		assertThat(contentPageFromVersion.getUid(), is(HOMEPAGE));
		assertThat(contentPageFromVersion.getMasterTemplate().getUid(), is(MAIN_TEMPLATE_UID));
		assertThat(contentPageFromVersion.getCatalogVersion().getVersion(), is(CATALOG_VERSION));
		assertThat(contentPageFromVersion.getCatalogVersion().getCatalog().getId(), is(CMS_CATALOG));
	}

	@Test
	public void testWillConvertCMSVersionModelToContentPageModelWithCustomAttributes()
	{
		// GIVEN
		contentPageCMSVersion = cmsVersionService.createRevisionForItem(contentPage);
		modelService.saveAll(contentPageCMSVersion);

		// WHEN
		cmsVersionToItemModelPreviewConverter.convert(contentPageCMSVersion);

		final CMSItemModel cachedContentSlot = getCMSItemModelFromCacheByUid(CONTENT_SLOT_0_UID);
		final CMSItemModel cachedBannerComponent = getCMSItemModelFromCacheByUid(LINK_UID);
		final ContentSlotForPageModel cachedRelation = getContentSlotForPageModelFromCacheByPosition(CONTENT_SLOT_POSITION);

		// THEN
		assertThat(cachedContentSlot, notNullValue());
		assertThat(cachedBannerComponent, notNullValue());
		assertThat(cachedRelation, notNullValue());
	}

	protected ContentSlotForPageModel getContentSlotForPageModelFromCacheByPosition(final String position)
	{
		return cmsVersionSessionContextProvider.getAllCachedContentSlotsForPage().stream()
				.filter((e) ->
						e.getPosition().equals(position)
				).findFirst().get();
	}

	protected CMSItemModel getCMSItemModelFromCacheByUid(final String uid)
	{
		return (CMSItemModel) cmsVersionSessionContextProvider.getAllGeneratedItemsFromCached().entrySet().stream()
				.filter((e) ->
						((CMSItemModel) e.getValue()).getUid().equals(uid)
				).findFirst().get().getValue();
	}
}


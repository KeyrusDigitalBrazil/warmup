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
package de.hybris.platform.cms2.version.service.impl;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cms2.model.CMSVersionModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminComponentService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminContentSlotService;
import de.hybris.platform.cms2.version.AbstractCMSVersionIntegrationTest;
import de.hybris.platform.cms2.version.service.CMSVersionSessionContextProvider;
import de.hybris.platform.core.model.ItemModel;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;

import org.hamcrest.Matchers;
import org.junit.Test;


@IntegrationTest
public class DefaultCMSVersionServiceIntegrationTest extends AbstractCMSVersionIntegrationTest
{
	private final String OLD_TITLE = "homePageTitle";
	private final String NEW_TITLE = "newHomePageTitle";
	private final String OLD_CMS_LINK_URL = "cmsLinkInSlotUrl";
	private final String NEW_CMS_LINK_URL = "newCmsLinkInSlotUrl";
	private final String CMS_LINK_UID = "LinkInSlot";


	@Resource
	private CMSAdminComponentService cmsAdminComponentService;

	@Resource
	private CMSAdminContentSlotService cmsAdminContentSlotService;

	@Resource
	private CMSVersionSessionContextProvider cmsVersionSessionContextProvider;

	@Test
	public void givenNewVersionWhenLocalAttributeChangedThenPreviewContainsOldLocalAttributeValue()
	{
		// GIVEN
		final CMSVersionModel versionModel = cmsVersionService.createRevisionForItem(contentPage);
		changeCurrentPageAttribute();

		// WHEN
		final ContentPageModel contentPagePreview = (ContentPageModel) cmsVersionService.createItemFromVersion(versionModel);

		// THEN
		assertThat(contentPagePreview.getTitle(), is(OLD_TITLE));
	}

	@Test
	public void givenNewVersionWhenLocalAttributeChangedThenRollbackContainsOldLocalAttributeValue()
	{
		// GIVEN
		final CMSVersionModel versionModel = cmsVersionService.createRevisionForItem(contentPage);
		changeCurrentPageAttribute();

		// WHEN
		final ContentPageModel contentPageRollback = (ContentPageModel) cmsVersionService.getItemFromVersion(versionModel);

		// THEN
		assertThat(contentPageRollback.getTitle(), is(OLD_TITLE));
	}

	@Test
	public void givenNewVersionWhenReferencedAttributeChangedThenRollbackContainsOldReferencedAttributeValue()
	{
		// GIVEN
		final CMSVersionModel versionModel = cmsVersionService.createRevisionForItem(contentPage);
		changeCurrentPageComponentAttribute();

		// WHEN
		final ContentPageModel contentPageRollback = (ContentPageModel) cmsVersionService.getItemFromVersion(versionModel);

		// THEN
		assertThat(getCMSLinkComponentFromCache(CMS_LINK_UID).getUrl(), is(OLD_CMS_LINK_URL));
	}

	@Test
	public void givenNewVersionWhenReferencedAttributeChangedThenPreviewContainsOldReferencedAttributeValue()
	{
		// GIVEN
		final CMSVersionModel versionModel = cmsVersionService.createRevisionForItem(contentPage);
		changeCurrentPageComponentAttribute();

		// WHEN
		final ContentPageModel contentPagePreview = (ContentPageModel) cmsVersionService.createItemFromVersion(versionModel);

		// THEN
		assertThat(getCMSLinkComponentFromCache(CMS_LINK_UID).getUrl(), is(OLD_CMS_LINK_URL));
	}

	@Test
	public void givenTransactionIdWherePageWasVersioned_WhenFindPageVersionedByTransactionIdIsCalled_ThenItReturnsAnEmptyOptional()
	{
		// GIVEN
		final CMSVersionModel versionModel = cmsVersionService.createRevisionForItem(contentPage);

		// WHEN
		final Optional<AbstractPageModel> pageModel = cmsVersionService.findPageVersionedByTransactionId(versionModel.getTransactionId());

		// THEN
		assertTrue(pageModel.isPresent());
		assertThat(pageModel.get(), is(contentPage));
	}

	@Test
	public void givenTransactionIdWhereNoPageWasVersioned_WhenFindPageVersionedByTransactionIdIsCalled_ThenItReturnsAnEmptyOptional()
	{
		// GIVEN
		final AbstractCMSComponentModel componentModel = cmsAdminComponentService
				.getCMSComponentForIdAndCatalogVersions("LinkInSlot", catalogVersionService.getAllCatalogVersions());
		final CMSVersionModel versionModel = cmsVersionService.createRevisionForItem(componentModel);

		// WHEN
		final Optional<AbstractPageModel> pageModel = cmsVersionService.findPageVersionedByTransactionId(versionModel.getTransactionId());

		// THEN
		assertFalse(pageModel.isPresent());
	}

	public void givenOldVersionWithoutComponentInSlotWhenComponentIsAddedToSlotThenPreviewOfOldVersionDoesNotContainComponent()
	{
		// GIVEN
		final CMSVersionModel oldVersion = cmsVersionService.createRevisionForItem(contentPage);
		addComponentToEmptyContentSlot();

		// WHEN
		final ItemModel pageFromOldVersion = cmsVersionService.getItemFromVersion(oldVersion);

		// THEN
		final ContentSlotModel slotFromCache = getSlotByUidFromCache(EMPTY_CONTENT_SLOT_UID);
		assertThat(slotFromCache.getCmsComponents(), Matchers.empty());
	}

	protected void changeCurrentPageAttribute()
	{
		contentPage.setTitle(NEW_TITLE);
		modelService.save(contentPage);
	}

	protected void changeCurrentPageComponentAttribute()
	{
		final CMSLinkComponentModel cmsLinkComponentModel = (CMSLinkComponentModel) cmsAdminComponentService.getCMSComponentForId(CMS_LINK_UID);
		cmsLinkComponentModel.setUrl(NEW_CMS_LINK_URL);
		modelService.save(cmsLinkComponentModel);
	}

	protected ContentSlotModel getSlotByUidFromCache(final String slotUid)
	{
		final List<ContentSlotForPageModel> allCachedContentSlotsForPage = cmsVersionSessionContextProvider.getAllCachedContentSlotsForPage();
		return allCachedContentSlotsForPage.stream() //
			.map(slot -> slot.getContentSlot())
			.filter(slot -> slot.getUid().equals(slotUid)) //
			.findFirst() //
			.get();
	}

	protected void addComponentToEmptyContentSlot()
	{
		final CMSLinkComponentModel componentForEmptySlot = (CMSLinkComponentModel) cmsAdminComponentService.getCMSComponentForId(COMPONENT_FOR_EMPTY_SLOT_UID);
		final ContentSlotModel emptyContentSlot = cmsAdminContentSlotService.getContentSlotForId(EMPTY_CONTENT_SLOT_UID);
		componentForEmptySlot.setSlots(Arrays.asList(emptyContentSlot));
		emptyContentSlot.setCmsComponents(Arrays.asList(componentForEmptySlot));
		modelService.saveAll(componentForEmptySlot, emptyContentSlot);
	}

	protected CMSLinkComponentModel getCMSLinkComponentFromCache(final String componentUID)
	{
		final Map<CMSVersionModel, ItemModel> context = cmsVersionSessionContextProvider.getAllGeneratedItemsFromCached();
		return (CMSLinkComponentModel) context.entrySet().stream().filter(entry -> {
			return entry.getKey().getItemUid().equals(componentUID);
		}).findFirst().get().getValue();
	}

}

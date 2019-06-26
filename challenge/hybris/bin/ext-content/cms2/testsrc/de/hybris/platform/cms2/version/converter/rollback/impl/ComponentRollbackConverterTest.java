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
package de.hybris.platform.cms2.version.converter.rollback.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.cloning.service.CMSItemCloningService;
import de.hybris.platform.cms2.exceptions.ItemRollbackException;
import de.hybris.platform.cms2.model.CMSVersionModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2.version.service.CMSVersionService;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.persistence.audit.payload.json.AuditPayload;
import de.hybris.platform.persistence.audit.payload.json.TypedValue;
import de.hybris.platform.persistence.audit.payload.json.ValueType;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ComponentRollbackConverterTest
{
	private final String TRANSACTION_ID = "some transaction id";
	private final String COMPONENT_ITEM_TYPE = "some component item type";
	private final String OTHER_ITEM_TYPE = "other item type";
	private final String VERSION_ITEM_TYPE = "version item type";

	private final String CLONE_COMPONENT_UID = "some clone component uid";
	private final String CLONE_COMPONENT_NAME = "some clone component name";
	private final String CURRENT_PAGE_SLOT_1_ID = "123";
	private final String CURRENT_PAGE_SLOT_2_ID = "321";
	private final String OTHER_PAGE_SLOT_1_ID = "456";
	private final String SHARED_SLOT_ID = "789";
	private final String EXTERNAL_SLOT_ID = "1112";

	@Mock
	private CMSVersionService cmsVersionService;

	@Mock
	private CMSComponentService cmsComponentService;

	@Mock
	private CMSItemCloningService cmsItemCloningService;

	@Mock
	private CMSPageService cmsPageService;

	@Mock
	private ModelService modelService;

	@Mock
	private TypeService typeService;

	@Mock
	private CMSVersionModel version;

	@Mock
	private AuditPayload auditPayload;

	@Mock
	private AbstractCMSComponentModel originalComponent;

	@Mock
	private AbstractCMSComponentModel clonedComponent;

	@Mock
	private AbstractPageModel page;

	@Mock
	private CMSVersionModel currentPageSlot1Version;

	@Mock
	private CMSVersionModel otherPageSlot1Version;

	@Mock
	private CMSVersionModel sharedSlotVersion;

	@Mock
	private ContentSlotModel externalSlot;

	@Mock
	private ContentSlotModel currentPageSlot1;

	@Mock
	private ContentSlotData currentPageSlot1Data;

	@Mock
	private ContentSlotData sharedSlot1Data;

	@Mock
	private ContentSlotModel currentPageSlot2;

	@Mock
	private ContentSlotData currentPageSlot2Data;

	@Mock
	private ContentSlotModel otherPageSlot1;

	@Mock
	private ContentSlotModel sharedSlot1;

	@InjectMocks
	private ComponentRollbackConverter componentRollbackConverter;

	@Before
	public void setUp()
	{
		// Version Payload
		setUpVersionPayload();

		setUpVersionedSlot(CURRENT_PAGE_SLOT_1_ID, currentPageSlot1Version);
		setUpVersionedSlot(OTHER_PAGE_SLOT_1_ID, otherPageSlot1Version);
		setUpVersionedSlot(SHARED_SLOT_ID, sharedSlotVersion);
		setUpVersionedSlot(EXTERNAL_SLOT_ID, externalSlot);

		// Page Slots
		when(cmsPageService.getContentSlotsForPage(page)).thenReturn(Arrays.asList(
				sharedSlot1Data, currentPageSlot1Data, currentPageSlot2Data));
		when(sharedSlot1Data.isFromMaster()).thenReturn(true);
		when(currentPageSlot1.getUid()).thenReturn(CURRENT_PAGE_SLOT_1_ID);
		when(currentPageSlot1Data.isFromMaster()).thenReturn(false);
		when(currentPageSlot1Data.getContentSlot()).thenReturn(currentPageSlot1);
		when(currentPageSlot2.getUid()).thenReturn(CURRENT_PAGE_SLOT_2_ID);
		when(currentPageSlot2Data.isFromMaster()).thenReturn(false);
		when(currentPageSlot2Data.getContentSlot()).thenReturn(currentPageSlot2);

		// External Slots
		when(otherPageSlot1.getUid()).thenReturn(OTHER_PAGE_SLOT_1_ID);
		when(sharedSlot1.getUid()).thenReturn(SHARED_SLOT_ID);
		when(originalComponent.getSlots()).thenReturn(Arrays.asList(otherPageSlot1, sharedSlot1, currentPageSlot2));

		// Components
		when(originalComponent.getItemtype()).thenReturn(COMPONENT_ITEM_TYPE);
		when(clonedComponent.getUid()).thenReturn(CLONE_COMPONENT_UID);
		when(clonedComponent.getName()).thenReturn(CLONE_COMPONENT_NAME);
		when(cmsItemCloningService.cloneComponent(originalComponent)).thenReturn(Optional.of(clonedComponent));
		when(cmsComponentService.isComponentUsedOutsidePage(originalComponent, page)).thenReturn(true); // By default, shared component.

		// Version
		when(version.getTransactionId()).thenReturn(TRANSACTION_ID);
		when(cmsVersionService.findPageVersionedByTransactionId(TRANSACTION_ID)).thenReturn(Optional.of(page));

		when(typeService.isAssignableFrom(CMSVersionModel._TYPECODE, VERSION_ITEM_TYPE)).thenReturn(true);
		when(typeService.isAssignableFrom(CMSVersionModel._TYPECODE, OTHER_ITEM_TYPE)).thenReturn(false);
	}

	protected void setUpVersionPayload()
	{
		Map<String, TypedValue> attributes = new HashMap<>();
		ValueType slotsValueType = new ValueType();

		attributes.put(AbstractCMSComponentModel.SLOTS,
				new TypedValue(slotsValueType, Arrays.asList(CURRENT_PAGE_SLOT_1_ID, OTHER_PAGE_SLOT_1_ID, SHARED_SLOT_ID)));
		when(auditPayload.getAttributes()).thenReturn(attributes);
	}

	protected void setUpVersionedSlot(final String slotUid, final ItemModel versionedSlot)
	{
		when(modelService.get(PK.parse(slotUid))).thenReturn(versionedSlot);

		String itemType = OTHER_ITEM_TYPE;
		if (versionedSlot instanceof CMSVersionModel)
		{
			CMSVersionModel slotVersion = (CMSVersionModel) versionedSlot;
			when(slotVersion.getItemUid()).thenReturn(slotUid);
			itemType = VERSION_ITEM_TYPE;
		}
		when(versionedSlot.getItemtype()).thenReturn(itemType);
	}

	@Test(expected = ItemRollbackException.class)
	public void givenComponentCannotBeVersioned_WhenRollbackItemIsCalled_ThenItThrowsAnItemRollbackException() throws ItemRollbackException
	{
		// GIVEN
		when(cmsItemCloningService.cloneComponent(originalComponent)).thenReturn(Optional.empty());

		// WHEN
		componentRollbackConverter.rollbackItem(originalComponent, version, auditPayload);
	}

	@Test
	public void givenComponentNotVersionedWithPage_WhenRollbackItemIsCalled_ThenItReturnsTheOriginalComponent() throws ItemRollbackException
	{
		// GIVEN
		when(cmsVersionService.findPageVersionedByTransactionId(TRANSACTION_ID)).thenReturn(Optional.empty());

		// WHEN
		ItemModel rollbackedItem = componentRollbackConverter.rollbackItem(originalComponent, version, auditPayload);

		// THEN
		assertThat(rollbackedItem, is(originalComponent));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void givenNonSharedComponent_WhenRollbackItemIsCalled_ThenItRollbacksTheComponentOnlyForTheCurrentPage() throws ItemRollbackException
	{
		// GIVEN
		when(cmsComponentService.isComponentUsedOutsidePage(originalComponent, page)).thenReturn(false);

		// WHEN
		AbstractCMSComponentModel rollbackedItem = componentRollbackConverter.rollbackItem(originalComponent, version, auditPayload);

		// THEN
		assertThat(rollbackedItem, is(originalComponent));
		verify(originalComponent).setSlots((Collection<ContentSlotModel>) argThat(containsInAnyOrder(currentPageSlot1)));
	}

	@Test
	public void givenNonSharedComponent_WhenRollbackItemIsCalled_ThenItUpdatesThePayloadToRemoveAnyExternalSlot() throws ItemRollbackException
	{
		// GIVEN
		when(cmsComponentService.isComponentUsedOutsidePage(originalComponent, page)).thenReturn(false);

		// WHEN
		componentRollbackConverter.rollbackItem(originalComponent, version, auditPayload);

		// THEN
		Map<String, TypedValue> attributes = auditPayload.getAttributes();
		assertThat(attributes.get(AbstractCMSComponentModel.SLOTS).getValue(), containsInAnyOrder(CURRENT_PAGE_SLOT_1_ID));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void givenSharedComponent_WhenRollbackItemIsCalled_ThenItReplacesTheComponentWithACloneInTheCurrentPage() throws ItemRollbackException
	{
		// WHEN
		AbstractCMSComponentModel rollbackedItem = componentRollbackConverter.rollbackItem(originalComponent, version, auditPayload);

		// THEN
		assertThat(rollbackedItem, is(clonedComponent));
		verify(originalComponent).setSlots((Collection<ContentSlotModel>) argThat(containsInAnyOrder(otherPageSlot1, sharedSlot1)));
		verify(rollbackedItem).setSlots((Collection<ContentSlotModel>) argThat(containsInAnyOrder(currentPageSlot1)));
	}

	@Test
	public void givenNonSharedComponent_WhenRollbackItemIsCalled_ThenItAddsTheCloneAndRemovesTheSharedSlotsFromThePayload() throws ItemRollbackException
	{
		// WHEN
		componentRollbackConverter.rollbackItem(originalComponent, version, auditPayload);

		// THEN
		Map<String, TypedValue> attributes = auditPayload.getAttributes();
		assertThat(attributes.get(AbstractCMSComponentModel.SLOTS).getValue(), containsInAnyOrder(CURRENT_PAGE_SLOT_1_ID));
		assertThat(attributes.get(AbstractCMSComponentModel.UID).getValue(), containsInAnyOrder(CLONE_COMPONENT_UID));
		assertThat(attributes.get(AbstractCMSComponentModel.NAME).getValue(), containsInAnyOrder(CLONE_COMPONENT_NAME));
	}
}

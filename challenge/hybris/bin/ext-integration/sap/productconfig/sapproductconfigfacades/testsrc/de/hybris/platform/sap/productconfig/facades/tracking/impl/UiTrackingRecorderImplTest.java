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
package de.hybris.platform.sap.productconfig.facades.tracking.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.CPQActionType;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.tracking.EventType;
import de.hybris.platform.sap.productconfig.services.tracking.RecorderParameters;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingItem;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingWriter;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class UiTrackingRecorderImplTest
{
	private static final String CART_ITEM_PK = "CART_ITEM_PK";
	private static final String CONFIG_ID = "ConfigId";
	private static final String SESSION_ID = "SessionId";
	private static final String PRODUCT_ID = "TheProduct";
	private static final String GROUP_ID = "1-TEST_GROUP._GEN";
	private static final String CSTIC_ID = "THECSTIC";
	private static final String CSTIC_PATH = "groups[0].subGroups[0].cstics[1]";


	private final UiTrackingRecorderImpl classUnderTest = Mockito.spy(new UiTrackingRecorderImpl());

	@Mock
	private SessionService sessionService;
	@Mock
	private Session session;
	@Mock
	private TrackingWriter writer;
	@Mock
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;

	private ConfigurationData configData;
	private ConfigurationOverviewData configOverviewData;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest.setSessionService(sessionService);
		classUnderTest.setAbstractOrderEntryLinkStrategy(configurationAbstractOrderEntryLinkStrategy);
		final List<TrackingWriter> writers = new ArrayList<>();
		writers.add(writer);
		classUnderTest.setWriters(writers);
		configData = new ConfigurationData();
		configData.setConfigId(CONFIG_ID);
		configData.setFocusId(CSTIC_ID);
		given(configurationAbstractOrderEntryLinkStrategy.getCartEntryForConfigId(CONFIG_ID)).willReturn(CART_ITEM_PK);
		configOverviewData = new ConfigurationOverviewData();
		configOverviewData.setId(CONFIG_ID);



		Mockito.when(sessionService.getCurrentSession()).thenReturn(session);
		Mockito.when(session.getSessionId()).thenReturn(SESSION_ID);
	}


	@Test
	public void testRecordPrevNextButtonClicked()
	{
		configData.setCpqAction(CPQActionType.NEXT_BTN);
		classUnderTest.recordPrevNextButtonClicked(configData, GROUP_ID);
		Mockito.verify(writer, times(1)).trackingItemCreated(Mockito.any());
	}

	@Test
	public void testRecordGroupInteraction_MenuNavigationEvent()
	{
		configData.setCpqAction(CPQActionType.MENU_NAVIGATION);
		classUnderTest.recordGroupInteraction(configData, GROUP_ID, false);
		Mockito.verify(writer, times(1)).trackingItemCreated(Mockito.any());
		Mockito.verify(classUnderTest, times(1)).recordGroupNavigationViaMenu(configData, GROUP_ID);
	}

	@Test
	public void testRecordGroup_ToggleGroupEvent()
	{
		configData.setCpqAction(CPQActionType.TOGGLE_GROUP);
		classUnderTest.recordGroupInteraction(configData, GROUP_ID, true);
		Mockito.verify(writer, times(1)).trackingItemCreated(Mockito.any());
		Mockito.verify(classUnderTest, times(1)).recordGroupToggle(configData, GROUP_ID, true);
	}


	@Test
	public void testRecordGroupInteraction_InvalidValidEvent()
	{
		configData.setCpqAction(CPQActionType.NEXT_BTN);
		classUnderTest.recordGroupInteraction(configData, GROUP_ID, false);
		Mockito.verify(writer, times(0)).trackingItemCreated(Mockito.any());
	}

	@Test
	public void testRecordGroupToggle_Expanded()
	{
		final TrackingItem item = classUnderTest.recordGroupToggle(configData, GROUP_ID, false);
		assertNotNull(item);
		assertNotNull(item.getTrackingItemKey());
		assertEquals(DigestUtils.sha256Hex(CONFIG_ID), item.getTrackingItemKey().getConfigId());
		assertEquals(EventType.UI_GROUP_TOGGLE, item.getTrackingItemKey().getEventType());
		assertEquals(GROUP_ID, item.getParameters().get(RecorderParameters.GROUP.toString()));
		assertFalse(Boolean.valueOf((item.getParameters().get(RecorderParameters.COLLAPSED.toString()))).booleanValue());
	}

	@Test
	public void testRecordGroupToggle_Collapsed()
	{
		final TrackingItem item = classUnderTest.recordGroupToggle(configData, GROUP_ID, true);
		assertNotNull(item);
		assertNotNull(item.getTrackingItemKey());
		assertEquals(DigestUtils.sha256Hex(CONFIG_ID), item.getTrackingItemKey().getConfigId());
		assertEquals(EventType.UI_GROUP_TOGGLE, item.getTrackingItemKey().getEventType());
		assertEquals(GROUP_ID, item.getParameters().get(RecorderParameters.GROUP.toString()));
		assertTrue(Boolean.valueOf((item.getParameters().get(RecorderParameters.COLLAPSED.toString()))).booleanValue());
	}

	@Test
	public void testRecordMenuToggle_Expanded()
	{
		classUnderTest.recordMenuToggle(configData, GROUP_ID, false);
		Mockito.verify(writer, times(1)).trackingItemCreated(Mockito.any());
	}

	@Test
	public void testRecordMenuToggle_Collapsed()
	{
		classUnderTest.recordMenuToggle(configData, GROUP_ID, true);
		Mockito.verify(writer, times(1)).trackingItemCreated(Mockito.any());
	}

	@Test
	public void testRecordGroupNavigationViaMenu()
	{
		final TrackingItem item = classUnderTest.recordGroupNavigationViaMenu(configData, GROUP_ID);
		assertNotNull(item);
		assertNotNull(item.getTrackingItemKey());
		assertEquals(DigestUtils.sha256Hex(CONFIG_ID), item.getTrackingItemKey().getConfigId());
		assertEquals(EventType.UI_MENU_NAVIGATION, item.getTrackingItemKey().getEventType());
		assertEquals(GROUP_ID, item.getParameters().get(RecorderParameters.GROUP.toString()));
	}

	@Test
	public void testRecordGroupNavigationViaMenu_noGroup()
	{
		final TrackingItem item = classUnderTest.recordGroupNavigationViaMenu(configData, null);
		assertNull(item);
	}


	@Test
	public void testRecordMenuToggle_noGroup()
	{
		classUnderTest.recordMenuToggle(configData, null, false);
		Mockito.verify(writer, times(0)).trackingItemCreated(Mockito.any());
	}

	@Test
	public void testRecordGroupToggle_noGroup()
	{
		final TrackingItem item = classUnderTest.recordGroupToggle(configData, null, false);
		assertNull(item);
	}

	@Test
	public void testRecordLongTextToggle_show()
	{
		configData.setCpqAction(CPQActionType.SHOW_FULL_LONG_TEXT);
		classUnderTest.recordLongTextToggle(configData);
		Mockito.verify(writer, times(1)).trackingItemCreated(Mockito.any());
		Mockito.verify(classUnderTest, times(1)).recordShowLongText(configData);
	}

	@Test
	public void testRecordLongTextToggle_hide()
	{
		configData.setCpqAction(CPQActionType.HIDE_FULL_LONG_TEXT);
		classUnderTest.recordLongTextToggle(configData);
		Mockito.verify(writer, times(1)).trackingItemCreated(Mockito.any());
		Mockito.verify(classUnderTest, times(1)).recordHideLongText(configData);
	}

	@Test
	public void testRecordExtendedMessageeToggleCstic()
	{
		configData.setCpqAction(CPQActionType.TOGGLE_EXTENDED_MESSAGE);
		classUnderTest.recordExtendedMessageToggle(configData, RecorderParameters.CSTIC);

		final ArgumentCaptor<TrackingItem> trackingItem = ArgumentCaptor.forClass(TrackingItem.class);
		Mockito.verify(writer, times(1)).trackingItemCreated(trackingItem.capture());
		Mockito.verify(classUnderTest, times(1)).recordItemExtendedMessageToggle(configData, RecorderParameters.CSTIC);

		assertTrue(trackingItem.getValue().getParameters().containsKey(RecorderParameters.CSTIC.toString()));
	}

	@Test
	public void testRecordExtendedMessageeToggleCsticValue()
	{
		configData.setCpqAction(CPQActionType.TOGGLE_EXTENDED_MESSAGE);
		classUnderTest.recordExtendedMessageToggle(configData, RecorderParameters.CSTIC_VALUE);

		final ArgumentCaptor<TrackingItem> trackingItem = ArgumentCaptor.forClass(TrackingItem.class);
		Mockito.verify(writer, times(1)).trackingItemCreated(trackingItem.capture());
		Mockito.verify(classUnderTest, times(1)).recordItemExtendedMessageToggle(configData, RecorderParameters.CSTIC_VALUE);

		assertTrue(trackingItem.getValue().getParameters().containsKey(RecorderParameters.CSTIC_VALUE.toString()));
	}

	@Test
	public void testRecordExtendedMessageeToggleWrongAction()
	{
		configData.setCpqAction(CPQActionType.MENU_NAVIGATION);
		classUnderTest.recordExtendedMessageToggle(configData, RecorderParameters.CSTIC_VALUE);

		Mockito.verify(writer, times(0)).trackingItemCreated(any());
		Mockito.verify(classUnderTest, times(0)).recordItemExtendedMessageToggle(configData, RecorderParameters.CSTIC_VALUE);
	}

	@Test
	public void testRecordHideLongText()
	{
		final TrackingItem item = classUnderTest.recordHideLongText(configData);
		assertNotNull(item);
		assertEquals(EventType.UI_HIDE_LONG_TEXT, item.getTrackingItemKey().getEventType());
	}

	@Test
	public void testRecordShowLongText()
	{
		final TrackingItem item = classUnderTest.recordShowLongText(configData);
		assertNotNull(item);
		assertEquals(EventType.UI_SHOW_LONG_TEXT, item.getTrackingItemKey().getEventType());
	}

	@Test
	public void testRecordNavigationToCsticInGroup()
	{
		final UiGroupData groupData = new UiGroupData();
		groupData.setId(GROUP_ID);
		classUnderTest.recordNavigationToCsticInGroup(configData, groupData);
		Mockito.verify(classUnderTest, times(1)).recordNavigationToCstic(configData, groupData, EventType.UI_NAV_TO_CSTIC_IN_GROUP);
	}

	@Test
	public void testRecordNavigationToCsticInGroup_null()
	{
		classUnderTest.recordNavigationToCsticInGroup(configData, null);
		Mockito.verify(classUnderTest, times(0)).recordNavigationToCstic(configData, new UiGroupData(),
				EventType.UI_NAV_TO_CSTIC_IN_GROUP);
	}

	@Test
	public void testRecordNavigationToCsticInConflict()
	{
		final UiGroupData groupData = new UiGroupData();
		groupData.setId(GROUP_ID);
		classUnderTest.recordNavigationToCsticInConflict(configData, groupData);
		Mockito.verify(classUnderTest, times(1)).recordNavigationToCstic(configData, groupData,
				EventType.UI_NAV_TO_CSTIC_IN_CONFLICT);
	}

	@Test
	public void testRecordNavigationToCsticInConflict_null()
	{
		classUnderTest.recordNavigationToCsticInConflict(configData, null);
		Mockito.verify(classUnderTest, times(0)).recordNavigationToCstic(configData, new UiGroupData(),
				EventType.UI_NAV_TO_CSTIC_IN_CONFLICT);
	}


	@Test
	public void testRecordNavigationToCstic_Group()
	{
		final UiGroupData groupData = new UiGroupData();
		groupData.setId(GROUP_ID);
		final TrackingItem item = classUnderTest.recordNavigationToCstic(configData, groupData, EventType.UI_NAV_TO_CSTIC_IN_GROUP);
		assertNotNull(item);
		assertEquals(EventType.UI_NAV_TO_CSTIC_IN_GROUP, item.getTrackingItemKey().getEventType());
		assertEquals(groupData.getId(), item.getParameters().get(RecorderParameters.GROUP.toString()));
		assertEquals(configData.getFocusId(), item.getParameters().get(RecorderParameters.CSTIC.toString()));
	}

	@Test
	public void testRecordNavigationToCstic_Conflict()
	{
		final UiGroupData groupData = new UiGroupData();
		groupData.setId(GROUP_ID);
		final TrackingItem item = classUnderTest.recordNavigationToCstic(configData, groupData,
				EventType.UI_NAV_TO_CSTIC_IN_CONFLICT);
		assertNotNull(item);
		assertEquals(EventType.UI_NAV_TO_CSTIC_IN_CONFLICT, item.getTrackingItemKey().getEventType());
		assertEquals(groupData.getId(), item.getParameters().get(RecorderParameters.GROUP.toString()));
		assertEquals(configData.getFocusId(), item.getParameters().get(RecorderParameters.CSTIC.toString()));
	}


	@Test
	public void testRecordImageGalleryToggle()
	{
		classUnderTest.recordImageGalleryToggle(configData, true);
		Mockito.verify(writer, times(1)).trackingItemCreated(Mockito.any());
	}

	@Test
	public void testRecordValueChanges_Change()
	{
		configData.setCpqAction(CPQActionType.VALUE_CHANGED);
		classUnderTest.recordValueChanges(configData, CSTIC_ID);
		Mockito.verify(writer, times(1)).trackingItemCreated(Mockito.any());
		Mockito.verify(classUnderTest, times(1)).recordValueChanged(configData, CSTIC_ID);
	}


	@Test
	public void testRecordValueChanges_Retract()
	{
		configData.setCpqAction(CPQActionType.RETRACT_VALUE);
		classUnderTest.recordValueChanges(configData, CSTIC_ID);
		Mockito.verify(writer, times(1)).trackingItemCreated(Mockito.any());
		Mockito.verify(classUnderTest, times(1)).recordValueRetracted(configData, CSTIC_ID);
	}


	@Test
	public void testRecordValueChanged()
	{
		configData.setFocusId(CSTIC_PATH);
		final TrackingItem item = classUnderTest.recordValueChanged(configData, CSTIC_ID);
		assertNotNull(item);
		assertEquals(EventType.UI_VALUE_CHANGED, item.getTrackingItemKey().getEventType());
		assertEquals(configData.getFocusId(), item.getParameters().get(RecorderParameters.CSTIC_PATH.toString()));
		assertEquals(CSTIC_ID, item.getParameters().get(RecorderParameters.CSTIC.toString()));
	}

	@Test
	public void testRecordValueRetracted()
	{
		configData.setFocusId(CSTIC_PATH);
		final TrackingItem item = classUnderTest.recordValueRetracted(configData, CSTIC_ID);
		assertNotNull(item);
		assertEquals(EventType.UI_VALUE_RETRACTED, item.getTrackingItemKey().getEventType());
		assertEquals(configData.getFocusId(), item.getParameters().get(RecorderParameters.CSTIC_PATH.toString()));
		assertEquals(CSTIC_ID, item.getParameters().get(RecorderParameters.CSTIC.toString()));
	}

	@Test
	public void testRecordUiAccessOverview()
	{
		classUnderTest.recordUiAccessOverview(configOverviewData, PRODUCT_ID);
		Mockito.verify(writer, times(1)).trackingItemCreated(Mockito.any());
	}

	@Test
	public void testRecordUiAccess()
	{
		classUnderTest.recordUiAccess(configData, PRODUCT_ID);
		Mockito.verify(writer, times(1)).trackingItemCreated(Mockito.any());
	}

	@Test
	public void testRecordUiAccessVariantOverview()
	{
		classUnderTest.recordUiAccessVariantOverview(PRODUCT_ID);
		Mockito.verify(writer, times(1)).trackingItemCreated(Mockito.any());
	}

	@Test
	public void testRecordUiAccessFromCart()
	{
		classUnderTest.recordUiAccessFromCart(configData, PRODUCT_ID);
		Mockito.verify(writer, times(1)).trackingItemCreated(Mockito.any());
	}


}

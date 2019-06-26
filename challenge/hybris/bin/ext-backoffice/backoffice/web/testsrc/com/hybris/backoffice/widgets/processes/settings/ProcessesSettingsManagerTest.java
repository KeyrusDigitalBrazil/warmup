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
package com.hybris.backoffice.widgets.processes.settings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

import com.google.common.collect.Sets;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@RunWith(MockitoJUnitRunner.class)
public class ProcessesSettingsManagerTest
{
	public static final String TEST_SETTING = "testSetting";
	private ProcessesSettingsManager settingsManager;
	private WidgetInstanceManager widgetInstanceManager;

	@Before
	public void setUp()
	{
		widgetInstanceManager = CockpitTestUtil.mockWidgetInstanceManager();
		doAnswer(inv -> inv.getArguments()[0]).when(widgetInstanceManager).getLabel(anyString());
		settingsManager = spy(new ProcessesSettingsManager(widgetInstanceManager));
		doReturn(new DefaultTimeRangeFactory()).when(settingsManager).getTimeRangeFactory();
		CockpitTestUtil.mockZkEnvironment();
	}

	@Test
	public void testBooleanSettingInitialValue()
	{
		final Label stateLabel = new Label();
		final Checkbox checkbox = new Checkbox();
		widgetInstanceManager.getWidgetSettings().put(TEST_SETTING, Boolean.TRUE);

		settingsManager.addBooleanSetting(TEST_SETTING, checkbox, stateLabel);

		assertThat(checkbox.isChecked()).isTrue();
		assertThat(stateLabel.getValue()).isEqualTo(ProcessesSettingsManager.LABEL_STATE_ENABLED);
	}

	@Test
	public void testBooleanSettingChange()
	{
		final Label stateLabel = new Label();
		final Checkbox checkbox = new Checkbox();
		settingsManager.addBooleanSetting(TEST_SETTING, checkbox, stateLabel);

		CockpitTestUtil.simulateEvent(checkbox, new CheckEvent(Events.ON_CHECK, checkbox, true));

		verify(settingsManager).notifySettingChanged(TEST_SETTING,Boolean.TRUE);
		assertThat(settingsManager.getSettingValue(TEST_SETTING, Boolean.class)).isTrue();
		assertThat(stateLabel.getValue()).isEqualTo(ProcessesSettingsManager.LABEL_STATE_ENABLED);
	}

	@Test
	public void testTimeRangeSettingInitialValue()
	{
		widgetInstanceManager.getWidgetSettings().put(TEST_SETTING, "1w,2h,10m", String.class);

		final Label statusLabel = new Label();
		final Listbox rangeList = new Listbox();

		settingsManager.addTimeRangeSetting(TEST_SETTING, rangeList, statusLabel);

		assertThat(settingsManager.getSettingValue(TEST_SETTING, TimeRange.class).getDuration().toMillis())
				.isEqualTo(TimeUnit.MINUTES.toMillis(10));
	}

	@Test
	public void testTimeRangeSettingInitialValueDoesNotContainModelValue()
	{
		widgetInstanceManager.getWidgetSettings().put(TEST_SETTING, "1w,2h,10m", String.class);

		final Label statusLabel = new Label();
		final Listbox rangeList = new Listbox();

		widgetInstanceManager.getModel()
				.setValue(settingsManager.getSettingPath(TEST_SETTING), settingsManager.getTimeRangeFactory().createTimeRange("2w"));
		settingsManager.addTimeRangeSetting(TEST_SETTING, rangeList, statusLabel);

		assertThat(settingsManager.getSettingValue(TEST_SETTING, TimeRange.class).getDuration().toMillis())
				.isEqualTo(TimeUnit.MINUTES.toMillis(10));
	}

	@Test
	public void testTimeRangeDefaultValue()
	{
		final Label statusLabel = new Label();
		final Listbox rangeList = new Listbox();

		settingsManager.addTimeRangeSetting(TEST_SETTING, rangeList, statusLabel);

		assertThat(settingsManager.getSettingValue(TEST_SETTING, TimeRange.class))
				.isEqualTo(settingsManager.getTimeRangeFactory().createTimeRange(ProcessesSettingsManager.DEFAULT_TIME_RANGE));
	}

	@Test
	public void testTimeRageSettingsChange()
	{
		widgetInstanceManager.getWidgetSettings().put(TEST_SETTING, "1w,2h,10m", String.class);

		final Label statusLabel = new Label();
		final Listbox rangeList = new Listbox();

		settingsManager.addTimeRangeSetting(TEST_SETTING, rangeList, statusLabel);

		final TimeRange twoHrsRange = settingsManager.getTimeRangeFactory().createTimeRange("2h");
		final SelectEvent<Listitem, TimeRange> selectEvent =
				new SelectEvent<>(Events.ON_SELECT, rangeList, null, null, null,
						Sets.newHashSet(twoHrsRange), null, null, null, null, 0);

		CockpitTestUtil.simulateEvent(rangeList, selectEvent);

		verify(settingsManager).notifySettingChanged(TEST_SETTING,twoHrsRange);
		assertThat(settingsManager.getSettingValue(TEST_SETTING, TimeRange.class).getDuration().toMillis())
				.isEqualTo(TimeUnit.HOURS.toMillis(2));
	}

}
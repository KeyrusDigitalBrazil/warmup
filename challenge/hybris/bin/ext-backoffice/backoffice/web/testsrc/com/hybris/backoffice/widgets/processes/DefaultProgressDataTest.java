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
package com.hybris.backoffice.widgets.processes;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.platform.cronjob.model.CronJobHistoryModel;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;


public class DefaultProgressDataTest
{
	private final Date initialDate = new Date(100000);
	private final Date fiveSecondsLater = new Date(initialDate.getTime() + 5000);
	private final Date tenSecondsLater = new Date(initialDate.getTime() + 10000);
	private final Date fifteenSecondsLater = new Date(initialDate.getTime() + 15000);
	private final Date twentySecondsLater = new Date(initialDate.getTime() + 20000);
	private final Date thirtySecondsLater = new Date(initialDate.getTime() + 30000);

	private DefaultProgressData progressData;

	@Before
	public void setUp()
	{
		progressData = new DefaultProgressData();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullTime()
	{
		//when
		progressData.updateProgress(getCronJobHistory(0), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullModel()
	{
		//when
		progressData.updateProgress(null, tenSecondsLater);
	}

	@Test
	public void testNotUpdatedProcessData()
	{
		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isZero();
		assertThat(progressData.getTimeToIncreaseOnePercent()).isZero();
	}

	@Test
	public void testProcessDataUpdatedWithNoProgress()
	{
		//when
		progressData.updateProgress(getCronJobHistory(0), tenSecondsLater);

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isZero();
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(DefaultProgressData.INITIAL_SPEED);
	}

	@Test
	public void testProcessDataUpdatedWith10Percent()
	{
		//when
		progressData.updateProgress(getCronJobHistory(10), tenSecondsLater);

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(1);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(526);
	}

	@Test
	public void testProcessDataUpdatedWithNull()
	{
		//when
		final CronJobHistoryModel cronJobHistory = getCronJobHistory(0);
		cronJobHistory.setProgress(null);
		progressData.updateProgress(cronJobHistory, tenSecondsLater);

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isZero();
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(DefaultProgressData.INITIAL_SPEED);
	}

	@Test
	public void testProcessDataUpdatedWith20Percent()
	{
		//when
		progressData.updateProgress(getCronJobHistory(20), tenSecondsLater);

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(1);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(256);
	}

	@Test
	public void testProcessDataUpdatedWith20PercentAfter20s()
	{
		//when
		progressData.updateProgress(getCronJobHistory(20), twentySecondsLater);

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(1);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(512);
	}

	@Test
	public void testProcessDataUpdatedWith10And20Percent()
	{
		//when
		progressData.updateProgress(getCronJobHistory(10), tenSecondsLater);
		progressData.updateProgress(getCronJobHistory(20), twentySecondsLater);

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(20);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(1000);
	}

	@Test
	public void testProcessDataUpdatedWith10And40Percent()
	{
		//when
		progressData.updateProgress(getCronJobHistory(10), tenSecondsLater);
		progressData.updateProgress(getCronJobHistory(40), twentySecondsLater);

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(21);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(204);
	}

	@Test
	public void testProcessDataUpdatedWith10And20PercentAfter15s()
	{
		//when
		progressData.updateProgress(getCronJobHistory(10), tenSecondsLater);
		progressData.updateProgress(getCronJobHistory(20), fifteenSecondsLater);

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(12);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(277);
	}

	@Test
	public void testSmallProgress()
	{
		//when
		progressData.updateProgress(getCronJobHistory(0), tenSecondsLater);
		progressData.updateProgress(getCronJobHistory(0), twentySecondsLater);
		progressData.updateProgress(getCronJobHistory(1), thirtySecondsLater);

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(10);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(0);
	}

	@Test
	public void testBigProgress()
	{
		//when
		progressData.updateProgress(getCronJobHistory(80), tenSecondsLater);
		progressData.updateProgress(getCronJobHistory(90), twentySecondsLater);

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(99);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(0);
	}

	@Test
	public void testSlowedDownProgress()
	{
		//when
		progressData.updateProgress(getCronJobHistory(30), tenSecondsLater);
		progressData.updateProgress(getCronJobHistory(31), twentySecondsLater);

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(60);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(0);
	}

	@Test
	public void testInitialProgress()
	{
		//when
		progressData.updateProgress(getCronJobHistory(0), tenSecondsLater);
		progressData.updateProgress(getCronJobHistory(0), thirtySecondsLater);

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(10);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(0);
	}

	@Test
	public void testInitialProgressForShortHeartBeat()
	{
		//when
		progressData.updateProgress(getCronJobHistory(0), fiveSecondsLater);
		progressData.updateProgress(getCronJobHistory(0), tenSecondsLater);

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(5);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(1000);
	}

	@Test
	public void testProgressWithRealSlowedData()
	{
		//when
		progressData.updateProgress(getCronJobHistory(0), getTime(17841));

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(0);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(DefaultProgressData.INITIAL_SPEED);

		//when
		progressData.updateProgress(getCronJobHistory(1), getTime(27839));

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(10);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(0);

		//when
		progressData.updateProgress(getCronJobHistory(1), getTime(37839));

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(10);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(0);

		//when
		progressData.updateProgress(getCronJobHistory(2), getTime(47968));

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(10);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(0);

		//when
		progressData.updateProgress(getCronJobHistory(9), getTime(57837));

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(10);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(1644);

		//when
		progressData.updateProgress(getCronJobHistory(15), getTime(67919));

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(16);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(2016);

		//when
		progressData.updateProgress(getCronJobHistory(20), getTime(77838));

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(21);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(2479);

		//when
		progressData.updateProgress(getCronJobHistory(26), getTime(87837));

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(26);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(1666);

		//when
		progressData.updateProgress(getCronJobHistory(29), getTime(97834));

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(32);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(0);

		//when
		progressData.updateProgress(getCronJobHistory(34), getTime(107836));

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(33);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(1667);

		//when
		progressData.updateProgress(getCronJobHistory(69), getTime(117839));

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(40);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(156);
	}

	@Test
	public void testProgressWithRealData()
	{
		//when
		progressData.updateProgress(getCronJobHistory(0), getTime(9127));

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(0);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(DefaultProgressData.INITIAL_SPEED);

		//when
		progressData.updateProgress(getCronJobHistory(0), getTime(9136));

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(0);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(1000);

		//when
		progressData.updateProgress(getCronJobHistory(4), getTime(18828));

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(10);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(0);

		//when
		progressData.updateProgress(getCronJobHistory(7), getTime(28755));

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(10);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(0);

		//when
		progressData.updateProgress(getCronJobHistory(8), getTime(38747));

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(10);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(0);

		//when
		progressData.updateProgress(getCronJobHistory(10), getTime(48968));

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(10);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(5110);

		//when
		progressData.updateProgress(getCronJobHistory(12), getTime(58761));

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(12);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(4896);

		//when
		progressData.updateProgress(getCronJobHistory(20), getTime(68740));

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(15);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(767);

		//when
		progressData.updateProgress(getCronJobHistory(67), getTime(78743));

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(29);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(117);

		//when
		progressData.updateProgress(getCronJobHistory(95), getTime(88999));

		//then
		assertThat(progressData.getEstimatedCurrentPercentage()).isEqualTo(99);
		assertThat(progressData.getTimeToIncreaseOnePercent()).isEqualTo(0);
	}

	private Date getTime(final long timeLapsed)
	{
		return new Date(initialDate.getTime() + timeLapsed);
	}

	private CronJobHistoryModel getCronJobHistory(final double progress)
	{
		final CronJobHistoryModel model = new CronJobHistoryModel();
		model.setProgress(Double.valueOf(progress));
		model.setStartTime(initialDate);
		return model;
	}
}

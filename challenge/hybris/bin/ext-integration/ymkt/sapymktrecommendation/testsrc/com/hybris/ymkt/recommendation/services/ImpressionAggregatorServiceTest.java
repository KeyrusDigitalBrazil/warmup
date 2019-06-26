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
package com.hybris.ymkt.recommendation.services;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;


@UnitTest
public class ImpressionAggregatorServiceTest
{
	public static class Impression
	{
		protected final int impressionCount;
		protected final int itemCount;
		protected final String scenarioId;
		protected final long time;

		public Impression(final String scenarioId, final int impressionCount, final int itemCount, final long time)
		{
			this.scenarioId = scenarioId;
			this.impressionCount = impressionCount;
			this.itemCount = itemCount;
			this.time = time;
		}
	}

	/**
	 * To be used to aggregate impressions from UI before persisting to DB.<br>
	 * Expected to be consumed in multi-threaded environment.
	 */
	public static class ImpressionAggregatorService
	{
		protected int window = 5 * 1000; // 5 sec
		protected int window_2 = window / 2;
		protected final Map<String, Map<Long, ImpressionCounter>> counters = new ConcurrentHashMap<>();

		public void addImpression(final String scenarioId, final int itemCount)
		{
			this.addImpression(scenarioId, 1, itemCount, System.currentTimeMillis());
		}

		public void addImpression(final String scenarioId, final int impressionCount, final int itemCount, final long time)
		{
			final ImpressionCounter counter = //
					counters.computeIfAbsent(scenarioId, s -> new ConcurrentHashMap<>()) //
							.computeIfAbsent(this.key(time), t -> new ImpressionCounter());
			counter.impressions.addAndGet(impressionCount);
			counter.items.addAndGet(itemCount);
		}

		protected long key(final long time)
		{
			// Middle value according to the aggregationWindow
			return time - (time % window) + window_2;
		}

		/**
		 * @return All aggregated {@link Impression} excluding those in the current ongoing aggregationWindow.
		 */
		public List<Impression> remove()
		{
			return this.removeInternal(System.currentTimeMillis());
		}

		protected List<Impression> removeInternal(final long time)
		{
			final long timeKey = this.key(time);

			final List<Impression> list = counters.entrySet().stream() //
					.<Impression> flatMap(c1 -> c1.getValue().entrySet().stream() //
							.<Impression> map(c2 -> new Impression(c1.getKey(), c2.getValue().impressions.get(),
									c2.getValue().items.get(), c2.getKey())))
					// time shall be before the current window. As the current aggregation window shall remain untouched.
					// Only previous completed windows will be removed.
					.filter(imp -> imp.time < timeKey) //
					.collect(Collectors.toList());

			list.forEach(imp -> counters.get(imp.scenarioId).remove(imp.time));

			return list;
		}
	}

	public static class ImpressionCounter
	{
		protected final AtomicInteger impressions = new AtomicInteger();
		protected final AtomicInteger items = new AtomicInteger();
	}

	ImpressionAggregatorService service = new ImpressionAggregatorService();


	@Test
	public void testEmpty()
	{
		final long time = System.currentTimeMillis();
		Assert.assertEquals(Collections.emptyList(), service.remove());
		service.addImpression("1", 1, 2, time);
		Assert.assertEquals(Collections.emptyList(), service.removeInternal(time));
	}

	@Test
	public void test1EntryRemoved()
	{
		final long time = System.currentTimeMillis();
		service.addImpression("1", 1, 2, time);
		service.addImpression("1", 1, 2, time);
		final List<Impression> aggregate = service.removeInternal(time + service.window);
		Assert.assertEquals(1, aggregate.size());
		Assert.assertEquals(service.key(time), aggregate.get(0).time);
		Assert.assertEquals(2, aggregate.get(0).impressionCount);
		Assert.assertEquals(4, aggregate.get(0).itemCount);
		Assert.assertEquals(Collections.emptyList(), service.removeInternal(time + service.window));
	}

	@Test
	public void test2EntryRemoved()
	{
		final long time = System.currentTimeMillis();
		service.addImpression("1", 1, 2, time);
		service.addImpression("1", 1, 2, time - service.window);
		final List<Impression> aggregate = service.removeInternal(time + service.window);
		Assert.assertEquals(2, aggregate.size());
		Assert.assertEquals(Collections.emptyList(), service.removeInternal(time + service.window));
	}

}

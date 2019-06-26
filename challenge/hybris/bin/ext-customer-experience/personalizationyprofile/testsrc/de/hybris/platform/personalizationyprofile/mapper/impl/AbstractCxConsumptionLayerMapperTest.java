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
package de.hybris.platform.personalizationyprofile.mapper.impl;

import de.hybris.platform.personalizationintegration.mapping.MappingData;
import de.hybris.platform.personalizationintegration.mapping.SegmentMappingData;
import de.hybris.platform.personalizationyprofile.yaas.Affinities;
import de.hybris.platform.personalizationyprofile.yaas.Affinity;
import de.hybris.platform.personalizationyprofile.yaas.Insights;
import de.hybris.platform.personalizationyprofile.yaas.Metrics;
import de.hybris.platform.personalizationyprofile.yaas.Profile;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Base class for mapper tests
 */
public abstract class AbstractCxConsumptionLayerMapperTest
{
	public MappingData target;

	@Mock
	ConfigurationService configurationService;

	@Mock
	Configuration configuration;

	public void init()
	{
		MockitoAnnotations.initMocks(this);
		target = new MappingData();
		target.setSegments(new ArrayList<>());

		Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
		Mockito.when(configuration.getBoolean(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(Boolean.TRUE);

	}

	protected Profile createProfile()
	{
		final Profile profile = new Profile();
		profile.setInsights(new Insights());
		profile.getInsights().setAffinities(new Affinities());
		profile.getInsights().setMetrics(new Metrics());
		return profile;
	}

	protected Affinity createAffinity(final BigDecimal score, final BigDecimal recentScore)
	{
		final Affinity affinity = new Affinity();
		affinity.setRecentScore(recentScore);
		affinity.setScore(score);
		return affinity;
	}

	protected void assertAffinityForSegment(final String segmentId, final String affinity, final MappingData data)
	{
		final Optional<SegmentMappingData> optionalSegment = data.getSegments().stream().filter(s -> segmentId.equals(s.getCode()))
				.findFirst();
		Assert.assertTrue("Missing segment with id " + segmentId, optionalSegment.isPresent());
		Assert.assertEquals("Invalid affinity", new BigDecimal(affinity), optionalSegment.get().getAffinity());
	}
}

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
package de.hybris.platform.cms2.servicelayer.services.evaluator.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.campaigns.model.CampaignModel;
import de.hybris.platform.campaigns.service.CampaignService;
import de.hybris.platform.cms2.model.restrictions.CMSCampaignRestrictionModel;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class CMSCampaignRestrictionEvaluatorTest
{
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	@Mock
	private CampaignService campaignService;
	@Mock
	RestrictionData restrictionData;
	@Mock
	private CampaignModel activeCamp1;
	@Mock
	private CampaignModel activeCamp2;
	@Mock
	private CampaignModel inactiveCamp;
	@InjectMocks
	private CMSCampaignRestrictionEvaluator evaluator;

	@Before
	public void setUp()
	{
		evaluator.setCampaignService(campaignService);
	}

	@Test
	public void testEvaluateWhenRestrictionIsNull()
	{
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("cmsCampaignRestriction");
		evaluator.evaluate(null, restrictionData);
	}

	@Test
	public void testEvaluateWhenRestrictionCampaignsAreActive()
	{
		when(campaignService.getActiveCampaigns()).thenReturn(Arrays.asList(activeCamp1, activeCamp2));

		final CMSCampaignRestrictionModel restriction = createCampaignRestriction(Arrays.asList(activeCamp1));

		final boolean isAllowed = evaluator.evaluate(restriction, restrictionData);
		assertTrue("Restriction is applied as it has only active campaigns", isAllowed);
	}

	@Test
	public void testEvaluateWhenRestrictionCampaignsAreActiveAndInactive()
	{
		when(campaignService.getActiveCampaigns()).thenReturn(Arrays.asList(activeCamp1, activeCamp2));

		final CMSCampaignRestrictionModel restriction = createCampaignRestriction(Arrays.asList(activeCamp1, inactiveCamp));

		final boolean isAllowed = evaluator.evaluate(restriction, restrictionData);
		assertTrue("Restriction is applied as it has active and inactive campaigns", isAllowed);
	}

	@Test
	public void testEvaluateWhenRestrictionCampaignsAreInactive()
	{
		when(campaignService.getActiveCampaigns()).thenReturn(Arrays.asList(activeCamp1, activeCamp2));

		final CMSCampaignRestrictionModel restriction = createCampaignRestriction(Arrays.asList(inactiveCamp));

		final boolean isAllowed = evaluator.evaluate(restriction, restrictionData);
		assertFalse("Restriction is not applied because the compaign in the restriction is not active", isAllowed);
	}

	@Test
	public void testEvaluateWhenRestrictionCampaignsAreEmptyOrNull()
	{
		when(campaignService.getActiveCampaigns()).thenReturn(Arrays.asList(activeCamp1));

		final CMSCampaignRestrictionModel restriction = createCampaignRestriction(null);

		boolean isAllowed = evaluator.evaluate(restriction, restrictionData);
		assertFalse("Restriction is not applied because the compaigns in the restriction are null", isAllowed);

		restriction.setCampaigns(Collections.emptyList());
		isAllowed = evaluator.evaluate(restriction, restrictionData);
		assertFalse("Restriction is not applied because the compaigns in the restriction are empty", isAllowed);
	}

	@Test
	public void testEvaluateWhenActiveCampaignsAreEmptyOrNull()
	{
		final CMSCampaignRestrictionModel restriction = createCampaignRestriction(Arrays.asList(activeCamp1));

		when(campaignService.getActiveCampaigns()).thenReturn(null);
		boolean isAllowed = evaluator.evaluate(restriction, restrictionData);
		assertFalse("Restriction is not applied because the active campaigns are null", isAllowed);

		when(campaignService.getActiveCampaigns()).thenReturn(Collections.emptyList());
		isAllowed = evaluator.evaluate(restriction, restrictionData);
		assertFalse("Restriction is not applied because the active compaigns are empty", isAllowed);
	}

	protected CMSCampaignRestrictionModel createCampaignRestriction(final Collection<CampaignModel> restrictedCampaigns)
	{
		final CMSCampaignRestrictionModel restriction = new CMSCampaignRestrictionModel();
		restriction.setCampaigns(restrictedCampaigns);
		return restriction;
	}

}

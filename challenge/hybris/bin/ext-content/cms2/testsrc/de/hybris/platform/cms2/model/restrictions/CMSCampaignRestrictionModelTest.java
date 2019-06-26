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
package de.hybris.platform.cms2.model.restrictions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.campaigns.model.CampaignModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@IntegrationTest
public class CMSCampaignRestrictionModelTest extends ServicelayerBaseTest // NOPMD
{
	@Resource
	private ModelService modelService;
	@Resource
	private TypeService typeService;
	@Mock
	private CampaignModel campaign1;
	@Mock
	private CampaignModel campaign2;


	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test method for {@link de.hybris.platform.cms2.model.restrictions.CMSCampaignRestrictionModel#getDescription()}.
	 */
	@Test
	public void shouldHaveDynamicAttributeDescriptor()
	{
		//given
		final ComposedTypeModel type = typeService.getComposedTypeForClass(CMSCampaignRestrictionModel.class);

		//when
		final AttributeDescriptorModel attributeDescriptor = typeService.getAttributeDescriptor(type,
				CMSCampaignRestrictionModel.DESCRIPTION);

		//then
		assertNotNull(attributeDescriptor);
		assertEquals("campaignRestrictionDynamicDescription", attributeDescriptor.getAttributeHandler());
	}

	/**
	 * Test method for {@link de.hybris.platform.cms2.model.restrictions.CMSCampaignRestrictionModel#getDescription()}.
	 *
	 */
	@Test
	public void shouldReturnRestrictionDescription()
	{
		//given
		final Collection<CampaignModel> campaigns = Arrays.asList(campaign1, campaign2);
		when(campaign1.getCode()).thenReturn("CAMP1");
		when(campaign2.getCode()).thenReturn("CAMP2");

		final CMSCampaignRestrictionModel restriction = modelService.create(CMSCampaignRestrictionModel.class);
		restriction.setCampaigns(campaigns);

		//when
		final String description = restriction.getDescription();

		//then
		assertNotNull(description);
		assertEquals("Display for campaigns: CAMP1, CAMP2", description);
	}
}

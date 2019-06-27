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
package de.hybris.platform.personalizationfacades.variation.impl;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.personalizationfacades.AbstractFacadeIntegrationTest;
import de.hybris.platform.personalizationfacades.data.VariationData;
import de.hybris.platform.personalizationfacades.enums.ItemStatus;
import de.hybris.platform.personalizationfacades.exceptions.AlreadyExistsException;
import de.hybris.platform.personalizationfacades.variation.VariationFacade;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;


@IntegrationTest
public class DefaultVariationFacadeIntegrationTest extends AbstractFacadeIntegrationTest
{
	private static final String NOTEXISTING_VARIATION_ID = "nonExistVariation";
	private static final String NEW_VARIATION_ID = "newVariation";
	private static final String NEW_VARIATION_NAME = "newVariationName";

	@Resource(name = "defaultCxVariationFacade")
	private VariationFacade variationFacade;

	//Tests for getVariations
	@Test
	public void getVariationsTest()
	{
		//when
		final List<VariationData> resultList = variationFacade.getVariations(CUSTOMIZATION_ID, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(resultList);
		Assert.assertEquals(4, resultList.size());
		VariationData result = resultList.get(0);
		Assert.assertEquals(VARIATION_ID, result.getCode());
		Assert.assertEquals(Integer.valueOf(0), result.getRank());
		result = resultList.get(1);
		Assert.assertEquals(VARIATION_ID_1, result.getCode());
		Assert.assertEquals(Integer.valueOf(1), result.getRank());
	}

	@Test(expected = UnknownIdentifierException.class)
	public void getVariationsForNotExistingCustomizationTest()
	{
		//when
		variationFacade.getVariations(NOTEXISTING_CUSTOMIZATION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	//Tests for create method
	@Test
	public void createVariationsTest()
	{
		//given
		final VariationData variationData = new VariationData();
		variationData.setCode(NEW_VARIATION_ID);
		variationData.setName(NEW_VARIATION_NAME);
		final Integer rank = Integer.valueOf(0);
		variationData.setRank(rank);

		//when
		final VariationData result = variationFacade.createVariation(CUSTOMIZATION_ID, variationData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(result);
		Assert.assertEquals(NEW_VARIATION_ID, result.getCode());
		Assert.assertEquals(rank, result.getRank());
	}

	@Test(expected = AlreadyExistsException.class)
	public void createAlreadyExistedVariationTest()
	{
		//given
		final VariationData variationData = new VariationData();
		variationData.setCode(VARIATION_ID);
		variationData.setName(NEW_VARIATION_NAME);

		//when
		variationFacade.createVariation(CUSTOMIZATION_ID, variationData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void createVariationsForNotExistingCustomizationTest()
	{
		//given
		final VariationData variationData = new VariationData();
		variationData.setCode(NEW_VARIATION_ID);
		variationData.setName(NEW_VARIATION_NAME);

		//when
		variationFacade.createVariation(NOTEXISTING_CUSTOMIZATION_ID, variationData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	//update method tests
	@Test
	public void updateVariationTest()
	{
		//given
		final VariationData variationData = new VariationData();
		variationData.setCode(VARIATION_ID);
		variationData.setName(VARIATION_NAME);
		final Integer rank = Integer.valueOf(1);
		variationData.setRank(rank);
		variationData.setStatus(ItemStatus.ENABLED);

		//when
		final VariationData result = variationFacade.updateVariation(CUSTOMIZATION_ID, VARIATION_ID, variationData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(result);
		Assert.assertEquals(VARIATION_ID, result.getCode());
		Assert.assertEquals(VARIATION_NAME, result.getName());
		Assert.assertEquals(rank, result.getRank());
		Assert.assertEquals(Boolean.TRUE, result.getEnabled());
		Assert.assertEquals(Boolean.TRUE, result.getActive());
	}

	@Test(expected = UnknownIdentifierException.class)
	public void updateNotExistingVariationTest()
	{
		//given
		final VariationData variationData = new VariationData();
		variationData.setCode(NOTEXISTING_VARIATION_ID);

		//when
		variationFacade.updateVariation(CUSTOMIZATION_ID, NOTEXISTING_VARIATION_ID, variationData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void updateVariationForNotExistingCustomizationTest()
	{
		//given
		final VariationData variationData = new VariationData();
		variationData.setCode(VARIATION_ID);

		//when
		variationFacade.updateVariation(NOTEXISTING_CUSTOMIZATION_ID, VARIATION_ID, variationData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);
	}

	//delete method tests
	@Test
	public void deleteVariationTest()
	{
		//given
		boolean variationRemoved = false;

		//when
		variationFacade.deleteVariation(CUSTOMIZATION_ID, VARIATION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);

		//then
		try
		{
			variationFacade.getVariation(CUSTOMIZATION_ID, VARIATION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
		}
		catch (final UnknownIdentifierException e)
		{
			variationRemoved = true;
		}
		assertTrue(variationRemoved);

	}

	@Test(expected = UnknownIdentifierException.class)
	public void deleteNotExistingVariationTest()
	{
		//when
		variationFacade.deleteVariation(CUSTOMIZATION_ID, NOTEXISTING_VARIATION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void deleteVariationForNotExistingCustomizationTest()
	{
		//when
		variationFacade.deleteVariation(NOTEXISTING_CUSTOMIZATION_ID, VARIATION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}
}

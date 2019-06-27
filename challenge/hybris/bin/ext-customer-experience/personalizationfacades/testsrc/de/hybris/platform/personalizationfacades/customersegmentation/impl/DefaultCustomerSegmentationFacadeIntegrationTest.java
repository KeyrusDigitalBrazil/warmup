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
package de.hybris.platform.personalizationfacades.customersegmentation.impl;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.personalizationfacades.AbstractFacadeIntegrationTest;
import de.hybris.platform.personalizationfacades.data.CustomerData;
import de.hybris.platform.personalizationfacades.data.CustomerSegmentationData;
import de.hybris.platform.personalizationfacades.data.SegmentData;
import de.hybris.platform.personalizationfacades.exceptions.AlreadyExistsException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCustomerSegmentationFacadeIntegrationTest extends AbstractFacadeIntegrationTest
{
	private static final String CUSTOMER_ID = "customer1@hybris.com";
	private static final String NOTEXISTING_CUSTOMER_ID = "nonExistCustomer";
	private static final String NOTRELATED_CUSTOMER_ID = "customer2@hybris.com";
	private static final String NOTRELATED_SEGMENT_ID = "segment1";
	private static final String BASESITE = "testSite";
	private static final String NOTEXISTING_BASESITE = "nonExistsBaseSite";
	private static final String NOTRELATED_BASESITE = "notRelatedBaseSite";
	private static final String INCORRECT_SEGMENTATION_ID = "incorrectId";
	private final String DEFAULT_PROVIDER = "DEFAULT";
	private String SEGMENTATION_ID;
	private String NOTEXISTING_SEGMENTATION_ID;
	private String CREATED_SEGMENTATION_ID;
	private String SEGMENTATION_ID_WITH_BASESITE;
	private String CREATED_SEGMENTATION_ID_WITH_BASESITE;

	@Resource(name = "defaultCxCustomerSegmentationFacade")
	private DefaultCustomerSegmentationFacade customerSegmentationFacade;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		SEGMENTATION_ID = customerSegmentationFacade.getSegmentationHelper().getSegmentationCode(SEGMENT_ID, CUSTOMER_ID, null,
				null);
		NOTEXISTING_SEGMENTATION_ID = customerSegmentationFacade.getSegmentationHelper().getSegmentationCode(NOTRELATED_SEGMENT_ID,
				NOTRELATED_CUSTOMER_ID, null, null);
		CREATED_SEGMENTATION_ID = customerSegmentationFacade.getSegmentationHelper().getSegmentationCode(NOTRELATED_SEGMENT_ID,
				NOTRELATED_CUSTOMER_ID, null, null);

		SEGMENTATION_ID_WITH_BASESITE = customerSegmentationFacade.getSegmentationHelper().getSegmentationCode(SEGMENT_ID_2,
				CUSTOMER_ID, BASESITE, null);
		CREATED_SEGMENTATION_ID_WITH_BASESITE = customerSegmentationFacade.getSegmentationHelper()
				.getSegmentationCode(NOTRELATED_SEGMENT_ID, NOTRELATED_CUSTOMER_ID, NOTRELATED_BASESITE, null);

	}

	//Tests for getCustomerSegmentation
	@Test
	public void getCustomerSegmentationTest()
	{
		//when
		final CustomerSegmentationData result = customerSegmentationFacade.getCustomerSegmentation(SEGMENTATION_ID);

		//then
		Assert.assertNotNull(result);
		Assert.assertEquals(SEGMENTATION_ID, result.getCode());
		Assert.assertEquals(CUSTOMER_ID, result.getCustomer().getUid());
		Assert.assertEquals(SEGMENT_ID, result.getSegment().getCode());
	}

	@Test
	public void getCustomerSegmentationWithBaseSiteTest()
	{
		//when
		final CustomerSegmentationData result = customerSegmentationFacade.getCustomerSegmentation(SEGMENTATION_ID_WITH_BASESITE);

		//then
		Assert.assertNotNull(result);
		Assert.assertEquals(SEGMENTATION_ID_WITH_BASESITE, result.getCode());
		Assert.assertEquals(CUSTOMER_ID, result.getCustomer().getUid());
		Assert.assertEquals(SEGMENT_ID_2, result.getSegment().getCode());
		Assert.assertEquals(BASESITE, result.getBaseSite());
	}

	@Test(expected = UnknownIdentifierException.class)
	public void getNotExistingCustomerSegmentationTest()
	{
		//when
		customerSegmentationFacade.getCustomerSegmentation(NOTEXISTING_SEGMENTATION_ID);
	}


	@Test(expected = UnknownIdentifierException.class)
	public void getCustomerSegmentationForNotExistingCustomerTest()
	{
		//when
		customerSegmentationFacade.getCustomerSegmentation(
				customerSegmentationFacade.getSegmentationHelper().getSegmentationCode(SEGMENT_ID, NOTEXISTING_CUSTOMER_ID, null,
						null));
	}

	@Test(expected = UnknownIdentifierException.class)
	public void getCustomerSegmentationForNotExistingWithBaseSiteCustomerTest()
	{
		//when
		customerSegmentationFacade.getCustomerSegmentation(customerSegmentationFacade.getSegmentationHelper()
				.getSegmentationCode(SEGMENT_ID, NOTEXISTING_CUSTOMER_ID, BASESITE, null, null));
	}

	@Test(expected = UnknownIdentifierException.class)
	public void getCustomerSegmentationForNotExistingSegmentTest()
	{
		//when
		customerSegmentationFacade.getCustomerSegmentation(
				customerSegmentationFacade.getSegmentationHelper().getSegmentationCode(NOTEXISTING_SEGMENT_ID, CUSTOMER_ID, null,
						null));
	}

	@Test(expected = UnknownIdentifierException.class)
	public void getCustomerSegmentationForNotExistingBaseSiteTest()
	{
		//when
		customerSegmentationFacade.getCustomerSegmentation(customerSegmentationFacade.getSegmentationHelper()
				.getSegmentationCode(SEGMENT_ID, CUSTOMER_ID, NOTEXISTING_BASESITE, null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void getCustomerSegmentationForIncorrectIdTest()
	{
		//when
		customerSegmentationFacade.getCustomerSegmentation(INCORRECT_SEGMENTATION_ID);
	}

	//Tests for create method

	@Test
	public void createCustomerSegmentationsTest()
	{
		//given
		final CustomerSegmentationData data = createCustomerSegmentationData();
		data.getCustomer().setUid(NOTRELATED_CUSTOMER_ID);
		data.getSegment().setCode(NOTRELATED_SEGMENT_ID);


		//when
		final CustomerSegmentationData result = customerSegmentationFacade.createCustomerSegmentation(data);

		//then
		Assert.assertNotNull(result);
		Assert.assertEquals(CREATED_SEGMENTATION_ID, result.getCode());
	}

	@Test
	public void createCustomerSegmentationsWithBaseSiteTest()
	{
		//given
		final CustomerSegmentationData data = createCustomerSegmentationData();
		data.getCustomer().setUid(NOTRELATED_CUSTOMER_ID);
		data.getSegment().setCode(NOTRELATED_SEGMENT_ID);
		data.setBaseSite(NOTRELATED_BASESITE);


		//when
		final CustomerSegmentationData result = customerSegmentationFacade.createCustomerSegmentation(data);

		//then
		Assert.assertNotNull(result);
		Assert.assertEquals(CREATED_SEGMENTATION_ID_WITH_BASESITE, result.getCode());
	}

	@Test
	public void createCustomerSegmentationsWithProviderTest()
	{
		//given
		final String id = customerSegmentationFacade.getSegmentationHelper().getSegmentationCode(SEGMENT_ID, CUSTOMER_ID, BASESITE,
				DEFAULT_PROVIDER);
		final CustomerSegmentationData data = createCustomerSegmentationData();
		data.getCustomer().setUid(CUSTOMER_ID);
		data.getSegment().setCode(SEGMENT_ID);
		data.setBaseSite(BASESITE);
		data.setProvider(DEFAULT_PROVIDER);

		//when
		final CustomerSegmentationData result = customerSegmentationFacade.createCustomerSegmentation(data);

		//then
		Assert.assertNotNull(result);
		Assert.assertEquals(id, result.getCode());
		Assert.assertEquals(DEFAULT_PROVIDER, result.getProvider());
	}

	private CustomerSegmentationData createCustomerSegmentationData()
	{
		final CustomerSegmentationData customerSegmentationData = new CustomerSegmentationData();
		final CustomerData customerData = new CustomerData();
		final SegmentData segmentData = new SegmentData();
		customerSegmentationData.setCustomer(customerData);
		customerSegmentationData.setSegment(segmentData);
		customerSegmentationData.setAffinity(BigDecimal.ONE);

		return customerSegmentationData;
	}

	@Test(expected = AlreadyExistsException.class)
	public void createAltreadyExistedCustomerSegmentationTest()
	{
		//given
		final CustomerSegmentationData data = createCustomerSegmentationData();
		data.getCustomer().setUid(CUSTOMER_ID);
		data.getSegment().setCode(SEGMENT_ID);

		//when
		customerSegmentationFacade.createCustomerSegmentation(data);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createCustomerSegmentationWithNullSegmentTest()
	{
		//given
		final CustomerSegmentationData data = createCustomerSegmentationData();
		data.getCustomer().setUid(CUSTOMER_ID);
		data.setSegment(null);

		//when
		customerSegmentationFacade.createCustomerSegmentation(data);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createCustomerSegmentationWithNullCustomerTest()
	{
		//given
		final CustomerSegmentationData data = createCustomerSegmentationData();
		data.setCustomer(null);
		data.getSegment().setCode(SEGMENT_ID);

		//when
		customerSegmentationFacade.createCustomerSegmentation(data);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void createCustomerSegmentationForNotExistingCustomerTest()
	{
		//given
		final CustomerSegmentationData data = createCustomerSegmentationData();
		data.getCustomer().setUid(NOTEXISTING_CUSTOMER_ID);
		data.getSegment().setCode(SEGMENT_ID);

		//when
		customerSegmentationFacade.createCustomerSegmentation(data);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void createCustomerSegmentationsForNotExistingSegmentTest()
	{
		//given
		final CustomerSegmentationData data = createCustomerSegmentationData();
		data.getCustomer().setUid(CUSTOMER_ID);
		data.getSegment().setCode(NOTEXISTING_SEGMENT_ID);

		//when
		customerSegmentationFacade.createCustomerSegmentation(data);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void createCustomerSegmentationsForNotExistingBaseSiteTest()
	{
		//given
		final CustomerSegmentationData data = createCustomerSegmentationData();
		data.getCustomer().setUid(CUSTOMER_ID);
		data.getSegment().setCode(SEGMENT_ID);
		data.setBaseSite(NOTEXISTING_BASESITE);

		//when
		customerSegmentationFacade.createCustomerSegmentation(data);
	}

	//delete method tests

	@Test
	public void deleteCustomerSegmentationTest()
	{
		//given
		boolean customerSegmentationRemoved = false;

		//when
		customerSegmentationFacade.deleteCustomerSegmentation(SEGMENTATION_ID);

		//then
		try
		{
			customerSegmentationFacade.getCustomerSegmentation(SEGMENTATION_ID);
		}
		catch (final UnknownIdentifierException e)
		{
			customerSegmentationRemoved = true;
		}
		assertTrue(customerSegmentationRemoved);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void deleteNotExistingCustomerSegmentationTest()
	{
		//when
		customerSegmentationFacade.deleteCustomerSegmentation(NOTEXISTING_SEGMENTATION_ID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void deleteCustomerSegmentationForNotExistingCustomerTest()
	{
		//when
		customerSegmentationFacade.deleteCustomerSegmentation(
				customerSegmentationFacade.getSegmentationHelper().getSegmentationCode(SEGMENT_ID, NOTEXISTING_CUSTOMER_ID));
	}

	@Test(expected = UnknownIdentifierException.class)
	public void deleteCustomerSegmentationForNotExistingCustomerWithBaseSiteTest()
	{
		//when
		customerSegmentationFacade.deleteCustomerSegmentation(customerSegmentationFacade.getSegmentationHelper()
				.getSegmentationCode(SEGMENT_ID, NOTEXISTING_CUSTOMER_ID, BASESITE));
	}

	@Test(expected = UnknownIdentifierException.class)
	public void deleteCustomerSegmentationForNotExistingSegmentTest()
	{
		//when
		customerSegmentationFacade.deleteCustomerSegmentation(
				customerSegmentationFacade.getSegmentationHelper().getSegmentationCode(NOTEXISTING_SEGMENT_ID, CUSTOMER_ID, null));
	}

	@Test(expected = UnknownIdentifierException.class)
	public void deleteCustomerSegmentationForNotExistingBaseSiteTest()
	{
		//when
		customerSegmentationFacade.deleteCustomerSegmentation(customerSegmentationFacade.getSegmentationHelper()
				.getSegmentationCode(SEGMENT_ID, CUSTOMER_ID, NOTEXISTING_BASESITE));
	}

	@Test(expected = IllegalArgumentException.class)
	public void deleteCustomerSegmentationWithIncorrectIdTest()
	{
		//when
		customerSegmentationFacade.deleteCustomerSegmentation(INCORRECT_SEGMENTATION_ID);
	}
}

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

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.hybris.ymkt.common.user.UserContextService;
import com.hybris.ymkt.recommendation.dao.OfferInteractionContext;
import com.hybris.ymkt.recommendation.dao.OfferInteractionContext.Interaction;
import com.hybris.ymkt.recommendation.dao.OfferInteractionContext.Offer;
import com.hybris.ymkt.recommendationbuffer.model.SAPOfferInteractionModel;


@IntegrationTest
public class OfferDiscoveryIntegrationTest extends ServicelayerTransactionalTest
{

	@Resource
	private ModelService modelService;

	@Resource
	private OfferInteractionService offerInteractionService;

	@Resource
	private OfferDiscoveryService offerDiscoveryService;

	@Mock
	private UserContextService userContextService;

	private static final int BATCH_SIZE = 2;

	private static final String CONTACTID_01 = "2612d9ed1631856b";
	private static final String CONTACTORIGINID_01 = "COOKIE_ID";
	private static final String INTERACTIONTYPE = "OFFER_DISPLAY";
	private static final String OFFERCONTENTITEMID = "00001";
	private static final String RECOMMENDATIONSCENARIOID = "PHX_OFFER_SCENARIO_2";
	private static final String OFFERID = "0000000288";
	private static final Date DATE = new Date();


	List<SAPOfferInteractionModel> offerInteractionModel;
	List<OfferInteractionContext> offerInteractionContext;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		offerInteractionService.setReadBatchSize(500);
		offerInteractionService.setUserContextService(userContextService);
		given(userContextService.getUserId()).willReturn("6de4ae57e795a737");
		given(userContextService.getUserOrigin()).willReturn("COOKIE_ID");


		final SAPOfferInteractionModel MODEL01 = new SAPOfferInteractionModel();
		MODEL01.setContactId(CONTACTID_01);
		MODEL01.setContactIdOrigin(CONTACTORIGINID_01);
		MODEL01.setInteractionType(INTERACTIONTYPE);
		MODEL01.setOfferContentItemId(OFFERCONTENTITEMID);
		MODEL01.setOfferId(OFFERID);
		MODEL01.setOfferRecommendationScenarioId(RECOMMENDATIONSCENARIOID);

		offerInteractionModel = Arrays.asList(MODEL01);

		final OfferInteractionContext CONTEXT01 = new OfferInteractionContext();
		final Interaction INTERACTION01 = new Interaction();
		final Offer OFFER01 = new Offer();

		OFFER01.setId(OFFERID);
		OFFER01.setContentItemId(OFFERCONTENTITEMID);
		OFFER01.setRecommendationScenarioId(RECOMMENDATIONSCENARIOID);

		INTERACTION01.setContactId(CONTACTID_01);
		INTERACTION01.setContactIdOrigin(CONTACTORIGINID_01);
		INTERACTION01.setInteractionType(INTERACTIONTYPE);

		CONTEXT01.setTimestamp(DATE);
		CONTEXT01.getInteractions().add(INTERACTION01);
		CONTEXT01.getInteractions().get(0).getOffers().add(OFFER01);

		offerInteractionContext = Arrays.asList(CONTEXT01);

	}

	@Test
	public void saveOfferInteractionTest()
	{

		System.out.println("START saveOfferInteractionTest");

		final OfferInteractionContext context = offerInteractionContext.get(0);
		final Interaction interaction = context.getInteractions().get(0);
		final Offer offer = interaction.getOffers().get(0);


		offerInteractionService.saveOfferInteraction(context);


		List<SAPOfferInteractionModel> offerInteractionResults = offerInteractionService.recommendationBufferService
				.getOfferInteractions(BATCH_SIZE);
		final SAPOfferInteractionModel model = offerInteractionResults.get(0);

		assertEquals(1, offerInteractionResults.size());
		assertEquals(interaction.getContactId(), model.getContactId());
		assertEquals(interaction.getInteractionType(), model.getInteractionType());
		assertEquals(interaction.getContactIdOrigin(), model.getContactIdOrigin());

		assertEquals(offer.getContentItemId(), model.getOfferContentItemId());
		assertEquals(offer.getId(), model.getOfferId());
		assertEquals(offer.getRecommendationScenarioId(), model.getOfferRecommendationScenarioId());

		modelService.remove(model);

		System.out.println("END saveOfferInteractionTest");

	}

	@Test
	public void testBatchReading()
	{

		final OfferInteractionContext context = offerInteractionContext.get(0);

		// saves 2 entries in DB
		offerInteractionService.saveOfferInteraction(context);
		offerInteractionService.saveOfferInteraction(context);

		for (int BATCH_SIZE_TEST = 0; BATCH_SIZE_TEST < 4; BATCH_SIZE_TEST++)
		{
			switch (BATCH_SIZE_TEST)
			{
				case 0:
					assertEquals(0, offerInteractionService.recommendationBufferService.getOfferInteractions(BATCH_SIZE_TEST).size());
					break;
				case 1:
					assertEquals(1, offerInteractionService.recommendationBufferService.getOfferInteractions(BATCH_SIZE_TEST).size());
					break;
				case 2:
					assertEquals(2, offerInteractionService.recommendationBufferService.getOfferInteractions(BATCH_SIZE_TEST).size());
					break;
				case 3:
					assertEquals(2, offerInteractionService.recommendationBufferService.getOfferInteractions(BATCH_SIZE_TEST).size());
					break;
				default:
					break;
			}

		}

	}

	//
	//
	//	@Test
	//	public void getOfferRecommendationScenariosTest() throws IOException
	//	{
	//
	//		System.out.println("START getOfferRecommendationScenariosTest");
	//
	//		List<SAPRecommendationType> recoScenarios = offerDiscoveryService.getOfferRecommendationScenarios();
	//
	//		assertNotSame(0, recoScenarios.size());
	//		assertNotNull(recoScenarios);
	//
	//		System.out.println("END getOfferRecommendationScenariosTest");
	//	}
	//
	//	@Test
	//	public void getItemDataSourceTypesTest() throws IOException
	//	{
	//
	//		System.out.println("START getItemDataSourceTypes");
	//
	//		List<SAPRecommendationItemDataSourceType> itemDS = offerDiscoveryService.getItemDataSourceTypes();
	//
	//		assertNotSame(0, itemDS.size());
	//		assertNotNull(itemDS);
	//
	//		System.out.println("END getItemDataSourceTypes");
	//	}
	//
	//	@Test
	//	public void getContentPositionValuesTest() throws IOException
	//	{
	//
	//		System.out.println("START getContentPositionValues");
	//
	//		List<SAPOfferContentPositionType> contentPositions = offerDiscoveryService.getContentPositionValues();
	//
	//		assertNotSame(0, contentPositions.size());
	//		assertNotNull(contentPositions);
	//
	//		System.out.println("END getContentPositionValues");
	//	}





}

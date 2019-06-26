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
package de.hybris.platform.sap.productconfig.rules.cps.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.sap.productconfig.rules.cps.handler.impl.CharacteristicValueRulesResultHandlerImpl;
import de.hybris.platform.sap.productconfig.rules.cps.model.CharacteristicValueRulesResultModel;
import de.hybris.platform.sap.productconfig.rules.cps.model.DiscountMessageRulesResultModel;
import de.hybris.platform.sap.productconfig.rules.integrationtests.ProductConfigRulesTest;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.junit.Test;


@IntegrationTest
@SuppressWarnings("javadoc")
public class CharacteristicValueRulesResultHandlerIntegrationTest extends ProductConfigRulesTest
{
	private final Logger LOG = Logger.getLogger(CharacteristicValueRulesResultHandlerIntegrationTest.class.getName());

	private static final String CONFIG_ID = "configId";
	private static final String CONFIG_ID2 = "configId2";
	private static final String CSTIC_ID1 = "csticId1";
	private static final String VALUE_ID1 = "valueId1";
	private static final BigDecimal DISCOUNT1 = new BigDecimal(10);
	private static final String CSTIC_ID2 = "csticId2";
	private static final String VALUE_ID2 = "valueId2";
	private static final BigDecimal DISCOUNT2 = new BigDecimal(20);
	private static final String CSTIC_ID3 = "csticId3";
	private static final String VALUE_ID3 = "valueId3";
	private static final BigDecimal DISCOUNT3 = new BigDecimal(30);
	private static final String VERSION = "version";

	@Resource(name = "sapProductConfigRulesCharacteristicValueRulesResultHandler")
	private CharacteristicValueRulesResultHandlerImpl rulesResultHandler;

	@Resource(name = "sapProductConfigProductConfigurationPersistenceService")
	protected ProductConfigurationPersistenceService cpqPersistenceService;

	@Override
	protected void importCPQTestData() throws ImpExException, Exception
	{
		super.importCPQTestData();
	}

	@Test
	public void testRulesResultLifecycle()
	{
		persistNewConfiguration(CONFIG_ID);
		persistNewConfiguration(CONFIG_ID2);
		rulesResultHandler.mergeDiscountAndPersistResults(createResult(CSTIC_ID1, VALUE_ID1, DISCOUNT1), CONFIG_ID);
		rulesResultHandler.addMessageToRulesResult(createMessage("test msg", new Date()), CONFIG_ID, CSTIC_ID1, VALUE_ID1);
		rulesResultHandler.addMessageToRulesResult(createMessage("test msg 123", new Date()), CONFIG_ID, CSTIC_ID1, VALUE_ID1);
		rulesResultHandler.mergeDiscountAndPersistResults(createResult(CSTIC_ID2, VALUE_ID2, DISCOUNT2), CONFIG_ID);
		rulesResultHandler.mergeDiscountAndPersistResults(createResult(CSTIC_ID3, VALUE_ID3, DISCOUNT3), CONFIG_ID2);
		rulesResultHandler.addMessageToRulesResult(createMessage("test msg 456", new Date()), CONFIG_ID2, CSTIC_ID3, VALUE_ID3);
		checkNumberOfResultsPersisted(3);
		checkNumberOfMessagesPersisted(3);

		final List<CharacteristicValueRulesResultModel> rulesResultsByConfigId = rulesResultHandler
				.getRulesResultsByConfigId(CONFIG_ID);
		assertNotNull(rulesResultsByConfigId);
		assertEquals(2, rulesResultsByConfigId.size());
		for (final CharacteristicValueRulesResultModel rulesResult : rulesResultsByConfigId)
		{
			if (rulesResult.getCharacteristic().equals(CSTIC_ID1))
			{
				assertEquals(VALUE_ID1, rulesResult.getValue());
				assertEquals(0, DISCOUNT1.compareTo(rulesResult.getDiscountValue()));
				assertEquals(2, rulesResult.getMessageRulesResults().size());
			}
			else if (rulesResult.getCharacteristic().equals(CSTIC_ID2))
			{
				assertEquals(VALUE_ID2, rulesResult.getValue());
				assertEquals(0, DISCOUNT2.compareTo(rulesResult.getDiscountValue()));
				assertTrue(CollectionUtils.isEmpty(rulesResult.getMessageRulesResults()));
			}
		}

		final List<CharacteristicValueRulesResultModel> rulesResultsByConfigId2 = rulesResultHandler
				.getRulesResultsByConfigId(CONFIG_ID2);
		assertNotNull(rulesResultsByConfigId2);
		assertEquals(1, rulesResultsByConfigId2.size());

		rulesResultHandler.deleteRulesResultsByConfigId(CONFIG_ID);
		checkNumberOfResultsPersisted(1);
		checkNumberOfMessagesPersisted(1);
	}



	protected DiscountMessageRulesResultModel createMessage(final String messageText, final Date endDate)
	{
		final DiscountMessageRulesResultModel message = rulesResultHandler.createMessageInstance();
		message.setMessage(messageText);
		message.setEndDate(endDate);
		return message;
	}

	@Test
	public void testRulesResultLifecycleDeleteAndPersistAgain()
	{
		persistNewConfiguration(CONFIG_ID);
		rulesResultHandler.mergeDiscountAndPersistResults(createResult(CSTIC_ID1, VALUE_ID1, DISCOUNT1), CONFIG_ID);
		rulesResultHandler.addMessageToRulesResult(createMessage("test msg", new Date()), CONFIG_ID, CSTIC_ID1, VALUE_ID1);
		rulesResultHandler.mergeDiscountAndPersistResults(createResult(CSTIC_ID2, VALUE_ID2, DISCOUNT2), CONFIG_ID);

		checkNumberOfResultsPersisted(2);
		checkNumberOfMessagesPersisted(1);

		final List<CharacteristicValueRulesResultModel> rulesResultsByConfigId = rulesResultHandler
				.getRulesResultsByConfigId(CONFIG_ID);
		assertNotNull(rulesResultsByConfigId);
		assertEquals(2, rulesResultsByConfigId.size());

		rulesResultHandler.deleteRulesResultsByConfigId(CONFIG_ID);
		checkNumberOfResultsPersisted(0);
		checkNumberOfMessagesPersisted(0);
		rulesResultHandler.mergeDiscountAndPersistResults(createResult(CSTIC_ID2, VALUE_ID2, DISCOUNT2), CONFIG_ID);
		rulesResultHandler.addMessageToRulesResult(createMessage("test msg", new Date()), CONFIG_ID, CSTIC_ID2, VALUE_ID2);
		checkNumberOfResultsPersisted(1);
		checkNumberOfMessagesPersisted(1);
	}


	protected CharacteristicValueRulesResultModel createResult(final String csticId, final String valueId,
			final BigDecimal discount)
	{
		final CharacteristicValueRulesResultModel result = rulesResultHandler.createInstance();
		result.setCharacteristic(csticId);
		result.setValue(valueId);
		result.setDiscountValue(discount);
		return result;
	}

	@Test
	public void testRulesResultDeletionByConfigModelDelete()
	{
		persistNewConfiguration(CONFIG_ID);
		rulesResultHandler.mergeDiscountAndPersistResults(createResult(CSTIC_ID1, VALUE_ID1, DISCOUNT1), CONFIG_ID);
		rulesResultHandler.mergeDiscountAndPersistResults(createResult(CSTIC_ID2, VALUE_ID2, DISCOUNT2), CONFIG_ID);
		checkNumberOfResultsPersisted(2);

		deleteProductConfiguration(CONFIG_ID);
		// After deletion of ProductConfigurationModel also associated rules results should be deleted
		checkNumberOfResultsPersisted(0);
	}

	protected void deleteProductConfiguration(final String configId)
	{
		final ProductConfigurationModel configModel = cpqPersistenceService.getByConfigId(configId);
		modelService.remove(configModel);
	}


	protected void persistNewConfiguration(final String configId)
	{
		final UserModel currentUser = realUserService.getAnonymousUser();
		final ProductConfigurationModel configModel = modelService.create(ProductConfigurationModel.class);
		configModel.setConfigurationId(configId);
		configModel.setVersion(VERSION);
		configModel.setProduct(Collections.emptyList());
		configModel.setUser(currentUser);
		modelService.save(configModel);
	}


	protected void checkNumberOfResultsPersisted(final int numExpected)
	{
		final SearchResult<Object> result = flexibleSearchService.search("select * from {CharacteristicValueRulesResult}");
		final int numActual = result.getTotalCount();
		assertEquals("Expected " + numExpected + " rules result persisted in the current user session, but saw " + numActual,
				numExpected, numActual);
	}

	protected void checkNumberOfMessagesPersisted(final int numExpected)
	{
		final SearchResult<Object> result = flexibleSearchService.search("select * from {DiscountMessageRulesResult}");
		final int numActual = result.getTotalCount();
		assertEquals(
				"Expected " + numExpected + " rules result messages persisted in the current user session, but saw " + numActual,
				numExpected, numActual);

	}
}

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
package de.hybris.platform.sap.productconfig.facades.integrationtests;

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.services.impl.ProductConfigurationPersistenceServiceImpl;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.session.impl.DefaultSession;
import de.hybris.platform.servicelayer.session.impl.DefaultSessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.testframework.Transactional;
import de.hybris.platform.tx.Transaction;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Attention: This test will create random PERSISTENT testdata for table productconfiguration with each run.
 */
@ManualTest
@Transactional(noRollback = true)
public class PersistenceStrategiesPerfIntegrationTest extends CPQFacadeLayerTest
{


	private static final int MAX_ENTRIES = 100000;
	private static final int ENTRIES_TO_TEST = 1000;

	@Resource(name = "sapProductConfigDefaultProductConfigurationPersistenceService")
	private ProductConfigurationPersistenceServiceImpl persistenceService;

	@Resource(name = "userService")
	private UserService userService;
	@Resource(name = "productService")
	private ProductService productService;
	@Resource(name = "sessionService")
	private SessionService sessionService;


	private static final Set<String> configIdsToTest = new HashSet<>((int) (ENTRIES_TO_TEST / 0.75));
	private static final Set<ProductAndSession> productAndSessionIdsToTest = new HashSet<>((int) (ENTRIES_TO_TEST / 0.75));




	private final Logger LOG = Logger.getLogger(PersistenceStrategiesPerfIntegrationTest.class);

	@Before
	public void setUp() throws Exception
	{
		prepareCPQData();
	}

	@Override
	public void importCPQTestData() throws ImpExException, Exception
	{
		super.importCPQTestData();
		importCPQUserData();
	}


	private void prepareTestData(final int maxEntries, final int entriesToTest)
	{

		final long startTime = System.currentTimeMillis();
		final int existingCount = getTotalCount();


		final ProductModel ySimplePoc = productService.getProductForCode(PRODUCT_CODE_YSAP_SIMPLE_POC);
		final ProductModel cpqLaptop = productService.getProductForCode(PRODUCT_CODE_CPQ_LAPTOP);
		final CustomerModel anonymousUser = userService.getAnonymousUser();

		LOG.info("ProductConfiguration Table has " + getTotalCount() + " entries BEFORE testdata creation");

		long packageStartTime = startTime;
		for (int ii = 1; ii <= maxEntries; ii++)
		{
			final String configId = UUID.randomUUID().toString();
			final String sessionId = UUID.randomUUID().toString();
			final ProductConfigurationModel configModel = modelService.create(ProductConfigurationModel.class);
			configModel.setConfigurationId(configId);
			configModel.setVersion("perfTest");
			configModel.setUser(anonymousUser);
			ProductModel product;
			if (ii % 2 == 0)
			{
				product = ySimplePoc;
			}
			else
			{
				product = cpqLaptop;
			}
			configModel.setProduct(Collections.singletonList(product));
			configModel.setUserSessionId(sessionId);
			if ((ii % (maxEntries / entriesToTest)) == 0)
			{
				modelService.saveAll();
				configIdsToTest.add(configId);
				productAndSessionIdsToTest.add(new ProductAndSession(product.getCode(), sessionId));
				LOG.info("Created test data entry #" + ii + ", creation of last " + (maxEntries / entriesToTest) + " took "
						+ (System.currentTimeMillis() - packageStartTime) + " ms");
				packageStartTime = System.currentTimeMillis();
				Transaction.current().commit();
				// make new
				Transaction.current().begin();
			}
		}
		LOG.info("Created " + maxEntries + " in " + (System.currentTimeMillis() - startTime) + " ms");
		LOG.info("ProductConfiguration Table has " + getTotalCount() + " entries AFTER testdata creation");

	}

	protected int getTotalCount()
	{
		final SearchResult<Object> result = flexibleSearchService.search("select {PK} from {productconfiguration}");
		return result.getTotalCount();
	}

	@Test
	public void testExecute()
	{
		for (int ii = 1; ii <= 9; ii++)
		{
			configIdsToTest.clear();
			productAndSessionIdsToTest.clear();
			prepareTestData(MAX_ENTRIES, ENTRIES_TO_TEST);
			checkPerfomanceByConfig();
			checkPerfomanceByProductCode();
		}
	}


	public void checkPerfomanceByConfig()
	{
		final long startTime = System.currentTimeMillis();
		final Iterator<String> iterator = configIdsToTest.iterator();
		while (iterator.hasNext())
		{
			final String configId = iterator.next();
			persistenceService.getByConfigId(configId);
		}
		final double duration = System.currentTimeMillis() - startTime;
		double perConfig = duration / configIdsToTest.size();
		perConfig = Math.round(perConfig * 1000) / 1000d;
		LOG.info("PERF_TEST_RESULT: Reading " + configIdsToTest.size() + " ProductConfigurations by config id took " + duration
				+ " ms. This is " + perConfig + " ms per config");
	}


	public void checkPerfomanceByProductCode()
	{
		jaloSession.setUser(UserManager.getInstance().getAnonymousCustomer());
		final long startTime = System.currentTimeMillis();

		final SessionServiceStub sessionServiceStub = new SessionServiceStub();
		try
		{
			persistenceService.setSessionService(sessionServiceStub);

			final Iterator<ProductAndSession> iterator = productAndSessionIdsToTest.iterator();
			while (iterator.hasNext())
			{

				final ProductAndSession productAndSession = iterator.next();
				sessionServiceStub.sessionStub.sessionId = productAndSession.sessionId;
				persistenceService.getByProductCode(productAndSession.productCode);
			}
			final double duration = System.currentTimeMillis() - startTime;
			double perConfig = duration / productAndSessionIdsToTest.size();
			perConfig = Math.round(perConfig * 1000) / 1000d;
			LOG.info("PERF_TEST_RESULT: Reading " + productAndSessionIdsToTest.size()
					+ " ProductConfigurations by product code took " + duration + " ms. This is " + perConfig + " ms per config");
		}
		finally
		{
			persistenceService.setSessionService(sessionService);
		}
	}





	private class ProductAndSession
	{
		private ProductAndSession(final String productCode, final String sessionId)
		{
			super();
			this.sessionId = sessionId;
			this.productCode = productCode;
		}

		public String sessionId;
		public String productCode;
	}

	private class SessionServiceStub extends DefaultSessionService implements SessionService
	{

		public SessionStub sessionStub = new SessionStub();

		@Override
		public Session getCurrentSession()
		{
			return sessionStub;
		}

	}

	private class SessionStub extends DefaultSession implements Session
	{

		public String sessionId;

		@Override
		public String getSessionId()
		{
			return sessionId;
		}

	}

}

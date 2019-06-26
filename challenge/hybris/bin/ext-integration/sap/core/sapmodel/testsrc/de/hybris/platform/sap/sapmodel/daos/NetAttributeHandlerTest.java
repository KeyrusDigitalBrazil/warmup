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
package de.hybris.platform.sap.sapmodel.daos;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.sap.sapmodel.model.SAPPricingSalesAreaToCatalogModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.store.BaseStoreModel;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.RelationQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.TranslationResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@UnitTest
public class NetAttributeHandlerTest
{

	class FlexibleSearchServiceMock implements FlexibleSearchService
	{

		public <T> T getModelByExample(final T example)
		{
			return null;
		}

		@SuppressWarnings("unchecked")
		public <T> List<T> getModelsByExample(final T example)
		{
			return (List<T>) modelsList;
		}

		public <T> SearchResult<T> search(final FlexibleSearchQuery searchQuery)
		{
			return null;
		}

		public <T> SearchResult<T> search(final String query)
		{
			return null;
		}

		public <T> SearchResult<T> search(final String query, final Map<String, ? extends Object> queryParams)
		{
			return null;
		}

		public <T> SearchResult<T> searchRelation(final ItemModel model, final String attribute, final int start, final int count)
		{
			return null;
		}

		public <T> SearchResult<T> searchRelation(final RelationQuery query)
		{
			return null;
		}

		public <T> T searchUnique(final FlexibleSearchQuery searchQuery)
		{
			return null;
		}

		public TranslationResult translate(final FlexibleSearchQuery searchQuery)
		{
			return null;
		}

	}

	NetAttributeHandler handler;
	List<SAPConfigurationModel> modelsList;
	Collection<BaseStoreModel> baseStoreCollection;
	SAPPricingSalesAreaToCatalogModel data;

	@Before
	public void setUp() throws Exception
	{
		handler = new NetAttributeHandler();
		data = new SAPPricingSalesAreaToCatalogModel();
		modelsList = new ArrayList<SAPConfigurationModel>();
		baseStoreCollection = new ArrayList<BaseStoreModel>();
		handler.flexibleSearchService = new FlexibleSearchServiceMock();
		
		//default search values
		data.setSalesOrganization("1000");
		data.setDistributionChannel("10");
	}

	private Boolean getNet()
	{
		return handler.get(data);
	}

	@Test
	public void testZeroConfigurationModel() throws Exception
	{
		Assert.assertFalse(getNet()); //No model - should hit first "return Boolean.FALSE"
	}
	
	@Test
	public void testOneConfigurationModelOneBaseStore() throws Exception
	{
		//setup one config model and one basestore
		SAPConfigurationModel model = new SAPConfigurationModel();
		model.setSapcommon_salesOrganization("1000");
		model.setSapcommon_distributionChannel("10");
		modelsList.add(model);
		
		BaseStoreModel baseStore = new BaseStoreModel();
		baseStore.setNet(true);
		baseStoreCollection.add(baseStore);
		
		model.setBaseStores(baseStoreCollection);
		
		Assert.assertTrue(getNet()); //simple scenario, returns basestore Net
	}

	@Test
	public void testManyConfigurationModelsOneBaseStore() throws Exception
	{
		SAPConfigurationModel model1 = new SAPConfigurationModel();
		model1.setSapcommon_salesOrganization("1000");
		model1.setSapcommon_distributionChannel("10");
		modelsList.add(model1);
		
		SAPConfigurationModel model2 = new SAPConfigurationModel();
		model2.setSapcommon_salesOrganization("1000");
		model2.setSapcommon_distributionChannel("10");
		modelsList.add(model2);
		
		BaseStoreModel baseStore = new BaseStoreModel();
		baseStore.setNet(true);
		baseStoreCollection.add(baseStore);
		 
		model2.setBaseStores(baseStoreCollection);
		
		Assert.assertTrue(getNet()); //check Net flag of base store in second model
	}

	@Test
	public void testOneConfigurationModelManyBaseStores() throws Exception
	{
		SAPConfigurationModel model = new SAPConfigurationModel();
		model.setSapcommon_salesOrganization("1000");
		model.setSapcommon_distributionChannel("10");
		modelsList.add(model);
		
		BaseStoreModel baseStore1 = new BaseStoreModel();
		baseStore1.setNet(true);
		baseStoreCollection.add(baseStore1);

		BaseStoreModel baseStore2 = new BaseStoreModel();
		baseStore2.setNet(false);
		baseStoreCollection.add(baseStore2);
		
		model.setBaseStores(baseStoreCollection);
		
		Assert.assertTrue(getNet()); //check Net flag of first base store in model
	}
	
	@Test
	public void testZeroBaseStore() throws Exception
	{
		SAPConfigurationModel model = new SAPConfigurationModel();
		model.setSapcommon_salesOrganization("1000");
		model.setSapcommon_distributionChannel("10");
		modelsList.add(model);

		Assert.assertFalse(getNet()); //should hit last "return Boolean.FALSE"
	}
	
	@After
	public void tearDown() throws Exception
	{
	}

}

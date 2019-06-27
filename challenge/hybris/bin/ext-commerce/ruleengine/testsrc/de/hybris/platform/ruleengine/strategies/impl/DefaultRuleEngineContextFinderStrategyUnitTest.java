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
package de.hybris.platform.ruleengine.strategies.impl;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ruleengine.dao.RuleEngineContextDao;
import de.hybris.platform.ruleengine.dao.RulesModuleDao;
import de.hybris.platform.ruleengine.enums.RuleType;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineContextModel;
import de.hybris.platform.ruleengine.model.AbstractRulesModuleModel;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.model.DroolsRuleEngineContextModel;
import de.hybris.platform.ruleengine.strategies.RuleEngineContextForCatalogVersionsFinderStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultRuleEngineContextFinderStrategyUnitTest
{
	@InjectMocks
	private DefaultRuleEngineContextFinderStrategy ruleEngineContextFinderStrategy;
	@Mock
	private RulesModuleDao rulesModuleDao;
	@Mock
	private RuleEngineContextDao ruleEngineContextDao;
	@Mock
	private RuleEngineContextForCatalogVersionsFinderStrategy ruleEngineContextForCatalogVersionsFinderStrategy;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private AbstractOrderModel order;
	@Mock
	private ProductModel product1;
	@Mock
	private ProductModel product2;
	@Mock
	private AbstractOrderEntryModel entry1;
	@Mock
	private AbstractOrderEntryModel entry2;
	@Mock
	private CatalogVersionModel onlineCatalogVersionModel;
	@Mock
	private CatalogVersionModel previewCatalogVersionModel;
	@Mock
	private CatalogModel onlineCatalog;
	@Mock
	private CatalogModel previewCatalog;
	@Mock
	private AbstractRuleEngineContextModel ruleEngineContext;
	@Mock
	private AbstractRuleEngineContextModel previewRuleEngineContext;
	@Mock
	private AbstractRulesModuleModel rulesModule;

	@Before
	public void setUp()
	{
		when(entry1.getProduct()).thenReturn(product1);
		when(entry2.getProduct()).thenReturn(product2);

		when(order.getEntries()).thenReturn(Arrays.asList(entry1, entry2));
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(singletonList(previewCatalogVersionModel));
		when(rulesModuleDao.findActiveRulesModulesByRuleType(RuleType.DEFAULT)).thenReturn(Collections.singletonList(rulesModule));
		when(ruleEngineContextDao.findRuleEngineContextByRulesModule(rulesModule))
				.thenReturn(Collections.singletonList(ruleEngineContext));

		when(ruleEngineContext.getName()).thenReturn("ruleengine-context");
		when(previewRuleEngineContext.getName()).thenReturn("preview-ruleengine-context");

		when(onlineCatalogVersionModel.getCatalog()).thenReturn(onlineCatalog);
		when(previewCatalogVersionModel.getCatalog()).thenReturn(previewCatalog);

		when(onlineCatalog.getName()).thenReturn("online-catalog");
		when(onlineCatalog.getVersion()).thenReturn("Online");
		when(previewCatalog.getName()).thenReturn("preview-catalog");
		when(previewCatalog.getVersion()).thenReturn("Preview");
	}

	@Test
	public void testFindRuleEngineContextsNoRulesModulesFound()
	{
		when(rulesModuleDao.findActiveRulesModulesByRuleType(any())).thenReturn(Collections.emptyList());

		final Optional<AbstractRuleEngineContextModel> fallbackRuleEngineContext = ruleEngineContextFinderStrategy
				.findRuleEngineContext(RuleType.DEFAULT);
		assertThat(fallbackRuleEngineContext.isPresent()).isFalse();
	}

	@Test(expected = IllegalStateException.class)
	public void testFindRuleEngineContextsMoreThan1RulesModulesFound()
	{
		final DroolsKIEModuleModel module1 = new DroolsKIEModuleModel();
		module1.setName("name1");
		final DroolsKIEModuleModel module2 = new DroolsKIEModuleModel();
		module2.setName("name2");
		when(rulesModuleDao.findActiveRulesModulesByRuleType(any())).thenReturn(
				Arrays.asList(module1, module2));

		ruleEngineContextFinderStrategy.findRuleEngineContext(RuleType.DEFAULT);
	}

	@Test
	public void testFindRuleEngineContextsRulesModules()
	{
		final DroolsKIEModuleModel testRulesModule = new DroolsKIEModuleModel();
		when(rulesModuleDao.findActiveRulesModulesByRuleType(any())).thenReturn(
				Collections.singletonList(testRulesModule));
		final DroolsRuleEngineContextModel ruleEngineContextModel = new DroolsRuleEngineContextModel();
		when(ruleEngineContextDao.findRuleEngineContextByRulesModule(testRulesModule)).thenAnswer(
				setupDummyListAnswer(ruleEngineContextModel));

		final Optional<? extends AbstractRuleEngineContextModel> fallbackRuleEngineContext = ruleEngineContextFinderStrategy
				.findRuleEngineContext(RuleType.DEFAULT);
		assertThat(fallbackRuleEngineContext.isPresent()).isTrue();
		assertThat(fallbackRuleEngineContext.get()).isSameAs(ruleEngineContextModel);
	}

	@Test
	public void testFindRuleEngineContextForOrderWithProducts()
	{
		initCatalogVersionsFromProductsAndSession();
		when(product1.getCatalogVersion()).thenReturn(onlineCatalogVersionModel);
		when(product2.getCatalogVersion()).thenReturn(onlineCatalogVersionModel);

		final Optional<AbstractRuleEngineContextModel> ruleEngineContextOpt = ruleEngineContextFinderStrategy
				.findRuleEngineContext(order, RuleType.DEFAULT);
		Mockito.verify(catalogVersionService, times(0)).getSessionCatalogVersions();
		assertThat(ruleEngineContextOpt.isPresent()).isTrue();
		assertThat(ruleEngineContextOpt.get()).isEqualTo(ruleEngineContext);
	}

	@Test
	public void testFindRuleEngineContextForOrderWithProductsHavingDifferentCatalogVersions()
	{
		initCatalogVersionsFromProductsAndSession();
		when(product1.getCatalogVersion()).thenReturn(onlineCatalogVersionModel);
		when(product2.getCatalogVersion()).thenReturn(previewCatalogVersionModel);

		assertThatThrownBy(() -> ruleEngineContextFinderStrategy
				.findRuleEngineContext(order, RuleType.DEFAULT)).isInstanceOf(IllegalStateException.class)
				.hasMessageContaining(
						"Cannot determine unique rule engine context for rule evaluation: more than one rule engine context");
		verify(catalogVersionService, times(0)).getSessionCatalogVersions();
	}

	@Test
	public void testFindRuleEngineContextForOrderWithoutProductsSessionHavingDifferentCatalogVersions()
	{
		when(order.getEntries()).thenReturn(Collections.emptyList());
		initCatalogVersionsFromProductsAndSession();
		when(catalogVersionService.getSessionCatalogVersions())
				.thenReturn(Arrays.asList(onlineCatalogVersionModel, previewCatalogVersionModel));

		assertThatThrownBy(() -> ruleEngineContextFinderStrategy
				.findRuleEngineContext(order, RuleType.DEFAULT)).isInstanceOf(IllegalStateException.class)
				.hasMessageContaining(
						"Cannot determine unique rule engine context for rule evaluation: more than one rule engine context");
		verify(catalogVersionService, times(1)).getSessionCatalogVersions();
	}

	@Test
	public void testFindRuleEngineContextForOrderWithoutProducts()
	{
		when(order.getEntries()).thenReturn(Collections.emptyList());
		initCatalogVersionsFromProductsAndSession();

		final Optional<AbstractRuleEngineContextModel> ruleEngineContextOpt = ruleEngineContextFinderStrategy
				.findRuleEngineContext(order, RuleType.DEFAULT);

		Mockito.verify(catalogVersionService, times(1)).getSessionCatalogVersions();
		assertThat(ruleEngineContextOpt).contains(previewRuleEngineContext);
	}

	@Test
	public void testFindRuleEngineContextForOrderWithoutProductsNoCatalogVersionMapping()
	{
		when(order.getEntries()).thenReturn(Collections.emptyList());
		initEmptyCatalogVersionsFromProductsAndSession();

		final Optional<AbstractRuleEngineContextModel> ruleEngineContextOpt = ruleEngineContextFinderStrategy
				.findRuleEngineContext(order, RuleType.DEFAULT);

		Mockito.verify(catalogVersionService, times(1)).getSessionCatalogVersions();
		Mockito.verify(ruleEngineContextDao, times(1)).findRuleEngineContextByRulesModule(rulesModule);

		assertThat(ruleEngineContextOpt.isPresent()).isTrue();
		assertThat(ruleEngineContextOpt.get()).isEqualTo(ruleEngineContext);
	}

	private void initCatalogVersionsFromProductsAndSession()
	{
		when(ruleEngineContextForCatalogVersionsFinderStrategy
				.findRuleEngineContexts(argThat(new CatalogVersionListMatcher()), eq(RuleType.DEFAULT)))
				.thenReturn(Arrays.asList(ruleEngineContext, previewRuleEngineContext));

		when(ruleEngineContextForCatalogVersionsFinderStrategy
				.findRuleEngineContexts(singletonList(onlineCatalogVersionModel), RuleType.DEFAULT)).thenReturn(
				singletonList(ruleEngineContext));
		when(ruleEngineContextForCatalogVersionsFinderStrategy
				.findRuleEngineContexts(singletonList(previewCatalogVersionModel), RuleType.DEFAULT)).thenReturn(
				singletonList(previewRuleEngineContext));
	}

	private void initEmptyCatalogVersionsFromProductsAndSession()
	{
		when(ruleEngineContextForCatalogVersionsFinderStrategy
				.findRuleEngineContexts(argThat(new CatalogVersionListMatcher()), eq(RuleType.DEFAULT)))
				.thenReturn(Collections.emptyList());

		when(ruleEngineContextForCatalogVersionsFinderStrategy
				.findRuleEngineContexts(singletonList(onlineCatalogVersionModel), RuleType.DEFAULT))
				.thenReturn(Collections.emptyList());
		when(ruleEngineContextForCatalogVersionsFinderStrategy
				.findRuleEngineContexts(singletonList(previewCatalogVersionModel), RuleType.DEFAULT))
				.thenReturn(Collections.emptyList());
	}

	private static <N extends AbstractRuleEngineContextModel> Answer<List<N>> setupDummyListAnswer(final N... values)
	{
		final List<N> someList = new ArrayList<>();

		someList.addAll(Arrays.asList(values));

		return invocation -> someList;
	}

	@SuppressWarnings("unchecked")
	class CatalogVersionListMatcher extends ArgumentMatcher<List<CatalogVersionModel>>
	{
		public boolean matches(Object list)
		{
			List<CatalogVersionModel> catalogVersions = (List<CatalogVersionModel>) list;
			return catalogVersions.size() == 2 && catalogVersions.contains(onlineCatalogVersionModel) && catalogVersions
					.contains(previewCatalogVersionModel);
		}
	}
}

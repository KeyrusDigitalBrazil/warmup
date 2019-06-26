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
package de.hybris.platform.ruleengine.dao.impl;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Lists.newArrayList;
import static de.hybris.platform.ruleengine.dao.impl.DefaultEngineRuleDao.GET_ALL_RULES_FOR_VERSION;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearchException;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengine.model.AbstractRulesModuleModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.time.TimeService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultEngineRuleDaoUnitTest
{
	private static final String RULE_MODULE_NAME = "test_rule_module";

	private static final String RULE_CODE = "test_code";

	@InjectMocks
	private DefaultEngineRuleDao engineRuleDao;

	@Mock
	private FlexibleSearchService flexibleSearchService;

	@Mock
	private TimeService timeService;

	@Mock
	private AbstractRulesModuleModel rulesModuleModel;

	@Captor
	private ArgumentCaptor<FlexibleSearchQuery> flexibleSearchQueryArgumentCaptor;

	@Before
	public void setUp()
	{
		when(timeService.getCurrentTime()).thenReturn(new Date());
		when(rulesModuleModel.getName()).thenReturn(RULE_MODULE_NAME);
	}

	@Test
	public void testGetRuleByUuidSingle()
	{
		final SearchResult<AbstractRuleEngineRuleModel> searchResult = mock(SearchResult.class);

		when(flexibleSearchService.<AbstractRuleEngineRuleModel> search(any(FlexibleSearchQuery.class))).thenReturn(searchResult);
		final String uuid = UUID.randomUUID().toString();
		final List<AbstractRuleEngineRuleModel> ruleModelList = listOfRulesByUuidSingle(uuid);
		when(searchResult.getResult()).thenReturn(ruleModelList);

		final AbstractRuleEngineRuleModel ruleModel = engineRuleDao.getRuleByUuid(uuid);
		assertThat(ruleModel).isNotNull().isInstanceOf(TestAbstractRuleEngineRuleModel.class);
		assertThat(ruleModel.getUuid()).isEqualTo(uuid);
		assertThat(ruleModel.getVersion()).isEqualTo(0l);
	}

	@Test
	public void testGetRuleByUuidMultiple()
	{
		final SearchResult<AbstractRuleEngineRuleModel> searchResult = mock(SearchResult.class);

		when(flexibleSearchService.<AbstractRuleEngineRuleModel> search(any(FlexibleSearchQuery.class))).thenReturn(searchResult);
		final String uuid = UUID.randomUUID().toString();

		final List<AbstractRuleEngineRuleModel> ruleModelList = listOfRulesByUuidMultiple(uuid);
		when(searchResult.getResult()).thenReturn(ruleModelList);

		final AbstractRuleEngineRuleModel ruleModel = engineRuleDao.getRuleByUuid(uuid);
		assertThat(ruleModel).isNotNull().isInstanceOf(TestAbstractRuleEngineRuleModel.class);
		assertThat(ruleModel.getUuid()).isEqualTo(uuid);
		assertThat(ruleModel.getVersion()).isEqualTo(2l);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetRuleByUuidNoArg()
	{
		engineRuleDao.getRuleByUuid(null);
	}

	@Test(expected = FlexibleSearchException.class)
	public void testGetRuleByUuidNotFound()
	{
		when(flexibleSearchService.<AbstractRuleEngineRuleModel> search(any(FlexibleSearchQuery.class)))
				.thenThrow(FlexibleSearchException.class);
		final String uuid = UUID.randomUUID().toString();

		engineRuleDao.getRuleByUuid(uuid);
	}

	@Test
	public void testGetRuleByCodeSingle()
	{
		final SearchResult<AbstractRuleEngineRuleModel> searchResult = mock(SearchResult.class);

		when(flexibleSearchService.<AbstractRuleEngineRuleModel> search(any(FlexibleSearchQuery.class))).thenReturn(searchResult);
		final List<AbstractRuleEngineRuleModel> ruleModelList = listOfRulesByCodeSingle(RULE_CODE);
		when(searchResult.getResult()).thenReturn(ruleModelList);

		final AbstractRuleEngineRuleModel ruleModel = engineRuleDao.getRuleByUuid(RULE_CODE);
		assertThat(ruleModel).isNotNull().isInstanceOf(TestAbstractRuleEngineRuleModel.class);
		assertThat(ruleModel.getCode()).isEqualTo(RULE_CODE);
		assertThat(ruleModel.getVersion()).isEqualTo(0l);
	}

	@Test
	public void testGetRuleByCodeMultiple()
	{
		final SearchResult searchResult = mock(SearchResult.class);
		when(flexibleSearchService.search(any(FlexibleSearchQuery.class))).thenReturn(searchResult);
		final List<AbstractRuleEngineRuleModel> ruleModelList = listOfRulesByCodeMultiple(RULE_CODE);
		when(searchResult.getResult()).thenReturn(ruleModelList);

		final AbstractRuleEngineRuleModel ruleModel = engineRuleDao.getRuleByUuid(RULE_CODE);
		assertThat(ruleModel).isNotNull().isInstanceOf(TestAbstractRuleEngineRuleModel.class);
		assertThat(ruleModel.getCode()).isEqualTo(RULE_CODE);
		assertThat(ruleModel.getVersion()).isEqualTo(2l);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetRuleByCodeNoArg()
	{
		engineRuleDao.getRuleByCode(null, RULE_MODULE_NAME);
	}

	@Test(expected = FlexibleSearchException.class)
	public void testGetRuleByCodeNotFound()
	{
		when(flexibleSearchService.<AbstractRuleEngineRuleModel> search(any(FlexibleSearchQuery.class)))
				.thenThrow(FlexibleSearchException.class);

		engineRuleDao.getRuleByCode(RULE_CODE, RULE_MODULE_NAME);
	}

	@Test
	public void testGetNextRuleVersion()
	{
		final long version = 3l;
		when(flexibleSearchService.<Long> searchUnique(any(FlexibleSearchQuery.class))).thenReturn(version);

		final Long nextRuleVersion = engineRuleDao.getCurrentRulesSnapshotVersion(rulesModuleModel);
		assertThat(nextRuleVersion).isNotNull().isEqualTo(version);
	}

	@Test
	public void testGetNextRuleVersionNotFound()
	{
		when(flexibleSearchService.<Long> searchUnique(any(FlexibleSearchQuery.class))).thenThrow(ModelNotFoundException.class);

		final Long nextRuleVersion = engineRuleDao.getCurrentRulesSnapshotVersion(rulesModuleModel);
		assertThat(nextRuleVersion).isNotNull().isEqualTo(0l);
	}

	@Test
	public void testGetAllRulesForVersion()
	{
		final SearchResult<AbstractRuleEngineRuleModel> searchResult = mock(SearchResult.class);

		when(flexibleSearchService.<AbstractRuleEngineRuleModel> search(any(FlexibleSearchQuery.class))).thenReturn(searchResult);

		final List<AbstractRuleEngineRuleModel> newArrayList = newArrayList(
				concat(listOfRules1(), listOfRules2(), listOfRules3(), listOfRules4()));

		List<AbstractRuleEngineRuleModel> allRules = new ArrayList<>(
				newArrayList.stream().filter(r -> r.getVersion() <= 0).collect(toSet()));
		when(searchResult.getResult()).thenReturn(allRules);

		List<AbstractRuleEngineRuleModel> rulesForVersion = engineRuleDao.getRulesForVersion(RULE_MODULE_NAME, 0);
		assertThat(rulesForVersion).isNotEmpty().hasSize(2).containsOnly(listOfRules1().toArray());

		allRules = new ArrayList<>(newArrayList.stream().filter(r -> r.getVersion() <= 1).collect(toSet()));
		when(searchResult.getResult()).thenReturn(allRules);

		rulesForVersion = engineRuleDao.getRulesForVersion(RULE_MODULE_NAME, 1);
		assertThat(rulesForVersion).isNotEmpty().hasSize(2).containsOnly(listOfRules2().toArray());

		allRules = new ArrayList<>(newArrayList.stream().filter(r -> r.getVersion() <= 2).collect(toSet()));
		when(searchResult.getResult()).thenReturn(allRules);

		rulesForVersion = engineRuleDao.getRulesForVersion(RULE_MODULE_NAME, 2);
		assertThat(rulesForVersion).isNotEmpty().hasSize(3).containsOnly(listOfRules3().toArray());

		allRules = new ArrayList<>(newArrayList.stream().filter(r -> r.getVersion() <= 3).collect(toSet()));
		when(searchResult.getResult()).thenReturn(allRules);

		rulesForVersion = engineRuleDao.getRulesForVersion(RULE_MODULE_NAME, 3);
		assertThat(rulesForVersion).isNotEmpty().hasSize(3).containsOnly(listOfRules4().toArray());

		verify(flexibleSearchService, times(4)).search(flexibleSearchQueryArgumentCaptor.capture());

		flexibleSearchQueryArgumentCaptor.getAllValues().forEach(v -> {
			assertThat(v.getQuery()).isEqualTo(GET_ALL_RULES_FOR_VERSION);
			assertThat(v.getQueryParameters().containsKey("active")).isFalse();
		});
	}

	@Test
	public void testGetActiveRulesForVersion()
	{
		final SearchResult<AbstractRuleEngineRuleModel> searchResult = mock(SearchResult.class);

		when(flexibleSearchService.<AbstractRuleEngineRuleModel> search(any(FlexibleSearchQuery.class))).thenReturn(searchResult);

		final List<AbstractRuleEngineRuleModel> newArrayList = newArrayList(
				concat(listOfRules1(), listOfRules2(), listOfRules3(), listOfRules4()));

		List<AbstractRuleEngineRuleModel> allRules = new ArrayList<>(
				newArrayList.stream().filter(r -> r.getVersion() <= 0).collect(toSet()));
		when(searchResult.getResult()).thenReturn(allRules);

		List<AbstractRuleEngineRuleModel> rulesForVersion = engineRuleDao.getActiveRulesForVersion(RULE_MODULE_NAME, 0);
		assertThat(rulesForVersion).isNotEmpty().hasSize(2).containsOnly(listOfRules1().toArray());

		allRules = new ArrayList<>(newArrayList.stream().filter(r -> r.getVersion() <= 3).collect(toSet()));
		when(searchResult.getResult()).thenReturn(allRules);

		rulesForVersion = engineRuleDao.getActiveRulesForVersion(RULE_MODULE_NAME, 3);
		assertThat(rulesForVersion).isNotEmpty().hasSize(3).containsOnly(listOfRules4().toArray());

		verify(flexibleSearchService, times(2)).search(flexibleSearchQueryArgumentCaptor.capture());

		flexibleSearchQueryArgumentCaptor.getAllValues().forEach(v -> {
			assertThat(v.getQuery()).isEqualTo(GET_ALL_RULES_FOR_VERSION);
		});
	}

	@Test
	public void testGetActiveRulesForVersionAllVersionsActive()
	{
		final SearchResult<AbstractRuleEngineRuleModel> searchResult = mock(SearchResult.class);

		when(flexibleSearchService.<AbstractRuleEngineRuleModel> search(any(FlexibleSearchQuery.class))).thenReturn(searchResult);

		final AbstractRuleEngineRuleModel test_rule1_0 = createRule("test_rule1", 0l);
		final AbstractRuleEngineRuleModel test_rule1_1 = createRule("test_rule1", 1l);

		final List<AbstractRuleEngineRuleModel> allRules = asList(test_rule1_0, test_rule1_1);

		when(searchResult.getResult()).thenReturn(allRules);

		List<AbstractRuleEngineRuleModel> rulesForVersion = engineRuleDao.getActiveRulesForVersion(RULE_MODULE_NAME, 1);
		assertThat(rulesForVersion).isNotEmpty().hasSize(1).containsOnly(test_rule1_1);
	}

	@Test
	public void testGetActiveRulesForVersionNewestRuleNonActive()
	{
		final SearchResult<AbstractRuleEngineRuleModel> searchResult = mock(SearchResult.class);

		when(flexibleSearchService.<AbstractRuleEngineRuleModel> search(any(FlexibleSearchQuery.class))).thenReturn(searchResult);

		final AbstractRuleEngineRuleModel test_rule1_0 = createRule("test_rule1", 0l);
		final AbstractRuleEngineRuleModel test_rule1_1 = createNonActiveRule("test_rule1", 1l);
		final List<AbstractRuleEngineRuleModel> allRules = asList(test_rule1_0, test_rule1_1);

		when(searchResult.getResult()).thenReturn(allRules);
		
		List<AbstractRuleEngineRuleModel> rulesForVersion = engineRuleDao.getActiveRulesForVersion(RULE_MODULE_NAME, 1);
		assertThat(rulesForVersion).isEmpty();
	}

	@Test
	public void testGetRulesForVersionNotFound()
	{
		when(flexibleSearchService.<AbstractRuleEngineRuleModel> search(any(FlexibleSearchQuery.class)))
				.thenThrow(FlexibleSearchException.class);

		final List<AbstractRuleEngineRuleModel> rulesForVersion = engineRuleDao.getRulesForVersion(RULE_MODULE_NAME, 0l);
		assertThat(rulesForVersion).isEmpty();
	}

	@Test
	public void testGetRuleVersion()
	{
		final long version = 3l;
		when(flexibleSearchService.<Long> searchUnique(any(FlexibleSearchQuery.class))).thenReturn(version);

		final Long nextRuleVersion = engineRuleDao.getRuleVersion(RULE_CODE, RULE_MODULE_NAME);
		assertThat(nextRuleVersion).isNotNull().isEqualTo(version);
	}

	@Test
	public void testGetRuleVersionNotFound()
	{
		when(flexibleSearchService.<Long> searchUnique(any(FlexibleSearchQuery.class))).thenThrow(ModelNotFoundException.class);

		final Long nextRuleVersion = engineRuleDao.getRuleVersion(RULE_CODE, RULE_MODULE_NAME);
		assertThat(nextRuleVersion).isNull();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetRuleVersionNoCode()
	{
		engineRuleDao.getRuleVersion(null, RULE_MODULE_NAME);
	}

	@Test
	public void testGetByRuleCodeAndMaxVersion()
	{
		final SearchResult<AbstractRuleEngineRuleModel> searchResult = mock(SearchResult.class);

		when(flexibleSearchService.<AbstractRuleEngineRuleModel> search(any(FlexibleSearchQuery.class))).thenReturn(searchResult);
		final List<AbstractRuleEngineRuleModel> ruleModelList = listOfRulesByCodeSingle(RULE_CODE);
		when(searchResult.getResult()).thenReturn(ruleModelList);

		final AbstractRuleEngineRuleModel ruleModel = engineRuleDao.getRuleByCodeAndMaxVersion(RULE_CODE, RULE_MODULE_NAME, 1l);
		assertThat(ruleModel).isNotNull().isInstanceOf(TestAbstractRuleEngineRuleModel.class);
		assertThat(ruleModel.getCode()).isEqualTo(RULE_CODE);
		assertThat(ruleModel.getVersion()).isEqualTo(0l);
	}

	@Test
	public void testGetByRuleCodeAndMaxVersionNegative()
	{
		final SearchResult<AbstractRuleEngineRuleModel> searchResult = mock(SearchResult.class);

		when(flexibleSearchService.<AbstractRuleEngineRuleModel> search(any(FlexibleSearchQuery.class))).thenReturn(searchResult);
		final List<AbstractRuleEngineRuleModel> ruleModelList = listOfRulesByCodeSingle(RULE_CODE);
		when(searchResult.getResult()).thenReturn(ruleModelList);

		final AbstractRuleEngineRuleModel ruleModel = engineRuleDao.getRuleByCodeAndMaxVersion(RULE_CODE, RULE_MODULE_NAME, -1l);
		assertThat(ruleModel).isNotNull().isInstanceOf(TestAbstractRuleEngineRuleModel.class);
		assertThat(ruleModel.getCode()).isEqualTo(RULE_CODE);
		assertThat(ruleModel.getVersion()).isEqualTo(0l);
	}

	@Test
	public void testGetByRuleCodeAndMaxVersionNoEntries()
	{
		final SearchResult<AbstractRuleEngineRuleModel> searchResult = mock(SearchResult.class);

		when(flexibleSearchService.<AbstractRuleEngineRuleModel> search(any(FlexibleSearchQuery.class))).thenReturn(searchResult);
		final List<AbstractRuleEngineRuleModel> ruleModelList = listOfRulesByCodeSingle(RULE_CODE);
		ruleModelList.stream().forEach(r -> r.setVersion(2l));
		when(searchResult.getResult()).thenReturn(ruleModelList);

		final AbstractRuleEngineRuleModel ruleModel = engineRuleDao.getRuleByCodeAndMaxVersion(RULE_CODE, RULE_MODULE_NAME, 1l);
		assertThat(ruleModel).isNull();
	}

	@Test
	public void testGetRuleVersionByCode()
	{
		final long version = 3l;
		when(flexibleSearchService.<Long> searchUnique(any(FlexibleSearchQuery.class))).thenReturn(version);

		final Long nextRuleVersion = engineRuleDao.getRuleVersion(RULE_CODE, RULE_MODULE_NAME);
		assertThat(nextRuleVersion).isNotNull().isEqualTo(version);
	}

	@Test
	public void testGetRuleVersionByCodeNotFound()
	{
		when(flexibleSearchService.<Long> searchUnique(any(FlexibleSearchQuery.class))).thenThrow(ModelNotFoundException.class);

		final Long nextRuleVersion = engineRuleDao.getRuleVersion(RULE_CODE, RULE_MODULE_NAME);
		assertThat(nextRuleVersion).isNull();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetRuleVersionByCodeNoCode()
	{
		engineRuleDao.getRuleVersion(null, RULE_MODULE_NAME);
	}

	private List<AbstractRuleEngineRuleModel> listOfRules1()
	{
		return asList(createRule("test_rule1", 0l), createRule("test_rule2", 0l));
	}

	private List<AbstractRuleEngineRuleModel> listOfRules2()
	{
		return asList(createRule("test_rule1", 1l), createRule("test_rule2", 0l));
	}

	private List<AbstractRuleEngineRuleModel> listOfRules3()
	{
		return asList(createRule("test_rule1", 1l), createRule("test_rule2", 2l), createRule("test_rule3", 2l));
	}

	private List<AbstractRuleEngineRuleModel> listOfRules4()
	{
		return asList(createRule("test_rule1", 3l), createRule("test_rule2", 2l), createRule("test_rule3", 2l));
	}

	private List<AbstractRuleEngineRuleModel> listOfRulesByUuidSingle(final String uuid)
	{
		return asList(createRule("test_rule", uuid, 0l));
	}

	private List<AbstractRuleEngineRuleModel> listOfRulesByUuidMultiple(final String uuid)
	{
		return asList(createRule("test_rule", uuid, 0l), createRule("test_rule", uuid, 2l));
	}

	private List<AbstractRuleEngineRuleModel> listOfRulesByCodeSingle(final String code)
	{
		return asList(createRule(code, 0l));
	}

	private List<AbstractRuleEngineRuleModel> listOfRulesByCodeMultiple(final String code)
	{
		return asList(createRule(code, 0l), createRule(code, 2l));
	}



	private AbstractRuleEngineRuleModel createRule(final String code, final String uuid, final Long version)
	{
		final AbstractRuleEngineRuleModel ruleModel = new TestAbstractRuleEngineRuleModel();
		ruleModel.setUuid(uuid);
		ruleModel.setCode(code);
		ruleModel.setVersion(version);
		ruleModel.setActive(true);
		return ruleModel;
	}

	private AbstractRuleEngineRuleModel createRule(final String code, final Long version)
	{
		return createRule(code, null, version);
	}

	private AbstractRuleEngineRuleModel createNonActiveRule(final String code, final Long version)
	{
		final AbstractRuleEngineRuleModel ruleModel = new TestAbstractRuleEngineRuleModel();
		ruleModel.setUuid(UUID.randomUUID().toString());
		ruleModel.setCode(code);
		ruleModel.setVersion(version);
		ruleModel.setActive(false);
		return ruleModel;
	}

	private static class TestAbstractRuleEngineRuleModel extends AbstractRuleEngineRuleModel
	{

		@Override
		public int hashCode()
		{
			int result = getCode().hashCode();
			result = 31 * result + getVersion().hashCode();
			return result;
		}

		@Override
		public boolean equals(final Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (getClass() != obj.getClass())
			{
				return false;
			}
			final TestAbstractRuleEngineRuleModel other = (TestAbstractRuleEngineRuleModel) obj;
			if (!getCode().equals(other.getCode()))
			{
				return false;
			}
			if (!getVersion().equals(other.getVersion()))
			{
				return false;
			}
			return true;
		}

		@Override
		public String toString()
		{
			return "TestAbstractRuleEngineRuleModel [getCode()=" + getCode() + ", getVersion()=" + getVersion() + "]";
		}

	}

}

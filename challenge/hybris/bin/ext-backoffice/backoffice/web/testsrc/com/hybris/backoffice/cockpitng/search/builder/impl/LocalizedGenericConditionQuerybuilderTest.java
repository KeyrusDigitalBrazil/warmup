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
package com.hybris.backoffice.cockpitng.search.builder.impl;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.KeywordModel;
import de.hybris.platform.core.GenericCondition;
import de.hybris.platform.core.GenericConditionList;
import de.hybris.platform.core.GenericQuery;
import de.hybris.platform.core.GenericSearchFieldType;
import de.hybris.platform.core.GenericSubQueryCondition;
import de.hybris.platform.core.GenericValueCondition;
import de.hybris.platform.core.Operator;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.internal.i18n.LocalizationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.hybris.backoffice.widgets.advancedsearch.engine.AdvancedSearchQueryData;
import com.hybris.cockpitng.search.data.SearchAttributeDescriptor;
import com.hybris.cockpitng.search.data.SearchQueryCondition;
import com.hybris.cockpitng.search.data.SearchQueryData;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;


@IntegrationTest
public class LocalizedGenericConditionQuerybuilderTest extends ServicelayerTransactionalTest
{
	private static final String typeCode = "Product";
	private CatalogVersionModel version1, version2;

	@Resource
	private TypeService typeService;
	@Resource
	private I18NService i18nService;
	@Resource
	private LocalizationService localizationService;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private ModelService modelService;
	private LocalizedGenericConditionQueryBuilder localizedConditionQueryBuilder;

	@Before
	public void prepare()
	{
		localizedConditionQueryBuilder = new LocalizedGenericConditionQueryBuilder();
		localizedConditionQueryBuilder.setTypeService(typeService);
		localizedConditionQueryBuilder.setCommonI18NService(commonI18NService);
		localizedConditionQueryBuilder.setI18nService(i18nService);
		localizedConditionQueryBuilder.setLocalizationService(localizationService);
		prepareTestObjects();
	}

	private void prepareTestObjects()
	{
		final CatalogModel catalogModel = modelService.create(CatalogModel.class);
		catalogModel.setId("catalogModelId");
		modelService.save(catalogModel);

		version1 = modelService.create(CatalogVersionModel.class);
		version1.setVersion("version1");
		version1.setCatalog(catalogModel);
		modelService.save(version1);

		version2 = modelService.create(CatalogVersionModel.class);
		version2.setVersion("version2");
		version2.setCatalog(catalogModel);
		modelService.save(version2);


	}


	@Test
	public void testLocalizedMultiReferenceManyToManyRelation()
	{
		//given
		final GenericQuery genericQuery = new GenericQuery(typeCode);
		final AdvancedSearchQueryData.Builder builder = new AdvancedSearchQueryData.Builder(typeCode);
		final List<SearchQueryCondition> entries = new LinkedList<>();

		final LanguageModel langModel = getOrCreateLanguageModel("en_US");

		final KeywordModel keyword = modelService.create(KeywordModel.class);
		keyword.setCatalogVersion(version1);
		keyword.setKeyword("keyword");
		keyword.setLanguage(langModel);
		modelService.save(keyword);

		final SearchQueryCondition entry = new SearchQueryCondition();
		entry.setOperator(ValueComparisonOperator.CONTAINS);
		entry.setDescriptor(new SearchAttributeDescriptor("keywords"));
		entry.setValue(Collections.singletonMap(commonI18NService.getLocaleForLanguage(langModel), keyword));
		entries.add(entry);

		builder.conditions(entries);
		builder.globalOperator(ValueComparisonOperator.OR);

		final SearchQueryData searchQueryData = builder.build();

		//when
		final List<GenericCondition> genericConditions = localizedConditionQueryBuilder.buildQuery(genericQuery, typeCode,
				new SearchAttributeDescriptor("keywords"), searchQueryData);

		//then
		assertThat(genericConditions).isNotNull();
		assertThat(genericConditions).hasSize(1);
		assertThat(genericConditions.get(0)).isInstanceOf(GenericConditionList.class);
		final GenericConditionList conditionList = (GenericConditionList) genericConditions.get(0);
		final GenericSubQueryCondition subQueryCondition = (GenericSubQueryCondition) conditionList.getConditionList().get(0);
		assertThat(subQueryCondition.getSubQuery().getCondition()).isInstanceOf(GenericConditionList.class);

		final GenericConditionList subQueryConditionList = (GenericConditionList) subQueryCondition.getSubQuery().getCondition();

		final GenericCondition firstElement = subQueryConditionList.getConditionList().get(0);
		assertThat(firstElement).isInstanceOf(GenericValueCondition.class);
		final Object firstElementsValue = ((GenericValueCondition) firstElement).getValue();
		assertThat(firstElementsValue).isInstanceOf(Collection.class);
		assertThat((Collection) firstElementsValue).hasSize(1);
		assertThat(((Collection) firstElementsValue).iterator().next().equals(keyword)).isTrue();

		assertThat(subQueryConditionList.getConditionList().get(1)).isInstanceOf(GenericValueCondition.class);
		assertThat(((GenericValueCondition) subQueryConditionList.getConditionList().get(1)).getValue().equals(langModel)).isTrue();

		assertThat(subQueryCondition.getOperator()).isEqualTo(Operator.IN);
	}

	@Test
	public void shouldExecuteLocalizedMultiReferenceManyToManyRelationQueryWhenOperatorIsIS_EMPTYAndReferenceCollectionIsNull()
	{
		//given
		final GenericQuery genericQuery = new GenericQuery(typeCode);
		final AdvancedSearchQueryData.Builder builder = new AdvancedSearchQueryData.Builder(typeCode);
		final List<SearchQueryCondition> entries = new LinkedList<>();

		final LanguageModel langModel = getOrCreateLanguageModel("en_US");

		final SearchQueryCondition entry = new SearchQueryCondition();
		entry.setOperator(ValueComparisonOperator.IS_EMPTY);
		entry.setDescriptor(new SearchAttributeDescriptor("keywords"));
		entry.setValue(Collections.singletonMap(commonI18NService.getLocaleForLanguage(langModel), null));
		entries.add(entry);

		builder.conditions(entries);
		builder.globalOperator(ValueComparisonOperator.OR);

		final SearchQueryData searchQueryData = builder.build();

		//when
		final List<GenericCondition> genericConditions = localizedConditionQueryBuilder.buildQuery(genericQuery, typeCode,
				new SearchAttributeDescriptor("keywords"), searchQueryData);

		//then
		assertThat(genericConditions).isNotNull();
		assertThat(genericConditions).hasSize(1);
		assertThat(genericConditions.get(0)).isInstanceOf(GenericConditionList.class);
		final GenericConditionList conditionList = (GenericConditionList) genericConditions.get(0);
		final GenericSubQueryCondition subQueryCondition = (GenericSubQueryCondition) conditionList.getConditionList().get(0);
		assertThat(subQueryCondition.getSubQuery().getCondition()).isInstanceOf(GenericValueCondition.class);

		final GenericValueCondition valueCondition = (GenericValueCondition) subQueryCondition.getSubQuery().getCondition();

		assertThat(valueCondition.getValue()).isInstanceOf(LanguageModel.class);
		assertThat(valueCondition.getValue().equals(langModel)).isTrue();

		assertThat(subQueryCondition.getOperator()).isEqualTo(Operator.NOT_IN);
	}


	@Test
	public void testSearchByLocalizedStringAttribute()
	{
		//given


		final GenericQuery genericQuery = new GenericQuery(typeCode);
		final AdvancedSearchQueryData.Builder builder = new AdvancedSearchQueryData.Builder(typeCode);
		final List<SearchQueryCondition> entries = new LinkedList<>();


		final LanguageModel langModel = getOrCreateLanguageModel("zh_TW");

		final SearchQueryCondition entry = new SearchQueryCondition();
		entry.setOperator(ValueComparisonOperator.STARTS_WITH);
		entry.setDescriptor(new SearchAttributeDescriptor("description", 0));
		entry.setValue(Collections.singletonMap(commonI18NService.getLocaleForLanguage(langModel), "abcd"));
		entries.add(entry);
		builder.conditions(entries).globalOperator(ValueComparisonOperator.OR);

		final SearchQueryData searchQueryData = builder.build();



		//when
		final List<GenericCondition> genericConditions = localizedConditionQueryBuilder.buildQuery(genericQuery, typeCode,
				new SearchAttributeDescriptor("description"), searchQueryData);
		//then
		//then
		assertThat(genericConditions).isNotNull();
		assertThat(genericConditions).hasSize(1);
		assertThat(genericConditions.get(0)).isInstanceOf(GenericConditionList.class);
		final GenericConditionList conditionList = (GenericConditionList) genericConditions.get(0);
		final GenericValueCondition genericValueCondition = (GenericValueCondition) conditionList.getConditionList().get(0);
		assertThat(genericValueCondition.getOperator()).isEqualTo(Operator.LIKE);
		assertThat(genericValueCondition.getField().getFieldTypes()).contains(GenericSearchFieldType.LOCALIZED);
		assertThat(genericValueCondition.getField().getLanguagePK()).isEqualTo(langModel.getPk());
	}

	protected LanguageModel getOrCreateLanguageModel(final String isoCode)
	{
		try
		{
			return commonI18NService.getLanguage(isoCode);
		}
		catch (final Exception e)
		{

			final LanguageModel langModel = modelService.create(LanguageModel.class);
			langModel.setIsocode(isoCode);
			modelService.save(langModel);
			return langModel;
		}
	}

}

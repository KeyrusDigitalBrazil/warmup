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
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.GenericCondition;
import de.hybris.platform.core.GenericConditionList;
import de.hybris.platform.core.GenericQuery;
import de.hybris.platform.core.GenericSubQueryCondition;
import de.hybris.platform.core.GenericValueCondition;
import de.hybris.platform.core.Operator;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.impl.DefaultTypeService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hybris.backoffice.widgets.advancedsearch.engine.AdvancedSearchQueryData;
import com.hybris.cockpitng.search.data.SearchAttributeDescriptor;
import com.hybris.cockpitng.search.data.SearchQueryCondition;
import com.hybris.cockpitng.search.data.SearchQueryData;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;


@IntegrationTest
public class GenericConditionQueryBuilderIntegrationTest extends ServicelayerTransactionalTest
{

	private static final String TYPE_CODE = "Product";
	private static final String TYPE_CODE_PRODUCT_REFERENCE = "ProductReference";
	private final Set<Character> queryBuilderSeparators = Sets.newHashSet(ArrayUtils.toObject(new char[]
	{ ' ', ',', ';', '\t', '\n', '\r' }));
	@Resource
	private DefaultTypeService typeService;
	@Resource
	private ModelService modelService;
	private GenericConditionQueryBuilder queryBuilder;
	private CatalogVersionModel version1, version2;

	@Before
	public void prepare()
	{
		queryBuilder = new GenericConditionQueryBuilder();
		queryBuilder.setTypeService(typeService);
		queryBuilder.setSeparators(queryBuilderSeparators);
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
	public void testSearchByStringAttribute()
	{
		// given
		final GenericQuery genericQuery = new GenericQuery(TYPE_CODE);
		final AdvancedSearchQueryData.Builder builder = new AdvancedSearchQueryData.Builder(TYPE_CODE);

		final List<SearchQueryCondition> entries = new LinkedList<>();

		final SearchQueryCondition entry = new SearchQueryCondition();
		entry.setOperator(ValueComparisonOperator.STARTS_WITH);
		entry.setDescriptor(new SearchAttributeDescriptor("code", 0));
		entry.setValue("abcd");
		entries.add(entry);


		builder.conditions(entries);
		final SearchQueryData searchQueryData = builder.build();
		// when
		final List<GenericCondition> genericConditions = queryBuilder.buildQuery(genericQuery, TYPE_CODE,
				new SearchAttributeDescriptor("code"), searchQueryData);
		// then
		assertThat(genericConditions).hasSize(1);
		assertThat(genericConditions.get(0)).isInstanceOf(GenericValueCondition.class);
		final GenericValueCondition returnedCondition = (GenericValueCondition) genericConditions.get(0);
		assertThat(returnedCondition.getField().getQualifier()).isEqualTo("code");
		assertThat(returnedCondition.getOperator()).isEqualTo(Operator.LIKE);
		assertThat(returnedCondition.getValue()).isInstanceOf(String.class);
		assertThat(returnedCondition.getValue()).isEqualTo("abcd%");
	}

	@Test
	public void testSearchByManyStringTokens()
	{
		// given
		final GenericQuery genericQuery = new GenericQuery(TYPE_CODE);
		final AdvancedSearchQueryData.Builder builder = new AdvancedSearchQueryData.Builder(TYPE_CODE);

		final List<SearchQueryCondition> entries = new LinkedList<>();

		final SearchQueryCondition entry = new SearchQueryCondition();
		entry.setOperator(ValueComparisonOperator.ENDS_WITH);
		entry.setDescriptor(new SearchAttributeDescriptor("code"));
		entry.setValue("abcd efgh");
		entries.add(entry);

		builder.conditions(entries).tokenizable(true);
		final SearchQueryData searchQueryData = builder.build();
		// when
		final List<GenericCondition> genericConditions = queryBuilder.buildQuery(genericQuery, TYPE_CODE,
				new SearchAttributeDescriptor("code"), searchQueryData);
		// then
		assertThat(genericConditions).hasSize(1);
		assertThat(genericConditions.get(0)).isInstanceOf(GenericConditionList.class);
		final GenericConditionList returnedCondition = (GenericConditionList) genericConditions.get(0);
		assertThat(returnedCondition.getConditionList()).hasSize(2);
		assertThat(returnedCondition.getConditionList().get(0)).isInstanceOf(GenericValueCondition.class);
		assertThat(returnedCondition.getConditionList().get(1)).isInstanceOf(GenericValueCondition.class);
		final GenericValueCondition condition1 = (GenericValueCondition) returnedCondition.getConditionList().get(0);
		final GenericValueCondition condition2 = (GenericValueCondition) returnedCondition.getConditionList().get(1);
		assertThat(condition1.getField().getQualifier()).isEqualTo("code");
		assertThat(condition1.getOperator()).isEqualTo(Operator.LIKE);
		assertThat(condition1.getValue()).isInstanceOf(String.class);
		assertThat(condition1.getValue()).isEqualTo("%abcd");
		assertThat(condition2.getField().getQualifier()).isEqualTo("code");
		assertThat(condition2.getOperator()).isEqualTo(Operator.LIKE);
		assertThat(condition2.getValue()).isInstanceOf(String.class);
		assertThat(condition2.getValue()).isEqualTo("%efgh");
	}


	@Test
	public void testSearchByEnumAttribute()
	{
		// given
		final GenericQuery genericQuery = new GenericQuery(TYPE_CODE);
		final AdvancedSearchQueryData.Builder builder = new AdvancedSearchQueryData.Builder(TYPE_CODE);

		final List<SearchQueryCondition> entries = new LinkedList<>();
		final SearchQueryCondition entry = new SearchQueryCondition();
		entry.setOperator(ValueComparisonOperator.EQUALS);
		entry.setDescriptor(new SearchAttributeDescriptor("approvalStatus", 0));
		entry.setValue(ArticleApprovalStatus.APPROVED);
		entries.add(entry);
		builder.conditions(entries);

		final SearchQueryData searchQueryData = builder.build();
		// when
		final List<GenericCondition> genericConditions = queryBuilder.buildQuery(genericQuery, TYPE_CODE,
				new SearchAttributeDescriptor("approvalStatus"), searchQueryData);
		// then
		assertThat(genericConditions).hasSize(1);
		assertThat(genericConditions.get(0)).isInstanceOf(GenericValueCondition.class);
		final GenericValueCondition returnedCondition = (GenericValueCondition) genericConditions.get(0);
		assertThat(returnedCondition.getField().getQualifier()).isEqualTo("approvalStatus");
		assertThat(returnedCondition.getOperator()).isEqualTo(Operator.EQUAL);
		assertThat(returnedCondition.getValue()).isInstanceOf(ArticleApprovalStatus.class);
		assertThat(returnedCondition.getValue()).isEqualTo(ArticleApprovalStatus.APPROVED);
	}

	@Test
	public void testSearchBySingleReference()
	{
		// given
		final GenericQuery genericQuery = new GenericQuery(TYPE_CODE);
		final AdvancedSearchQueryData.Builder builder = new AdvancedSearchQueryData.Builder(TYPE_CODE);
		final List<SearchQueryCondition> entries = new LinkedList<>();

		final SearchQueryCondition entry = new SearchQueryCondition();
		entry.setOperator(ValueComparisonOperator.EQUALS);
		entry.setDescriptor(new SearchAttributeDescriptor("catalogVersion"));
		entry.setValue(version1);
		entries.add(entry);

		builder.conditions(entries);
		final SearchQueryData searchQueryData = builder.build();

		// when
		final List<GenericCondition> genericConditions = queryBuilder.buildQuery(genericQuery, TYPE_CODE,
				new SearchAttributeDescriptor("catalogVersion"), searchQueryData);
		// then
		assertThat(genericConditions).hasSize(1);
		assertThat(genericConditions.get(0)).isInstanceOf(GenericValueCondition.class);
		final GenericValueCondition returnedCondition = (GenericValueCondition) genericConditions.get(0);
		assertThat(returnedCondition.getField().getQualifier()).isEqualTo("catalogVersion");
		assertThat(returnedCondition.getOperator()).isEqualTo(Operator.EQUAL);
		assertThat(returnedCondition.getValue()).isInstanceOf(CatalogVersionModel.class);
		assertThat(returnedCondition.getValue()).isEqualTo(version1);
	}

	@Test
	public void testSearchBySingleReference2Conditions()
	{
		// given
		final GenericQuery genericQuery = new GenericQuery(TYPE_CODE);
		final AdvancedSearchQueryData.Builder builder = new AdvancedSearchQueryData.Builder(TYPE_CODE);
		final List<SearchQueryCondition> entries = new LinkedList<>();

		final SearchQueryCondition entry1 = new SearchQueryCondition();
		entry1.setOperator(ValueComparisonOperator.EQUALS);
		entry1.setDescriptor(new SearchAttributeDescriptor("catalogVersion", 0));
		entry1.setValue(version1);
		entries.add(entry1);

		final SearchQueryCondition entry2 = new SearchQueryCondition();
		entry2.setOperator(ValueComparisonOperator.EQUALS);
		entry2.setDescriptor(new SearchAttributeDescriptor("catalogVersion", 1));
		entry2.setValue(version2);
		entries.add(entry2);

		builder.conditions(entries);
		final SearchQueryData searchQueryData = builder.build();
		// when
		final List<GenericCondition> genericConditions = queryBuilder.buildQuery(genericQuery, TYPE_CODE,
				new SearchAttributeDescriptor("catalogVersion"), searchQueryData);
		genericConditions.addAll(queryBuilder.buildQuery(genericQuery, TYPE_CODE,
				new SearchAttributeDescriptor("catalogVersion", 1), searchQueryData));
		// then
		assertThat(genericConditions).hasSize(2);
		assertThat(genericConditions.get(0)).isInstanceOf(GenericValueCondition.class);
		assertThat(genericConditions.get(1)).isInstanceOf(GenericValueCondition.class);
		final GenericValueCondition returnedCondition = (GenericValueCondition) genericConditions.get(0);
		assertThat(returnedCondition.getField().getQualifier()).isEqualTo("catalogVersion");
		assertThat(returnedCondition.getOperator()).isEqualTo(Operator.EQUAL);
		assertThat(returnedCondition.getValue()).isInstanceOf(CatalogVersionModel.class);
		assertThat(returnedCondition.getValue()).isEqualTo(version1);
		final GenericValueCondition returnedCondition2 = (GenericValueCondition) genericConditions.get(1);
		assertThat(returnedCondition2.getField().getQualifier()).isEqualTo("catalogVersion");
		assertThat(returnedCondition2.getOperator()).isEqualTo(Operator.EQUAL);
		assertThat(returnedCondition2.getValue()).isInstanceOf(CatalogVersionModel.class);
		assertThat(returnedCondition2.getValue()).isEqualTo(version2);
	}

	@Test
	public void testSearchByCollectionTypeShouldReturnEmptyConditionList()
	{
		// project.detail
		// given
		final GenericQuery genericQuery = new GenericQuery(TYPE_CODE);
		final AdvancedSearchQueryData.Builder builder = new AdvancedSearchQueryData.Builder(TYPE_CODE);

		final MediaModel detail = modelService.create(MediaModel.class);
		detail.setCatalogVersion(version1);
		detail.setCode("someCode");
		modelService.save(detail);

		final List<SearchQueryCondition> entries = new LinkedList<>();
		final SearchQueryCondition entry1 = new SearchQueryCondition();
		entry1.setOperator(ValueComparisonOperator.CONTAINS);
		entry1.setDescriptor(new SearchAttributeDescriptor("detail"));
		entry1.setValue(detail);
		entries.add(entry1);

		builder.conditions(entries);
		final SearchQueryData searchQueryData = builder.build();
		// when
		final List<GenericCondition> conditions = queryBuilder.buildQuery(genericQuery, TYPE_CODE,
				new SearchAttributeDescriptor("detail"), searchQueryData);
		assertThat(conditions).isEmpty();
	}



	@Test
	public void testSearchByManyToOneRelation()
	{
		final ProductModel source = modelService.create(ProductModel.class);
		source.setCatalogVersion(version1);
		source.setCode("productCodeSource");
		modelService.save(source);
		final ProductModel target = modelService.create(ProductModel.class);
		target.setCatalogVersion(version1);
		target.setCode("productCodeTarget");
		modelService.save(target);
		final ProductReferenceModel productReference = modelService.create(ProductReferenceModel.class);
		productReference.setActive(Boolean.TRUE);
		productReference.setPreselected(Boolean.TRUE);
		productReference.setSource(source);
		productReference.setTarget(target);
		modelService.save(productReference);

		final GenericQuery genericQuery = new GenericQuery(TYPE_CODE);
		final List<SearchQueryCondition> entries = new LinkedList<>();

		final SearchQueryCondition entry1 = new SearchQueryCondition();
		entry1.setOperator(ValueComparisonOperator.EQUALS);
		entry1.setDescriptor(new SearchAttributeDescriptor("source"));
		entry1.setValue(source);
		entries.add(entry1);

		final AdvancedSearchQueryData.Builder builder = new AdvancedSearchQueryData.Builder(TYPE_CODE_PRODUCT_REFERENCE);
		builder.conditions(entries);
		final SearchQueryData searchQueryData = builder.build();
		// when
		final List<GenericCondition> genericConditions = queryBuilder.buildQuery(genericQuery, TYPE_CODE_PRODUCT_REFERENCE,
				new SearchAttributeDescriptor("source"), searchQueryData);
		assertThat(genericConditions).isNotNull();

		assertThat(genericConditions).hasSize(1);
		assertThat(genericConditions.get(0)).isInstanceOf(GenericValueCondition.class);
		final GenericValueCondition valueCondition = (GenericValueCondition) genericConditions.get(0);
		assertThat(valueCondition.getOperator()).isEqualTo(Operator.EQUAL);
	}

	@Test
	public void testSearchByManyToManyRelation()
	{
		final CategoryModel categoryModel = modelService.create(CategoryModel.class);
		categoryModel.setCatalogVersion(version1);
		categoryModel.setCode("categoryCode1");
		modelService.save(categoryModel);

		final GenericQuery genericQuery = new GenericQuery(TYPE_CODE);

		final List<SearchQueryCondition> entries = new LinkedList<>();
		final SearchQueryCondition entry1 = new SearchQueryCondition();
		entry1.setOperator(ValueComparisonOperator.CONTAINS);
		entry1.setDescriptor(new SearchAttributeDescriptor("supercategories"));
		entry1.setValue(categoryModel);
		entries.add(entry1);

		final AdvancedSearchQueryData.Builder builder = new AdvancedSearchQueryData.Builder(TYPE_CODE);
		builder.conditions(entries);
		final SearchQueryData searchQueryData = builder.build();
		// when
		final List<GenericCondition> genericConditions = queryBuilder.buildQuery(genericQuery, TYPE_CODE,
				new SearchAttributeDescriptor("supercategories"), searchQueryData);
		assertThat(genericConditions).isNotNull();
		assertThat(genericConditions).hasSize(1);
		assertThat(genericConditions.get(0)).isInstanceOf(GenericSubQueryCondition.class);
		final GenericSubQueryCondition subQueryCondition = (GenericSubQueryCondition) genericConditions.get(0);
		assertThat(subQueryCondition.getOperator()).isEqualTo(Operator.IN);
	}

	@Test
	public void testSearchByManyToManyRelationForIsEmptyOperator()
	{
		// given
		final ProductModel productModel = modelService.create(ProductModel.class);
		productModel.setCatalogVersion(version1);
		productModel.setCode("productCode");
		modelService.save(productModel);

		final GenericQuery genericQuery = new GenericQuery(ProductModel._TYPECODE);

		final List<SearchQueryCondition> entries = new LinkedList<>();
		final SearchQueryCondition entry1 = new SearchQueryCondition();
		entry1.setOperator(ValueComparisonOperator.IS_EMPTY);
		entry1.setDescriptor(new SearchAttributeDescriptor("supercategories"));
		entry1.setValue(null);
		entries.add(entry1);

		final AdvancedSearchQueryData.Builder builder = new AdvancedSearchQueryData.Builder(ProductModel._TYPECODE);
		builder.conditions(entries);
		final SearchQueryData searchQueryData = builder.build();

		// when
		final List<GenericCondition> genericConditions = queryBuilder.buildQuery(genericQuery, ProductModel._TYPECODE,
				new SearchAttributeDescriptor("supercategories"), searchQueryData);

		// then
		assertThat(genericConditions).isNotNull();
		assertThat(genericConditions).hasSize(1);
		assertThat(genericConditions.get(0)).isInstanceOf(GenericSubQueryCondition.class);
		final GenericSubQueryCondition subQueryCondition = (GenericSubQueryCondition) genericConditions.get(0);
		assertThat(subQueryCondition.getOperator()).isEqualTo(Operator.NOT_IN);
		genericQuery.addCondition(genericConditions.get(0));
		final StringBuilder flexibleSearch = new StringBuilder();
		genericQuery.toFlexibleSearch(flexibleSearch, Maps.newHashMap(), Maps.newHashMap());
		assertThat(
				"SELECT {Product:PK} FROM {Product AS Product } WHERE {Product:pk} NOT IN ({{ SELECT {CategoryProductRelation:target} FROM {CategoryProductRelation AS CategoryProductRelation } }})")
						.isEqualToIgnoringCase(flexibleSearch.toString());
	}

	@Test
	public void testSearchByManyToManyRelationForIsNotEmptyOperator()
	{
		// given
		final ProductModel productModel = modelService.create(ProductModel.class);
		productModel.setCatalogVersion(version1);
		productModel.setCode("productCode");
		modelService.save(productModel);

		final GenericQuery genericQuery = new GenericQuery(ProductModel._TYPECODE);

		final List<SearchQueryCondition> entries = new LinkedList<>();
		final SearchQueryCondition entry1 = new SearchQueryCondition();
		entry1.setOperator(ValueComparisonOperator.IS_NOT_EMPTY);
		entry1.setDescriptor(new SearchAttributeDescriptor("supercategories"));
		entry1.setValue(null);
		entries.add(entry1);

		final AdvancedSearchQueryData.Builder builder = new AdvancedSearchQueryData.Builder(ProductModel._TYPECODE);
		builder.conditions(entries);
		final SearchQueryData searchQueryData = builder.build();

		// when
		final List<GenericCondition> genericConditions = queryBuilder.buildQuery(genericQuery, ProductModel._TYPECODE,
				new SearchAttributeDescriptor("supercategories"), searchQueryData);

		// then
		assertThat(genericConditions).isNotNull();
		assertThat(genericConditions).hasSize(1);
		assertThat(genericConditions.get(0)).isInstanceOf(GenericSubQueryCondition.class);
		final GenericSubQueryCondition subQueryCondition = (GenericSubQueryCondition) genericConditions.get(0);
		assertThat(subQueryCondition.getOperator()).isEqualTo(Operator.IN);
		genericQuery.addCondition(genericConditions.get(0));
		final StringBuilder flexibleSearch = new StringBuilder();
		genericQuery.toFlexibleSearch(flexibleSearch, Maps.newHashMap(), Maps.newHashMap());
		assertThat(
				"SELECT {Product:PK} FROM {Product AS Product } WHERE {Product:pk} IN ({{ SELECT {CategoryProductRelation:target} FROM {CategoryProductRelation AS CategoryProductRelation } }})")
						.isEqualToIgnoringCase(flexibleSearch.toString());
	}

	@Test
	public void shouldAddConditionWithListOfLongs()
	{
		//given
		final List<Long> values = new ArrayList<>();
		final Long firstValue = new Long(124124123L);
		final Long secondValue = new Long(54213445232L);

		values.add(firstValue);
		values.add(secondValue);

		final List<SearchQueryCondition> entries = new LinkedList<>();
		final SearchQueryCondition entry1 = new SearchQueryCondition();
		entry1.setOperator(ValueComparisonOperator.CONTAINS);
		entry1.setDescriptor(new SearchAttributeDescriptor("pk"));
		entry1.setValue(values);
		entries.add(entry1);

		final AdvancedSearchQueryData.Builder builder = new AdvancedSearchQueryData.Builder(ProductModel._TYPECODE);
		builder.conditions(entries);
		final SearchQueryData searchQueryData = builder.build();

		final GenericQuery genericQuery = new GenericQuery(ProductModel._TYPECODE);

		//when
		final List<GenericCondition> genericConditions = queryBuilder.buildQuery(genericQuery, ProductModel._TYPECODE,
				new SearchAttributeDescriptor("pk"), searchQueryData);

		//then
		assertThat(genericConditions).isNotNull();
		assertThat(genericConditions).hasSize(1);
		final GenericCondition genericCondition = genericConditions.get(0);
		assertThat(genericCondition).isInstanceOf(GenericValueCondition.class);
		final GenericValueCondition genericValueCondition = (GenericValueCondition) genericCondition;
		assertThat(genericValueCondition.getValue()).isInstanceOf(Collection.class);
		assertThat((Collection) genericValueCondition.getValue()).containsOnly(firstValue, secondValue);
		assertThat(genericValueCondition.getOperator()).isEqualTo(Operator.IN);
		assertThat(genericValueCondition.getResettableValues()).isEmpty();
	}


	@Test
	public void shouldAddConditionWithListOfPKs()
	{
		//given
		final List<PK> values = new ArrayList<>();
		final PK firstValue = PK.fromLong(124124123L);
		final PK secondValue = PK.fromLong(54213445232L);

		values.add(firstValue);
		values.add(secondValue);

		final List<SearchQueryCondition> entries = new LinkedList<>();
		final SearchQueryCondition entry1 = new SearchQueryCondition();
		entry1.setOperator(ValueComparisonOperator.CONTAINS);
		entry1.setDescriptor(new SearchAttributeDescriptor("pk"));
		entry1.setValue(values);
		entries.add(entry1);

		final AdvancedSearchQueryData.Builder builder = new AdvancedSearchQueryData.Builder(ProductModel._TYPECODE);
		builder.conditions(entries);
		final SearchQueryData searchQueryData = builder.build();

		final GenericQuery genericQuery = new GenericQuery(ProductModel._TYPECODE);

		//when
		final List<GenericCondition> genericConditions = queryBuilder.buildQuery(genericQuery, ProductModel._TYPECODE,
				new SearchAttributeDescriptor("pk"), searchQueryData);

		//then
		assertThat(genericConditions).isNotNull();
		assertThat(genericConditions).hasSize(1);
		final GenericCondition genericCondition = genericConditions.get(0);
		assertThat(genericCondition).isInstanceOf(GenericValueCondition.class);
		final GenericValueCondition genericValueCondition = (GenericValueCondition) genericCondition;
		assertThat(genericValueCondition.getValue()).isInstanceOf(Collection.class);
		assertThat((Collection) genericValueCondition.getValue()).containsOnly(firstValue, secondValue);
		assertThat(genericValueCondition.getOperator()).isEqualTo(Operator.IN);
		assertThat(genericValueCondition.getResettableValues()).isEmpty();
	}


	@Test
	public void shouldUseLikeInConditionWithStringValue()
	{
		//given
		final List<SearchQueryCondition> entries = new LinkedList<>();
		final String testName = "testName";

		final SearchQueryCondition entry1 = new SearchQueryCondition();
		entry1.setOperator(ValueComparisonOperator.CONTAINS);
		entry1.setDescriptor(new SearchAttributeDescriptor("name"));
		entry1.setValue(testName);
		entries.add(entry1);

		final AdvancedSearchQueryData.Builder builder = new AdvancedSearchQueryData.Builder(ProductModel._TYPECODE);
		builder.conditions(entries);
		final SearchQueryData searchQueryData = builder.build();

		final GenericQuery genericQuery = new GenericQuery(ProductModel._TYPECODE);

		//when
		final List<GenericCondition> genericConditions = queryBuilder.buildQuery(genericQuery, ProductModel._TYPECODE,
				new SearchAttributeDescriptor("name"), searchQueryData);

		//then
		assertThat(genericConditions).isNotNull();
		assertThat(genericConditions).hasSize(1);
		final GenericCondition genericCondition = genericConditions.get(0);
		assertThat(genericCondition).isInstanceOf(GenericValueCondition.class);
		final GenericValueCondition genericValueCondition = (GenericValueCondition) genericCondition;
		assertThat(genericValueCondition.getValue()).isEqualTo("%testName%");
		assertThat(genericValueCondition.getOperator()).isEqualTo(Operator.LIKE);
		assertThat(genericValueCondition.getResettableValues()).isEmpty();
	}
}

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
package com.hybris.backoffice.workflow.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isNull;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.internal.i18n.LocalizationService;
import de.hybris.platform.servicelayer.model.ItemModelContext;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.workflow.ScriptEvaluationService;
import de.hybris.platform.workflow.constants.WorkflowConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.internal.util.collections.Sets;

import com.hybris.backoffice.workflow.WorkflowTemplateActivationAction;
import com.hybris.backoffice.workflow.WorkflowTemplateActivationCtx;
import com.hybris.cockpitng.dataaccess.context.Context;
import com.hybris.cockpitng.dataaccess.context.impl.DefaultContext;
import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;

import bsh.EvalError;


public class DefaultWorkflowTemplateActivationServiceTest
{
	public static final String TEST_ITEM_TYPE = "TestItemType";
	@Mock
	private TypeFacade typeFacade;
	@Mock
	private ScriptEvaluationService scriptEvaluationService;
	@Mock
	private LocalizationService localizationService;
	@Mock
	private TypeService typeService;
	@InjectMocks
	@Spy
	private DefaultWorkflowTemplateActivationService service;
	private Locale[] availableLocales;

	@Before
	public void setUp()
	{
		availableLocales = new Locale[]
		{ Locale.ENGLISH, Locale.GERMAN, Locale.ITALY, Locale.CHINA };
		MockitoAnnotations.initMocks(this);
		service.setWorkflowActivationSupportedTypes(Sets.newSet(TEST_ITEM_TYPE));
		doReturn(Boolean.TRUE).when(typeService).isAssignableFrom(TEST_ITEM_TYPE, TEST_ITEM_TYPE);
		doAnswer(mock -> ((Locale) mock.getArguments()[0]).toLanguageTag()).when(localizationService)
				.getDataLanguageIsoCode(any(Locale.class));
	}

	@Test
	public void testActivateWorkflowTemplates() throws EvalError
	{

		final List<WorkflowTemplateActivationCtx> ctxes = new ArrayList<>();
		final Map<String, Object> currentValues = new HashMap<>();
		final Map<String, Object> initialValues = new HashMap<>();
		ctxes.add(new WorkflowTemplateActivationCtx(mockItemModel(), currentValues, initialValues, null));
		ctxes.add(new WorkflowTemplateActivationCtx(mockItemModel(), currentValues, null, null));
		ctxes.add(new WorkflowTemplateActivationCtx(mockItemModel(), null, initialValues, null));

		service.activateWorkflowTemplates(ctxes);
		verify(scriptEvaluationService).evaluateActivationScripts(any(ItemModel.class), same(currentValues), same(initialValues),
				anyString());
		verify(scriptEvaluationService).evaluateActivationScripts(any(ItemModel.class), same(currentValues), isNull(Map.class),
				anyString());
		verify(scriptEvaluationService).evaluateActivationScripts(any(ItemModel.class), isNull(Map.class), same(initialValues),
				anyString());
	}

	@Test
	public void testPrepareWorkflowTemplateActivationContexts()
	{
		doReturn(new WorkflowTemplateActivationCtx(null, null, null, null)).when(service).prepareWorkflowTemplateActivationCtx(
				any(ItemModel.class), any(WorkflowTemplateActivationAction.class), any(Context.class));

		final Map<ItemModel, WorkflowTemplateActivationAction> itemModels = new HashMap<>();
		itemModels.put(mockItemModel(), WorkflowTemplateActivationAction.CREATE);
		itemModels.put(mockItemModel(), WorkflowTemplateActivationAction.SAVE);
		itemModels.put(mockItemModel(), WorkflowTemplateActivationAction.CREATE);

		final List<WorkflowTemplateActivationCtx> ctxes = service.prepareWorkflowTemplateActivationContexts(itemModels, null);
		assertThat(ctxes).hasSize(3);
		verify(service, times(2)).prepareWorkflowTemplateActivationCtx(any(ItemModel.class),
				same(WorkflowTemplateActivationAction.CREATE), any(Context.class));
		verify(service, times(1)).prepareWorkflowTemplateActivationCtx(any(ItemModel.class),
				same(WorkflowTemplateActivationAction.SAVE), any(Context.class));
	}

	@Test
	public void testCreateWorkflowTemplateActivationCtxForSave() throws TypeNotFoundException
	{
		final DataType dataType = mockDataTypeWithAttributes("a", "b", "c", "d");
		when(typeFacade.load(TEST_ITEM_TYPE)).thenReturn(dataType);

		final ItemModelContext itemModelContext = mockItemModelCtxWithAttributes(Sets.newSet("a", "b"), Sets.newSet("c", "d"));
		doReturn(itemModelContext).when(service).getItemModelContext(any(ItemModel.class));
		doReturn(Boolean.FALSE).when(itemModelContext).isNew();

		final Context invocationCtx = createFakeInvocationCtx("ctxAttr1", "ctxAttr2");

		final ItemModel itemModel = mockItemModel();
		final WorkflowTemplateActivationCtx ctx = service.prepareWorkflowTemplateActivationCtx(itemModel,
				WorkflowTemplateActivationAction.SAVE, invocationCtx);

		verify(service).prepareContextForSave(itemModel, dataType, invocationCtx);
		assertThat(ctx.getWorkflowOperationType()).isEqualTo(WorkflowConstants.WorkflowActivationScriptActions.SAVE);
		assertThat(ctx.getInitialValues()).hasSize(4);
		assertThat(ctx.getCurrentValues()).hasSize(4);
		assertThat(ctx.getAttribute("ctxAttr1")).isEqualTo("ctxAttr1VAL");
		assertThat(ctx.getAttribute("ctxAttr2")).isEqualTo("ctxAttr2VAL");
	}

	@Test
	public void testCreateWorkflowTemplateActivationCtxForCreate() throws TypeNotFoundException
	{
		final DataType dataType = mockDataTypeWithAttributes("a", "b", "c", "d");
		when(typeFacade.load(TEST_ITEM_TYPE)).thenReturn(dataType);

		final ItemModelContext itemModelContext = mockItemModelCtxWithAttributes(Sets.newSet("a", "b"), Sets.newSet("c", "d"));
		doReturn(itemModelContext).when(service).getItemModelContext(any(ItemModel.class));
		doReturn(Boolean.TRUE).when(itemModelContext).isNew();

		final Context invocationCtx = createFakeInvocationCtx("ctxAttr1", "ctxAttr2");

		final ItemModel itemModel = mockItemModel();
		final WorkflowTemplateActivationCtx ctx = service.prepareWorkflowTemplateActivationCtx(itemModel,
				WorkflowTemplateActivationAction.CREATE, invocationCtx);

		verify(service).prepareContextForCreate(itemModel, dataType, invocationCtx);
		assertThat(ctx.getWorkflowOperationType()).isEqualTo(WorkflowConstants.WorkflowActivationScriptActions.CREATE);
		assertThat(ctx.getInitialValues()).hasSize(4);
		assertThat(ctx.getCurrentValues()).isEmpty();
		assertThat(ctx.getAttribute("ctxAttr1")).isEqualTo("ctxAttr1VAL");
		assertThat(ctx.getAttribute("ctxAttr2")).isEqualTo("ctxAttr2VAL");
	}

	@Test
	public void testCreateWorkflowTemplateActivationCtxForRemove() throws TypeNotFoundException
	{
		final DataType dataType = mockDataTypeWithAttributes("a", "b", "c", "d");
		when(typeFacade.load(TEST_ITEM_TYPE)).thenReturn(dataType);

		final ItemModelContext itemModelContext = mockItemModelCtxWithAttributes(Sets.newSet("a", "b"), Sets.newSet("c", "d"));
		doReturn(itemModelContext).when(service).getItemModelContext(any(ItemModel.class));
		doReturn(Boolean.FALSE).when(itemModelContext).isNew();

		final Context invocationCtx = createFakeInvocationCtx("ctxAttr1", "ctxAttr2");

		final ItemModel itemModel = mockItemModel();
		service.prepareWorkflowTemplateActivationCtx(itemModel, WorkflowTemplateActivationAction.REMOVE, invocationCtx);

		verify(service).prepareContextForRemove(itemModel, dataType, invocationCtx);

	}

	@Test
	public void testCollectCurrentValues()
	{
		final ItemModel itemModel = mockItemModel();

		final Map<String, Set<Locale>> localizedAttributes = new HashMap<>();
		localizedAttributes.put("c", Sets.newSet(Locale.CANADA, Locale.CHINA));
		localizedAttributes.put("d", Sets.newSet(Locale.ENGLISH, Locale.GERMAN, Locale.ITALY));

		final Map<String, Object> originalValues = service.collectCurrentValues(itemModel, Sets.newSet("a", "b"),
				localizedAttributes);

		assertThat(originalValues).hasSize(4);
		assertThat(originalValues.get("a")).isEqualTo(getCurrentValue("a"));
		assertThat(originalValues.get("b")).isEqualTo(getCurrentValue("b"));

		assertThat(originalValues.get("c")).isInstanceOf(Map.class);
		assertThat(((Map<String, Object>) originalValues.get("c")).get(Locale.CANADA.toLanguageTag()))
				.isEqualTo(getCurrentValue("c", Locale.CANADA));
		assertThat(((Map<String, Object>) originalValues.get("c")).get(Locale.CHINA.toLanguageTag()))
				.isEqualTo(getCurrentValue("c", Locale.CHINA));

		assertThat(originalValues.get("d")).isInstanceOf(Map.class);
		assertThat(((Map<String, Object>) originalValues.get("d")).get(Locale.ENGLISH.toLanguageTag()))
				.isEqualTo(getCurrentValue("d", Locale.ENGLISH));
		assertThat(((Map<String, Object>) originalValues.get("d")).get(Locale.GERMAN.toLanguageTag()))
				.isEqualTo(getCurrentValue("d", Locale.GERMAN));

		// check if is case insensitive
		assertThat(originalValues.get("a")).isEqualTo(originalValues.get("A"));
	}

	@Test
	public void testCollectOriginalValues()
	{
		final ItemModelContext itemModelContext = mockItemModelCtxWithAttributes(Sets.newSet("a", "b"), Sets.newSet("c", "d"));

		final Map<String, Set<Locale>> localizedAttributes = new HashMap<>();
		localizedAttributes.put("c", Sets.newSet(Locale.CANADA, Locale.CHINA));
		localizedAttributes.put("d", Sets.newSet(Locale.ENGLISH, Locale.GERMAN, Locale.ITALY));

		final Map<String, Object> originalValues = service.collectOriginalValues(itemModelContext, Sets.newSet("a", "b"),
				localizedAttributes);

		assertThat(originalValues).hasSize(4);
		assertThat(originalValues.get("a")).isEqualTo(getOriginalValue("a"));
		assertThat(originalValues.get("b")).isEqualTo(getOriginalValue("b"));

		assertThat(originalValues.get("c")).isInstanceOf(Map.class);
		assertThat(((Map<String, Object>) originalValues.get("c")).get(Locale.CANADA.toLanguageTag()))
				.isEqualTo(getOriginalValue("c", Locale.CANADA));
		assertThat(((Map<String, Object>) originalValues.get("c")).get(Locale.CHINA.toLanguageTag()))
				.isEqualTo(getOriginalValue("c", Locale.CHINA));

		assertThat(originalValues.get("d")).isInstanceOf(Map.class);
		assertThat(((Map<String, Object>) originalValues.get("d")).get(Locale.ENGLISH.toLanguageTag()))
				.isEqualTo(getOriginalValue("d", Locale.ENGLISH));
		assertThat(((Map<String, Object>) originalValues.get("d")).get(Locale.GERMAN.toLanguageTag()))
				.isEqualTo(getOriginalValue("d", Locale.GERMAN));

		// check if is case insensitive
		assertThat(originalValues.get("a")).isEqualTo(originalValues.get("A"));
	}

	@Test
	public void testCollectValues()
	{
		final Map<String, Object> values = service.collectValues(Sets.newSet("a", "b", "c"), "VAL"::concat);
		assertThat(values).hasSize(3);
		assertThat(values.get("a")).isEqualTo("VAL".concat("a"));
		assertThat(values.get("b")).isEqualTo("VAL".concat("b"));
		assertThat(values.get("c")).isEqualTo("VAL".concat("c"));
	}

	@Test
	public void testCollectLocalizedValues()
	{
		final BiFunction<String, Locale, Object> localizedValueSupp = (attribute, locale) -> locale.toLanguageTag()
				.concat(attribute);

		final Map<String, Set<Locale>> localizedAttributes = new HashMap<>();
		localizedAttributes.put("a", Sets.newSet(Locale.CANADA, Locale.CHINA));
		localizedAttributes.put("b", Sets.newSet(Locale.ENGLISH, Locale.GERMAN, Locale.ITALY));

		final Map<String, Object> localizedValues = service.collectLocalizedValues(localizedAttributes, localizedValueSupp);

		assertThat(localizedValues.get("a")).isInstanceOf(Map.class);
		assertThat(((Map) localizedValues.get("a"))).hasSize(2);
		assertThat(((Map) localizedValues.get("a")).get(Locale.CANADA.toLanguageTag()))
				.isEqualTo(Locale.CANADA.toLanguageTag().concat("a"));
		assertThat(((Map) localizedValues.get("a")).get(Locale.CHINA.toLanguageTag()))
				.isEqualTo(Locale.CHINA.toLanguageTag().concat("a"));


		assertThat(localizedValues.get("b")).isInstanceOf(Map.class);
		assertThat(((Map) localizedValues.get("b"))).hasSize(3);
		assertThat(((Map) localizedValues.get("b")).get(Locale.ENGLISH.toLanguageTag()))
				.isEqualTo(Locale.ENGLISH.toLanguageTag().concat("b"));
		assertThat(((Map) localizedValues.get("b")).get(Locale.GERMAN.toLanguageTag()))
				.isEqualTo(Locale.GERMAN.toLanguageTag().concat("b"));
		assertThat(((Map) localizedValues.get("b")).get(Locale.ITALY.toLanguageTag()))
				.isEqualTo(Locale.ITALY.toLanguageTag().concat("b"));
	}

	@Test
	public void testGetLocalizedValuesForAttribute()
	{
		final BiFunction<String, Locale, Object> localizedValueSupp = (attribute, locale) -> locale.toLanguageTag()
				.concat(attribute);

		final Map<String, Object> localizedValue = service.getLocalizedValuesForAttribute("a",
				Sets.newSet(Locale.CANADA, Locale.ENGLISH), localizedValueSupp);

		assertThat(localizedValue).hasSize(2);
		assertThat(localizedValue.get(Locale.ENGLISH.toLanguageTag())).isEqualTo(localizedValueSupp.apply("a", Locale.ENGLISH));
		assertThat(localizedValue.get(Locale.CANADA.toLanguageTag())).isEqualTo(localizedValueSupp.apply("a", Locale.CANADA));
	}

	@Test
	public void testCollectModifiedAttributes()
	{
		final DataType dataType = mockDataTypeWithAttributes("a", "b", "c");
		final ItemModelContext itemModelContext = mockItemModelCtxWithAttributes(Sets.newSet("b", "c", "d"),
				Collections.emptySet());

		final Set<String> modified = service.collectModifiedAttributes(itemModelContext, dataType);

		assertThat(modified).hasSize(2);
		assertThat(modified).contains("b", "c");
	}

	@Test
	public void testCollectModifiedLocalizedAttributes()
	{
		final DataType dataType = mockDataTypeWithAttributes("a", "b", "c");
		final ItemModelContext itemModelContext = mockItemModelCtxWithAttributes(Collections.emptySet(),
				Sets.newSet("b", "c", "d"));

		final Map<String, Set<Locale>> modified = service.collectModifiedLocalizedAttributes(itemModelContext, dataType);

		assertThat(modified).hasSize(2);
		assertThat(modified.keySet()).contains("b", "c");
		assertThat(modified.get("b")).hasSize(availableLocales.length);
		assertThat(modified.get("c")).hasSize(availableLocales.length);
	}

	@Test
	public void testIsSupportedType()
	{
		doReturn(Boolean.TRUE).when(typeService).isAssignableFrom("Product", "VariantProduct");
		doReturn(Boolean.FALSE).when(typeService).isAssignableFrom("Product", "Catalog");
		service.setWorkflowActivationSupportedTypes(Sets.newSet("Product", TEST_ITEM_TYPE));

		assertThat(service.isSupportedType("VariantProduct")).isTrue();
		assertThat(service.isSupportedType("Catalog")).isFalse();
	}

	protected ItemModelContext mockItemModelCtxWithAttributes(final Set<String> attributes, final Set<String> localizedAttributes)
	{
		final ItemModelContext itemModelContext = mock(ItemModelContext.class);
		when(itemModelContext.getDirtyAttributes()).thenReturn(attributes);
		when(itemModelContext.getDirtyLocalizedAttributes())
				.thenReturn(Arrays.stream(availableLocales).collect(Collectors.toMap(loc -> loc, loc -> localizedAttributes)));
		doAnswer(mock -> getOriginalValue((String) mock.getArguments()[0])).when(itemModelContext)
				.getOriginalValue(any(String.class));
		doAnswer(mock -> {
			final Object[] args = mock.getArguments();
			return getOriginalValue((String) args[0], (Locale) args[1]);
		}).when(itemModelContext).getOriginalValue(any(String.class), any(Locale.class));

		return itemModelContext;
	}

	protected ItemModel mockItemModel()
	{
		final ItemModel itemModel = mock(ItemModel.class);

		doAnswer(mock -> getCurrentValue((String) mock.getArguments()[0])).when(itemModel).getProperty(any(String.class));
		doAnswer(mock -> {
			final Object[] args = mock.getArguments();
			return getCurrentValue((String) args[0], (Locale) args[1]);
		}).when(itemModel).getProperty(any(String.class), any(Locale.class));
		when(itemModel.getItemtype()).thenReturn(TEST_ITEM_TYPE);
		return itemModel;
	}

	protected String getOriginalValue(final String value)
	{
		return value.concat("ORIGINAL");
	}

	protected String getOriginalValue(final String value, final Locale locale)
	{
		return value.concat(locale.toLanguageTag()).concat("ORIGINAL");
	}

	protected String getCurrentValue(final String value)
	{
		return value.concat("CURRENT");
	}

	protected String getCurrentValue(final String value, final Locale locale)
	{
		return value.concat(locale.toLanguageTag()).concat("CURRENT");
	}

	protected DataType mockDataTypeWithAttributes(final String... attributes)
	{
		final DataType dataType = mock(DataType.class);

		final List<DataAttribute> dataAttributes = new ArrayList<>();
		for (final String attribute : attributes)
		{
			final DataAttribute dataAttribute = mock(DataAttribute.class);
			dataAttributes.add(dataAttribute);
			when(dataType.getAttribute(attribute)).thenReturn(dataAttribute);
			when(dataAttribute.getQualifier()).thenReturn(attribute);
		}
		when(dataType.getAttributes()).thenReturn(dataAttributes);
		return dataType;
	}

	protected Context createFakeInvocationCtx(final String... attributes)
	{
		final DefaultContext ctx = new DefaultContext();
		for (final String attr : attributes)
		{

			ctx.addAttribute(attr, attr.concat("VAL"));
		}
		return ctx;
	}
}

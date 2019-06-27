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
package com.hybris.backoffice.renderer.attributeschooser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.assertj.core.util.Maps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hybris.backoffice.attributechooser.Attribute;
import com.hybris.backoffice.attributechooser.AttributeChooserForm;
import com.hybris.backoffice.excel.classification.ExcelClassificationService;
import com.hybris.backoffice.excel.translators.ExcelAttributeTranslatorRegistry;
import com.hybris.cockpitng.labels.LabelService;


/**
 * <pre>
 * Structure of test data:
 *
 * - 2 classification categories, each with 2 classification attributes:
 *     - hardware
 *         - name
 *         - manufacturer
 *     - cpu
 *         - speed
 *         - cores
 * - 2 Products:
 *     - intelCore
 *     - amdRyzen
 *
 * Each product is assigned to both hardware & cpu categories
 * </pre>
 */
@RunWith(MockitoJUnitRunner.class)
public class ClassificationAttributesExportRendererTest extends AbstractAttributesExportRendererTest
{
	public static final String HARDWARE = "Hardware";
	public static final int HARDWARE_NAME_PK = 1;
	public static final int HARDWARE_MANUFACTURER_PK = 2;
	public static final int CPU_SPEED_PK = 3;
	public static final int CPU_CORES_PK = 4;
	public static final int HARDWARE_PK = 100;
	public static final int CPU_PK = 200;

	@Mock
	private ExcelClassificationService excelClassificationService;
	@Mock
	private ExcelAttributeTranslatorRegistry excelAttributeTranslatorRegistry;

	@Mock
	private ClassificationClassModel hardware;
	@Mock
	private ClassificationAttributeModel name;
	@Mock
	private ClassificationAttributeModel manufacturer;
	@Mock
	private ClassAttributeAssignmentModel hardwareName;
	@Mock
	private ClassAttributeAssignmentModel hardwareManufacturer;
	@Mock
	private Predicate<ClassificationSystemModel> blacklistedClassificationPredicate;
	@Mock
	private LabelService labelService;


	@Mock
	private ClassificationClassModel cpu;
	@Mock
	private ClassificationAttributeModel speed;
	@Mock
	private ClassificationAttributeModel cores;
	@Mock
	private ClassAttributeAssignmentModel cpuSpeed;
	@Mock
	private ClassAttributeAssignmentModel cpuCores;
	@Mock
	private ClassificationSystemVersionModel classificationSystemVersion;

	@Mock
	private ClassificationSystemModel blacklistedClassificationSystem;
	@Mock
	private ClassificationSystemVersionModel blacklistedVersion;
	@Mock
	private ClassificationClassModel blacklistedClass;
	@Mock
	private ClassificationAttributeModel blacklistedAttribute;
	@Mock
	private ClassAttributeAssignmentModel blacklistedAssignment;

	@InjectMocks
	private ClassificationAttributesExportRenderer renderer;
	private final List<ItemModel> products = new ArrayList<>();


	@Override
	@Before
	public void setUp()
	{
		super.setUp();

		params.put(ClassificationAttributesExportRenderer.PARAM_RETRIEVE_MODE,
				ClassificationAttributesExportRenderer.RETRIEVE_MODE_ITEMS_INTERSECTION);

		when(classificationSystemVersion.getPk()).thenReturn(PK.fromLong(1000));
		// classification - see javadoc for test data structure
		initializeClassificationClass(hardware, HARDWARE, HARDWARE_PK, Arrays.asList(hardwareName, hardwareManufacturer));
		initializeClassificationClass(cpu, "Cpu", CPU_PK, Arrays.asList(cpuSpeed, cpuCores));
		initializeClassificationClass(blacklistedClass, "Blacklisted class", 1L, Arrays.asList(blacklistedAssignment));

		initializeClassAttributeAssignment(hardwareName, HARDWARE_NAME_PK, hardware, name);
		initializeClassAttributeAssignment(hardwareManufacturer, HARDWARE_MANUFACTURER_PK, hardware, manufacturer);
		initializeClassAttributeAssignment(cpuSpeed, CPU_SPEED_PK, cpu, speed);
		initializeClassAttributeAssignment(cpuCores, CPU_CORES_PK, cpu, cores);
		initializeClassAttributeAssignment(blacklistedAssignment, 1, blacklistedClass, blacklistedAttribute);

		when(name.getName()).thenReturn("Name");
		when(manufacturer.getName()).thenReturn("Manufacturer");
		when(speed.getName()).thenReturn("Speed");
		when(cores.getName()).thenReturn("Cores");

		// products & pageable
		final ProductModel intelCore = createProductWithClassificationClasses(hardware, cpu, blacklistedClass);
		final ProductModel amdRyzen = createProductWithClassificationClasses(hardware, cpu, blacklistedClass);
		Collections.addAll(products, intelCore, amdRyzen);
		when(pageable.getAllResults()).thenReturn(products);

		// services
		when(permissionFacade.canReadInstance(any())).thenReturn(true);
		when(excelAttributeTranslatorRegistry.canHandle(any())).thenReturn(true);

		wim.getModel().put("attributesForm", new AttributeChooserForm());
		wim.getModel().put("itemsToEdit", pageable.getAllResults());

		given(excelClassificationService.getItemsIntersectedClassificationClasses(products))
				.willReturn(Maps.newHashMap(classificationSystemVersion, Lists.newArrayList(hardware, cpu)));
		given(blacklistedClassificationPredicate.test(blacklistedClassificationSystem)).willReturn(true);
	}

	private ProductModel createProductWithClassificationClasses(final ClassificationClassModel... classes)
	{
		final ProductModel product = mock(ProductModel.class);
		final FeatureList features = mock(FeatureList.class);
		when(features.getClassificationClasses()).thenReturn(Sets.newHashSet(classes));
		return product;
	}

	private void initializeClassAttributeAssignment(final ClassAttributeAssignmentModel assignment, final int pk,
			final ClassificationClassModel parentClass, final ClassificationAttributeModel attribute)
	{
		when(assignment.getPk()).thenReturn(PK.fromLong(pk));
		when(assignment.getClassificationClass()).thenReturn(parentClass);
		when(assignment.getClassificationAttribute()).thenReturn(attribute);
		when(assignment.getLocalized()).thenReturn(false);
		when(assignment.getMandatory()).thenReturn(false);
	}

	private void initializeClassificationClass(final ClassificationClassModel classificationClass, final String name,
			final long pk, final List<ClassAttributeAssignmentModel> classAttributeAssignments)
	{
		when(classificationClass.getName()).thenReturn(name);
		when(classificationClass.getDeclaredClassificationAttributeAssignments()).thenReturn(classAttributeAssignments);
		when(classificationClass.getCatalogVersion()).thenReturn(classificationSystemVersion);
		when(classificationClass.getPk()).thenReturn(PK.fromLong(pk));
	}

	@Test
	public void shouldPopulateAttributeChooserFormWithClassificationAttributes()
	{
		// when
		renderer.render(parent, null, params, null, wim);

		// then
		final AttributeChooserForm form = captureAttributesChooserForm();
		assertThat(form.getAvailableAttributes()).hasSize(1);
		final Set<Attribute> classificationAttributes = form.getAvailableAttributes().iterator().next().getSubAttributes();
		assertThat(classificationAttributes).hasSize(2);

		final Optional<Attribute> foundHW = classificationAttributes.stream()
				.filter(attr -> String.valueOf(HARDWARE_PK).equals(attr.getQualifier())).findFirst();
		assertThat(foundHW.isPresent()).isTrue();
		assertThat(foundHW.get().getSubAttributes().stream().map(Attribute::getQualifier))
				.containsOnly(String.valueOf(HARDWARE_NAME_PK), String.valueOf(HARDWARE_MANUFACTURER_PK));

		final Optional<Attribute> foundCPU = classificationAttributes.stream()
				.filter(attr -> String.valueOf(CPU_PK).equals(attr.getQualifier())).findFirst();
		assertThat(foundCPU.isPresent()).isTrue();
		assertThat(foundCPU.get().getSubAttributes().stream().map(Attribute::getQualifier))
				.containsOnly(String.valueOf(CPU_CORES_PK), String.valueOf(CPU_SPEED_PK));

		assertThat(form.getChosenAttributes()).hasSize(0);
	}

	@Test
	public void shouldHandleMandatoryAttributes()
	{
		// given
		when(hardwareName.getMandatory()).thenReturn(true);

		// when
		renderer.render(parent, null, params, null, wim);

		// then
		final AttributeChooserForm form = captureAttributesChooserForm();
		assertThat(form.getChosenAttributes()).hasSize(1);
		final Set<Attribute> classificationAttributes = form.getChosenAttributes().iterator().next().getSubAttributes();
		assertThat(classificationAttributes).hasSize(1);

		final Optional<Attribute> foundHW = classificationAttributes.stream()
				.filter(attr -> String.valueOf(HARDWARE_PK).equals(attr.getQualifier())).findFirst();
		assertThat(foundHW.isPresent()).isTrue();
		assertThat(foundHW.get().getSubAttributes().stream().map(Attribute::getQualifier))
				.containsOnly(String.valueOf(HARDWARE_NAME_PK));
	}

	@Test
	public void shouldHandleLocalizedAttributes()
	{
		// given
		when(hardwareName.getLocalized()).thenReturn(true);

		// when
		renderer.render(parent, null, params, null, wim);

		// then
		final AttributeChooserForm form = captureAttributesChooserForm();
		assertThat(form.getAvailableAttributes()).hasSize(1);
		final Set<Attribute> classificationAttributes = form.getAvailableAttributes().iterator().next().getSubAttributes();
		assertThat(classificationAttributes).hasSize(2);

		final Optional<Attribute> foundHW = classificationAttributes.stream()
				.filter(attr -> String.valueOf(HARDWARE_PK).equals(attr.getQualifier())).findFirst();
		assertThat(foundHW.isPresent()).isTrue();

		final Optional<Attribute> foundName = foundHW.get().getSubAttributes().stream()
				.filter(attr -> String.valueOf(HARDWARE_NAME_PK).equals(attr.getQualifier())).findAny();
		assertThat(foundName.isPresent()).isTrue();
		assertThat(foundName.get().getSubAttributes()).hasSize(2);
		assertThat(foundName.get().getSubAttributes().stream().map(Attribute::getIsoCode)).containsOnly("en", "de");
	}

	@Test
	public void shouldCreateCorrectAttributeName()
	{
		//given
		when(hardware.getDeclaredClassificationAttributeAssignments()).thenReturn(Lists.newArrayList(hardwareName));
		when(cpu.getDeclaredClassificationAttributeAssignments()).thenReturn(Collections.emptyList());
		when(labelService.getShortObjectLabel(hardwareName)).thenReturn("Hardware - label");
		// when
		renderer.render(parent, null, params, null, wim);

		// then
		final AttributeChooserForm form = captureAttributesChooserForm();
		assertThat(form.getAvailableAttributes()).hasSize(1);
		final Set<Attribute> classificationAttributes = form.getAvailableAttributes().iterator().next().getSubAttributes();
		assertThat(classificationAttributes).hasSize(1);

		final Optional<Attribute> foundHW = classificationAttributes.stream()
				.filter(attr -> String.valueOf(HARDWARE_PK).equals(attr.getQualifier())).findFirst();
		assertThat(foundHW.isPresent()).isTrue();
		assertThat(foundHW.get().getSubAttributes().stream().map(Attribute::getDisplayName).collect(Collectors.toList()))
				.containsOnly("Hardware - label");
	}

	@Test
	public void shouldFilterAttributesUsingTranslatorRegistry()
	{
		//given
		renderer.setSupportedAttributesPredicate(attr -> !hardwareName.equals(attr) && !hardwareManufacturer.equals(attr));

		// when
		renderer.render(parent, null, params, null, wim);

		// then
		final AttributeChooserForm form = captureAttributesChooserForm();
		assertThat(form.getAvailableAttributes()).hasSize(1);
		final Set<Attribute> classificationAttributes = form.getAvailableAttributes().iterator().next().getSubAttributes();
		assertThat(classificationAttributes).hasSize(1);


		final Optional<Attribute> foundCPU = classificationAttributes.stream()
				.filter(attr -> String.valueOf(CPU_PK).equals(attr.getQualifier())).findFirst();
		assertThat(foundCPU.isPresent()).isTrue();
		assertThat(foundCPU.get().getSubAttributes().stream().map(Attribute::getQualifier))
				.containsOnly(String.valueOf(CPU_CORES_PK), String.valueOf(CPU_SPEED_PK));
	}

	@Test
	public void shouldFilterByInstancePermission()
	{
		//given
		when(permissionFacade.canReadInstance(hardware)).thenReturn(false);

		// when
		renderer.render(parent, null, params, null, wim);

		// then
		final AttributeChooserForm form = captureAttributesChooserForm();
		assertThat(form.getAvailableAttributes()).hasSize(1);
		final Set<Attribute> classificationAttributes = form.getAvailableAttributes().iterator().next().getSubAttributes();
		assertThat(classificationAttributes).hasSize(1);


		final Optional<Attribute> foundCPU = classificationAttributes.stream()
				.filter(attr -> String.valueOf(CPU_PK).equals(attr.getQualifier())).findFirst();
		assertThat(foundCPU.isPresent()).isTrue();
		assertThat(foundCPU.get().getSubAttributes().stream().map(Attribute::getQualifier))
				.containsOnly(String.valueOf(CPU_CORES_PK), String.valueOf(CPU_SPEED_PK));
	}

}

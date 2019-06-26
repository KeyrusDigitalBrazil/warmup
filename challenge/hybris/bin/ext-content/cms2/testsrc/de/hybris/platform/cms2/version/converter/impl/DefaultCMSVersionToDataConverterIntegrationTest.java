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
package de.hybris.platform.cms2.version.converter.impl;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cms2.enums.LinkTargets;
import de.hybris.platform.cms2.model.CMSVersionModel;
import de.hybris.platform.cms2.version.AbstractCMSVersionIntegrationTest;
import de.hybris.platform.cms2.version.converter.customattribute.CMSVersionCustomAttribute;
import de.hybris.platform.core.PK;
import de.hybris.platform.persistence.audit.payload.PayloadDeserializer;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;


@IntegrationTest
@SuppressWarnings("unchecked")
public class DefaultCMSVersionToDataConverterIntegrationTest extends AbstractCMSVersionIntegrationTest
{
	private final String VERSION_LABEL = "someLabel";
	private final String VERSION_DESCRIPTION = "someDescription";

	private final String CONTENT_PAGE_UID = "homepage";
	private final String CONTENT_PAGE_LABEL = "homepage";
	private final String CONTENT_PAGE_NAME = "Homepage";
	private final String CONTENT_PAGE_TITLE = "homePageTitle";
	private final String CMS_LINK_COMPONENT_UID = "LinkInSlot";
	private final String CONTENT_PAGE_TEMPLATE_UID = "mainTemplate";
	private final String CONTENT_SLOT_UID = "BodySlot";
	private final String CMS_LINK_NAME = "LinkInSlotName";
	private final String CMS_LINK_URL = "cmsLinkInSlotUrl";
	private final String CMS_LINK_TARGET = "SAMEWINDOW";
	private final String TEMPLATE_NAME = "Main template";
	private final String CONTENT_SLOT_NAME = "Add To Cart";
	private final String EN = "en";

	private final String CATALOG_VERSION_ATTRIBUTE = "catalogVersion";
	private final String DEFAULT_PAGE_ATTRIBUTE = "defaultPage";
	private final String HOMEPAGE_ATTRIBUTE = "homepage";
	private final String MASTER_TEMPLATE_ATTRIBUTE = "masterTemplate";
	private final String LABEL_ATTRIBUTE = "label";
	private final String NAME_ATTRIBUTE = "name";
	private final String UID_ATTRIBUTE = "uid";
	private final String TITLE_ATTRIBUTE = "title";
	private final String URL_ATTRIBUTE = "url";
	private final String TARGET_ATTRIBUTE = "target";
	private final String ACTIVE_ATTRIBUTE = "active";
	private final String CMS_COMPONENTS_ATTRIBUTE = "cmsComponents";
	private final String CUSTOM_ATTRIBUTE = "contentSlotsForPage";

	private CMSVersionPayloadAnalyzer comparator;

	@Resource
	private FlexibleSearchService flexibleSearchService;
	@Resource
	private PayloadDeserializer payloadDeserializer;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		comparator = new CMSVersionPayloadAnalyzer(payloadDeserializer, flexibleSearchService, modelService);
	}

	@Test
	public void shouldGenerateFourVersionsForContentPageAndAllRelatedItems()
	{
		// WHEN
		cmsVersionService.createRevisionForItem(contentPage);

		// THEN
		final List<CMSVersionModel> createdVersions = getAllGeneratedVersions();
		assertThat(createdVersions, hasSize(5));
	}

	@Test
	public void shouldCreateValidRelatedChildren()
	{
		// WHEN
		final CMSVersionModel cmsVersionModel = cmsVersionService.createVersionForItem(contentPage, VERSION_LABEL, VERSION_DESCRIPTION);

		// THEN
		final List<CMSVersionModel> createdVersions = getAllGeneratedVersions();
		assertNotNull(cmsVersionModel.getRelatedChildren());
		assertThat(cmsVersionModel.getRelatedChildren(), hasSize(4));

		assertTrue(createdVersions.containsAll(cmsVersionModel.getRelatedChildren()));
		assertTrue(cmsVersionModel.getRelatedChildren().stream()
				.allMatch(result -> CollectionUtils.isEmpty(result.getRelatedChildren())));
	}

	@Test
	public void shouldSaveAllContentPageAttributesToVersion()
	{
		// WHEN
		final CMSVersionModel cmsVersionModel = cmsVersionService.createRevisionForItem(contentPage);
		comparator.analyse(cmsVersionModel.getPayload());

		// THEN
		assertCommonAttributes(comparator, CONTENT_PAGE_NAME, CONTENT_PAGE_UID);

		CMSVersionPayloadAssertionBuilder.aModel()
				.withComparator(comparator)
				.withAttribute(DEFAULT_PAGE_ATTRIBUTE)
				.withAssertions(isBooleanType, isNotCollection, isNotVersionValue, isNotPKValue, assertValue(true))
				.check();

		CMSVersionPayloadAssertionBuilder.aModel()
				.withComparator(comparator)
				.withAttribute(HOMEPAGE_ATTRIBUTE)
				.withAssertions(isBooleanType, isNotCollection, isNotVersionValue, isNotPKValue, assertValue(true))
				.check();

		CMSVersionPayloadAssertionBuilder.aModel()
				.withComparator(comparator)
				.withAttribute(MASTER_TEMPLATE_ATTRIBUTE)
				.withAssertions(isPKType, isNotCollection, isVersionValue, isPKValue, doNotAssertValue)
				.check();

		CMSVersionPayloadAssertionBuilder.aModel()
				.withComparator(comparator)
				.withAttribute(LABEL_ATTRIBUTE)
				.withAssertions(isStringType, isNotCollection, isNotVersionValue, isNotPKValue, assertValue(CONTENT_PAGE_LABEL))
				.check();

		CMSVersionPayloadAssertionBuilder.aModel()
				.withComparator(comparator)
				.withAttribute(TITLE_ATTRIBUTE)
				.withAssertions(isStringType, isNotCollection, isNotVersionValue, isNotPKValue, assertValue(CONTENT_PAGE_TITLE, EN))
				.check();

		CMSVersionPayloadAssertionBuilder.aModel()
				.withComparator(comparator)
				.withAttribute(CUSTOM_ATTRIBUTE)
				.withAssertions(isCustomType, isCollection, isVersionValue, isPKValue, doNotAssertValue)
				.check();
	}

	@Test
	public void shouldSaveAllContentSlotAttributesApartFromContentPageAttributes()
	{
		// WHEN
		cmsVersionService.createRevisionForItem(contentPage);
		final CMSVersionModel cmsVersionModel = getVersionByAttributeName(CONTENT_SLOT_UID);
		comparator.analyse(cmsVersionModel.getPayload());

		// THEN
		assertCommonAttributes(comparator, CONTENT_SLOT_NAME, CONTENT_SLOT_UID);

		CMSVersionPayloadAssertionBuilder.aModel()
				.withComparator(comparator)
				.withAttribute(ACTIVE_ATTRIBUTE)
				.withAssertions(isBooleanType, isNotCollection, isNotVersionValue, isNotPKValue, assertValue(true))
				.check();

		CMSVersionPayloadAssertionBuilder.aModel()
				.withComparator(comparator)
				.withAttribute(CMS_COMPONENTS_ATTRIBUTE)
				.withAssertions(isPKType, isCollection, isVersionValue, isPKValue, doNotAssertValue)
				.check();
	}

	@Test
	public void shouldSaveAllPageTemplateAttributesApartFromContentPageAttributes()
	{
		// WHEN
		cmsVersionService.createRevisionForItem(contentPage);
		final CMSVersionModel cmsVersionModel = getVersionByAttributeName(CONTENT_PAGE_TEMPLATE_UID);
		comparator.analyse(cmsVersionModel.getPayload());

		// THEN
		assertCommonAttributes(comparator, TEMPLATE_NAME, CONTENT_PAGE_TEMPLATE_UID);
	}

	@Test
	public void shouldSaveAllCMSLinkAttributesApartFromContentPageAttributes()
	{
		// WHEN
		cmsVersionService.createRevisionForItem(contentPage);
		final CMSVersionModel cmsVersionModel = getVersionByAttributeName(CMS_LINK_COMPONENT_UID);
		comparator.analyse(cmsVersionModel.getPayload());

		// THEN
		assertCommonAttributes(comparator, CMS_LINK_NAME, CMS_LINK_COMPONENT_UID);

		CMSVersionPayloadAssertionBuilder.aModel()
				.withComparator(comparator)
				.withAttribute(URL_ATTRIBUTE)
				.withAssertions(isStringType, isNotCollection, isNotVersionValue, isNotPKValue, assertValue(CMS_LINK_URL))
				.check();

		CMSVersionPayloadAssertionBuilder.aModel()
				.withComparator(comparator)
				.withAttribute(TARGET_ATTRIBUTE)
				.withAssertions(isLinkTargetsType, isNotCollection, isNotVersionValue, isNotPKValue, assertValue(CMS_LINK_TARGET))
				.check();
	}

	protected void assertCommonAttributes(final CMSVersionPayloadAnalyzer comparator, final String name, final String uid)
	{
		CMSVersionPayloadAssertionBuilder.aModel()
				.withComparator(comparator)
				.withAttribute(CATALOG_VERSION_ATTRIBUTE)
				.withAssertions(isPKType, isNotCollection, isNotVersionValue, isPKValue)
				.check();

		CMSVersionPayloadAssertionBuilder.aModel()
				.withComparator(comparator)
				.withAttribute(NAME_ATTRIBUTE)
				.withAssertions(isStringType, isNotCollection, isNotVersionValue, isNotPKValue, assertValue(name))
				.check();

		CMSVersionPayloadAssertionBuilder.aModel()
				.withComparator(comparator)
				.withAttribute(UID_ATTRIBUTE)
				.withAssertions(isStringType, isNotCollection, isNotVersionValue, isNotPKValue, assertValue(uid))
				.check();
	}


	Consumer<CMSVersionPayloadAnalyzer.PayloadAttribute> isCollection = payloadAttribute ->
			assertTrue("Attribute " + payloadAttribute.name + " expected to be a collection", payloadAttribute.isCollection);
	Consumer<CMSVersionPayloadAnalyzer.PayloadAttribute> isNotCollection = payloadAttribute ->
			assertFalse("Attribute " + payloadAttribute.name + " expected not to be a collection", payloadAttribute.isCollection);
	Consumer<CMSVersionPayloadAnalyzer.PayloadAttribute> isPKValue = payloadAttribute ->
			assertTrue("Attribute " + payloadAttribute.name + " expected to have all values as PKs",
					payloadAttribute.values.stream().allMatch(value -> value.containsPK));
	Consumer<CMSVersionPayloadAnalyzer.PayloadAttribute> isNotPKValue = payloadAttribute ->
			assertTrue("Attribute " + payloadAttribute.name + " expected to have all values as not PKs",
					payloadAttribute.values.stream().noneMatch(value -> value.containsPK));
	Consumer<CMSVersionPayloadAnalyzer.PayloadAttribute> isVersionValue = payloadAttribute ->
			assertTrue("Attribute " + payloadAttribute.name + " expected to have all PK values as versions",
					payloadAttribute.values.stream().allMatch(value -> value.containsVersionPK));
	Consumer<CMSVersionPayloadAnalyzer.PayloadAttribute> isNotVersionValue = payloadAttribute ->
			assertTrue("Attribute " + payloadAttribute.name + " expected to have all PK values as not versions",
					payloadAttribute.values.stream().noneMatch(value -> value.containsVersionPK));


	Consumer<CMSVersionPayloadAnalyzer.PayloadAttribute> isStringType = payloadAttribute ->
			assertEquals("Attribute " + payloadAttribute.name + " expected to be a String", String.class.getName(),
					payloadAttribute.type);
	Consumer<CMSVersionPayloadAnalyzer.PayloadAttribute> isBooleanType = payloadAttribute ->
			assertEquals("Attribute " + payloadAttribute.name + " expected to be a Boolean", Boolean.class.getName(),
					payloadAttribute.type);
	Consumer<CMSVersionPayloadAnalyzer.PayloadAttribute> isLinkTargetsType = payloadAttribute ->
			assertEquals("Attribute " + payloadAttribute.name + " expected to be a Boolean", LinkTargets.class.getName(),
					payloadAttribute.type);
	Consumer<CMSVersionPayloadAnalyzer.PayloadAttribute> isPKType = payloadAttribute ->
			assertTrue("Attribute " + payloadAttribute.name + " expected to be a PK", isPKAttributeType(payloadAttribute.type));
	Consumer<CMSVersionPayloadAnalyzer.PayloadAttribute> isCustomType = payloadAttribute ->
			assertTrue("Attribute " + payloadAttribute.name + " expected to be a Custom",
					isCustomAttributeType(payloadAttribute.type));

	Consumer<CMSVersionPayloadAnalyzer.PayloadAttribute> doNotAssertValue = param -> {
	};

	Consumer<CMSVersionPayloadAnalyzer.PayloadAttribute> assertValue(final Object value)
	{
		return assertValue(value, null);
	}

	Consumer<CMSVersionPayloadAnalyzer.PayloadAttribute> assertValue(final Object value, final String language)
	{
		return (payloadAttribute -> {
			if (payloadAttribute.isCollection)
			{
				final List<String> payloadValues = payloadAttribute.values.stream()
						.filter(val -> language == null || val.language.equals(language)).map(val -> val.rawValue).collect(toList());
				assertEquals("Attribute " + payloadAttribute.name + " expected to have the following values: " + String
						.join(",", (List) value), value, payloadValues);
			}
			else
			{
				final String payloadValue = payloadAttribute.values.stream().map(val -> val.rawValue).findFirst().get();
				assertEquals("Attribute " + payloadAttribute.name + " expected to have the following value: " + value, payloadValue,
						value.toString());
			}
		});
	}


	protected boolean isCustomAttributeType(final String payloadType)
	{
		Class typeClass = null;
		try
		{
			typeClass = Class.forName(payloadType);
		}
		catch (final ClassNotFoundException e)
		{
			// ignore
		}

		return typeClass != null && CMSVersionCustomAttribute.class.isAssignableFrom(typeClass);
	}

	protected CMSVersionModel getVersionByAttributeName(final String attributeName)
	{
		return getAllGeneratedVersions().stream().filter(version -> version.getItemUid().equals(attributeName)).findFirst().get();
	}

	protected List<CMSVersionModel> getAllGeneratedVersions()
	{
		final SearchResult<CMSVersionModel> result = flexibleSearchService.search("SELECT {pk} FROM {CMSVersion}");
		return result.getResult();
	}

	protected boolean isPKAttributeType(final String payloadType)
	{
		Class typeClass = null;
		try
		{
			typeClass = Class.forName(payloadType);
		}
		catch (final ClassNotFoundException e)
		{
			// ignore
		}

		return typeClass != null && PK.class.isAssignableFrom(typeClass);
	}
}

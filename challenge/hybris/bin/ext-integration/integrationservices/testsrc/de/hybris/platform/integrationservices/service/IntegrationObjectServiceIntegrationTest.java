/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.integrationservices.service;

import static de.hybris.platform.integrationservices.IntegrationObjectItemAttributeBuilder.attribute;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.IntegrationObjectBuilder;
import de.hybris.platform.integrationservices.IntegrationObjectItemAttributeBuilder;
import de.hybris.platform.integrationservices.IntegrationObjectItemBuilder;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.util.IntegrationObjectItemsContext;
import de.hybris.platform.integrationservices.util.IntegrationObjectsContext;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import java.util.Collections;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Rule;
import org.junit.Test;

@IntegrationTest
public class IntegrationObjectServiceIntegrationTest extends ServicelayerTest
{
	private static final String OUTBOUND_PRODUCT = "OutboundProduct";
	private static final String INBOUND_PRODUCT = "InboundProduct";
	private static final String PRODUCT = "Product";
	private static final String PRODUCT_ALIAS = "ProductCode";
	private static final String CATEGORY = "Category";

	private static final String CODE_FIELD = "code";
	private static final String ATTRIBUTE_NAME = "name";
	private static final String ATTRIBUTE_ALIAS_NAME = "aliasName";

	@Rule
	public IntegrationObjectItemsContext integrationObjectItemsContext = IntegrationObjectItemsContext.create();
	@Rule
	public IntegrationObjectsContext integrationObjectsContext = IntegrationObjectsContext.create();

	@Resource
	private IntegrationObjectService integrationObjectService;

	@Test
	public void testFindIntegrationObjectItem()
	{
		final IntegrationObjectItemModel itemModel = givenItem(PRODUCT).build();
		integrationObjectsContext.givenExist(
				givenIntegrationObject(OUTBOUND_PRODUCT).addIntegrationObjectItem(itemModel).build());

		final Set<IntegrationObjectItemModel> result = integrationObjectService.findAllDependencyTypes(PRODUCT, OUTBOUND_PRODUCT);

		assertThat(result).isNotNull().hasSize(1);
		final IntegrationObjectItemModel model = result.iterator().next();
		assertThat(model).hasFieldOrPropertyWithValue(CODE_FIELD, PRODUCT);
		assertThat(model.getIntegrationObject()).isNotNull().hasFieldOrPropertyWithValue(CODE_FIELD, OUTBOUND_PRODUCT);
	}

	@Test
	public void testFindIntegrationObjectItem_NonExistingIntegrationObject()
	{
		final IntegrationObjectItemModel itemModel = givenItem(PRODUCT).build();
		integrationObjectsContext.givenExist(
				givenIntegrationObject(OUTBOUND_PRODUCT).addIntegrationObjectItem(itemModel).build());

		final Set<IntegrationObjectItemModel> result =
				integrationObjectService.findAllDependencyTypes(PRODUCT, "nonExistingIntegrationObject");

		assertThat(result).isEmpty();
	}

	@Test
	public void testFindIntegrationObjectItem_MultipleIntegrationObjectsSameItemCode()
	{
		final IntegrationObjectItemModel inboundItem = givenItem(PRODUCT).build();
		final IntegrationObjectItemModel outboundItem = givenItem(PRODUCT).build();
		integrationObjectsContext.givenExist(
				givenIntegrationObject(OUTBOUND_PRODUCT).addIntegrationObjectItem(inboundItem).build());
		integrationObjectsContext.givenExist(
				givenIntegrationObject(INBOUND_PRODUCT).addIntegrationObjectItem(outboundItem).build());

		final Set<IntegrationObjectItemModel> result =
				integrationObjectService.findAllDependencyTypes(PRODUCT, INBOUND_PRODUCT);

		assertThat(result).isNotNull().hasSize(1);
		final IntegrationObjectItemModel model = result.iterator().next();
		assertThat(model).hasFieldOrPropertyWithValue(CODE_FIELD, PRODUCT);
		assertThat(model.getIntegrationObject()).isNotNull().hasFieldOrPropertyWithValue(CODE_FIELD, INBOUND_PRODUCT);
	}

	@Test
	public void testFindIntegrationObjectItem_notFound()
	{
		final IntegrationObjectItemModel[] categories = integrationObjectItemsContext.givenExist(givenItem(CATEGORY));

		final Set<IntegrationObjectItemModel> result =
				integrationObjectService.findAllDependencyTypes(PRODUCT, categories[0].getIntegrationObject().getCode());

		assertThat(result).isEmpty();
	}

	@Test
	public void testFindIntegrationObjectItem_nullIntegrationItemCode()
	{
		final Set<IntegrationObjectItemModel> result =
				integrationObjectService.findAllDependencyTypes(null, "doesNotMatter");

		assertThat(result).isEmpty();
	}

	@Test
	public void testFindAllIntegrationObjectItems()
	{
		final IntegrationObjectItemModel[] models = integrationObjectItemsContext.givenExist(
				givenItem(CATEGORY),
				givenItem(PRODUCT));
		final String contextIntegrationObjectCode = models[0].getIntegrationObject().getCode();

		final Set<IntegrationObjectItemModel> result = integrationObjectService.findAllIntegrationObjectItems(contextIntegrationObjectCode);

		assertThat(result).containsExactlyInAnyOrder(models[0], models[1]);
	}

	@Test
	public void testFindAllIntegrationObjectItems_noIntegrationObjectsDefined()
	{
		final Set<IntegrationObjectItemModel> result = integrationObjectService.findAllIntegrationObjectItems("anyIntegrationObject");

		assertThat(result).isEmpty();
	}

	@Test
	public void testFindAllDependencyTypes_usingAttributeDescriptor()
	{
		final IntegrationObjectItemModel unit = givenItem("Unit").build();
		final IntegrationObjectItemModel product = givenItem(PRODUCT)
				.withAttribute(attribute()
						.named("unit").forObjectOfType(unit.getType())
						.returnIntegrationObjectType(unit))
				.build();
		integrationObjectItemsContext.givenExist(unit, product);
		final String contextIntegrationObjectCode = product.getIntegrationObject().getCode();

		final Set<IntegrationObjectItemModel> result = integrationObjectService.findAllDependencyTypes(PRODUCT, contextIntegrationObjectCode);

		assertThat(result).containsExactlyInAnyOrder(product, unit);
	}

	@Test
	public void testFindAllDependencyTypes_usingReturnIntegrationObjectItem()
	{
		final IntegrationObjectItemModel catalog = givenItem("Catalog").build();
		final IntegrationObjectItemModel catalogVersion = givenItem("CatalogVersion")
				.withAttribute(attribute()
						.named("catalog").forObjectOfType(catalog.getType())
						.returnIntegrationObjectType(catalog)
				).build();
		final IntegrationObjectItemModel product = givenItem(PRODUCT)
				.withAttribute(attribute()
						.named("catalogVersion").forObjectOfType(catalogVersion.getType())
						.returnIntegrationObjectType(catalogVersion)
				)
				.build();
		integrationObjectItemsContext.givenExist(catalog, catalogVersion, product);
		final String contextIntegrationObjectCode = catalog.getIntegrationObject().getCode();

		final Set<IntegrationObjectItemModel> result = integrationObjectService.findAllDependencyTypes(PRODUCT, contextIntegrationObjectCode);

		assertThat(result).containsExactlyInAnyOrder(product, catalogVersion, catalog);
	}

	@Test
	public void testFindAllDependencyTypes_noDependenciesDefined()
	{
		final IntegrationObjectItemModel[] products = integrationObjectItemsContext.givenExist(givenItem(PRODUCT));
		final String contextIntegrationObjectCode = products[0].getIntegrationObject().getCode();

		final Set<IntegrationObjectItemModel> result = integrationObjectService.findAllDependencyTypes(PRODUCT, contextIntegrationObjectCode);

		assertThat(result).containsExactlyInAnyOrder(products[0]);
	}

	@Test
	public void testFindAllDependencyTypes_circularDependencyDoesNotCauseStackOverflow()
	{
		final IntegrationObjectItemModel catalogVersion = givenItem("CatalogVersion").build();
		final IntegrationObjectItemModel category = givenItem(CATEGORY).build();
		final IntegrationObjectItemAttributeModel rootCategoriesAttr = givenAttribute(catalogVersion.getType())
				.named("rootCategories")
				.returnIntegrationObjectType(category)
				.build();
		final IntegrationObjectItemAttributeModel catalogVersionAttr = givenAttribute(category.getType())
				.named("catalogVersion")
				.returnIntegrationObjectType(catalogVersion)
				.build();
		catalogVersion.setAttributes(Collections.singleton(rootCategoriesAttr));
		category.setAttributes(Collections.singleton(catalogVersionAttr));
		integrationObjectItemsContext.givenExist(catalogVersion, category);
		final String contextIntegrationObjectCode = category.getIntegrationObject().getCode();

		final Set<IntegrationObjectItemModel> result = integrationObjectService.findAllDependencyTypes(CATEGORY, contextIntegrationObjectCode);

		assertThat(result).containsExactlyInAnyOrder(catalogVersion, category);
	}

	@Test
	public void testFindAllIntegrationObjects()
	{
		//OutBound only with Product
		final IntegrationObjectItemModel product1 = givenItem(PRODUCT).build();
		final IntegrationObjectModel model1 = givenIntegrationObject(OUTBOUND_PRODUCT).addIntegrationObjectItem(product1).build();

		//Inbound with Product and Category
		final IntegrationObjectItemModel product2 = givenItem(PRODUCT).build();
		final IntegrationObjectItemModel category = givenItem(CATEGORY).build();
		final IntegrationObjectModel model2 = givenIntegrationObject(INBOUND_PRODUCT).addIntegrationObjectItem(product2).addIntegrationObjectItem(category).build();

		integrationObjectsContext.givenExist(model1,model2);

		final Set<IntegrationObjectModel> resultProduct = integrationObjectService.findAllIntegrationObjects(PRODUCT);

		assertThat(resultProduct).hasSize(2).extracting("code")
								 .containsExactlyInAnyOrder(OUTBOUND_PRODUCT, INBOUND_PRODUCT);

		final Set<IntegrationObjectModel> resultCatalog = integrationObjectService.findAllIntegrationObjects(CATEGORY);

		assertThat(resultCatalog).hasSize(1).extracting("code")
								 .containsExactlyInAnyOrder(INBOUND_PRODUCT);
	}

	@Test
	public void testFindAllIntegrationObjects_EmptyResponse()
	{
		final Set<IntegrationObjectModel> result = integrationObjectService.findAllIntegrationObjects(PRODUCT);

		assertThat(result).isEmpty();
	}

	@Test
	public void testFindAllIntegrationObjects_IllegalArgument()
	{
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> integrationObjectService.findAllIntegrationObjects(null))
				.withMessage("Type code provided cannot be empty or null.");
	}

	@Test
	public void testFindTypeCode()
	{
		final IntegrationObjectItemModel[] products = integrationObjectItemsContext.givenExist(givenItem(PRODUCT, PRODUCT_ALIAS));
		final String contextIntegrationObjectCode = products[0].getIntegrationObject().getCode();

		assertThat(integrationObjectService.findItemTypeCode(contextIntegrationObjectCode, PRODUCT_ALIAS)).isEqualTo(PRODUCT);
	}

	@Test
	public void testFindTypeCode_invalidIntegrationObjectCode()
	{
		integrationObjectItemsContext.givenExist(givenItem(PRODUCT, PRODUCT_ALIAS));

		assertThat(integrationObjectService.findItemTypeCode("NOT_EXISTING", PRODUCT_ALIAS)).isEmpty();
	}

	@Test
	public void testFindTypeCode_invalidIntegrationObjectItemCode()
	{
		final IntegrationObjectItemModel[] products = integrationObjectItemsContext.givenExist(givenItem(PRODUCT));
		final String contextIntegrationObjectCode = products[0].getIntegrationObject().getCode();

		assertThat(integrationObjectService.findItemTypeCode(contextIntegrationObjectCode, PRODUCT_ALIAS)).isEmpty();
	}

	@Test
	public void findAttributeDescriptor()
	{
		final IntegrationObjectItemModel productIO = createBasicIntegrationObject();
		final String ioCode = productIO.getIntegrationObject().getCode();

		final AttributeDescriptorModel attributeDescriptor = integrationObjectService.findAttributeDescriptor(ioCode, PRODUCT_ALIAS, ATTRIBUTE_ALIAS_NAME);
		assertThat(attributeDescriptor).isNotNull();
		assertThat(attributeDescriptor.getQualifier()).isEqualTo(ATTRIBUTE_NAME);
	}

	@Test
	public void findAttributeDescriptor_invalidIntegrationObjectCode()
	{
		createBasicIntegrationObject();

		final String expectedMessage = String.format("Property [%s] is required for EntityType [%s] in IntegrationObject [%s].",
				ATTRIBUTE_ALIAS_NAME, PRODUCT_ALIAS, "NOT_EXISTING");
		assertThatThrownBy(()->integrationObjectService.findAttributeDescriptor("NOT_EXISTING", PRODUCT_ALIAS, ATTRIBUTE_ALIAS_NAME))
				.isInstanceOf(AttributeDescriptorNotFoundException.class)
				.hasMessage(expectedMessage);
	}

	@Test
	public void findAttributeDescriptor_invalidIntegrationObjectItemCode()
	{
		final IntegrationObjectItemModel productIO = createBasicIntegrationObject();
		final String ioCode = productIO.getIntegrationObject().getCode();

		final String expectedMessage = String.format("Property [%s] is required for EntityType [%s] in IntegrationObject [%s].",
				ATTRIBUTE_ALIAS_NAME, "NOT_EXISTING", ioCode);
		assertThatThrownBy(()->integrationObjectService.findAttributeDescriptor(ioCode, "NOT_EXISTING", ATTRIBUTE_ALIAS_NAME))
				.isInstanceOf(AttributeDescriptorNotFoundException.class)
				.hasMessage(expectedMessage);
	}

	@Test
	public void findAttributeDescriptor_invalidAttributeName()
	{
		final IntegrationObjectItemModel productIO = createBasicIntegrationObject();
		final String ioCode = productIO.getIntegrationObject().getCode();

		final String expectedMessage = String.format("Property [%s] is required for EntityType [%s] in IntegrationObject [%s].",
				"NOT_EXISTING", PRODUCT_ALIAS, ioCode);
		assertThatThrownBy(()->integrationObjectService.findAttributeDescriptor(ioCode, PRODUCT_ALIAS, "NOT_EXISTING"))
				.isInstanceOf(AttributeDescriptorNotFoundException.class)
				.hasMessage(expectedMessage);
	}

	@Test
	public void testFindAttributeNameWhenAttributeDescriptorExists()
	{
		final IntegrationObjectItemModel productIO = createBasicIntegrationObject();
		final String ioCode = productIO.getIntegrationObject().getCode();

		final String attributeName = integrationObjectService.findItemAttributeName(ioCode, PRODUCT_ALIAS, ATTRIBUTE_ALIAS_NAME);
		assertThat(attributeName)
				.isNotNull()
				.isEqualTo(ATTRIBUTE_NAME);
	}

	@Test
	public void testFindAttributeNameWhenInvalidIntegrationObjectCode()
	{
		assertThatThrownBy(() -> integrationObjectService.findItemAttributeName("NonExisting", PRODUCT_ALIAS, ATTRIBUTE_ALIAS_NAME))
				.isInstanceOf(AttributeDescriptorNotFoundException.class)
				.hasMessageContaining("NonExisting");
	}

	@Test
	public void testFindAttributeNameWhenInvalidItemCode()
	{
		final IntegrationObjectItemModel productIO = createBasicIntegrationObject();
		final String ioCode = productIO.getIntegrationObject().getCode();

		assertThatThrownBy(() -> integrationObjectService.findItemAttributeName(ioCode, "NonExisting", ATTRIBUTE_ALIAS_NAME))
				.isInstanceOf(AttributeDescriptorNotFoundException.class)
				.hasMessageContaining("NonExisting");
	}

	@Test
	public void testFinaAttributeNameWhenInvalidAttributeName()
	{
		final IntegrationObjectItemModel productIO = createBasicIntegrationObject();
		final String ioCode = productIO.getIntegrationObject().getCode();

		assertThatThrownBy(() -> integrationObjectService.findItemAttributeName(ioCode, PRODUCT_ALIAS, "NonExisting"))
				.isInstanceOf(AttributeDescriptorNotFoundException.class)
				.hasMessageContaining("NonExisting");
	}

	private IntegrationObjectItemModel createBasicIntegrationObject()
	{
		return integrationObjectItemsContext.givenExist(
				givenItem(PRODUCT, PRODUCT_ALIAS)
						.withAttribute(givenAttribute(ATTRIBUTE_ALIAS_NAME, ATTRIBUTE_NAME)))[0];
	}

	@Test
	public void findIntegrationObject()
	{
		final IntegrationObjectModel product = givenIntegrationObject(PRODUCT).build();
		integrationObjectsContext.givenExist(product);
		final String integrationObjectCode = product.getCode();

		final IntegrationObjectModel integrationObject = integrationObjectService.findIntegrationObject(integrationObjectCode);
		assertThat(integrationObject).hasFieldOrPropertyWithValue("code", integrationObjectCode);
	}

	@Test
	public void findIntegrationObjectNotFoundException()
	{
		assertThatThrownBy(()-> integrationObjectService.findIntegrationObject("invalid"))
				.isInstanceOf(ModelNotFoundException.class)
				.hasMessageContaining("invalid");
	}

	@Test
	public void findIntegrationObjectItemByType_notFoundException()
	{
		assertThatThrownBy(()-> integrationObjectService.findIntegrationObjectItemByTypeCode("someIOCode", "invalid"))
				.isInstanceOf(ModelNotFoundException.class)
				.hasMessage("The Integration Object Definition of 'someIOCode' was not found");
	}

	@Test
	public void findIntegrationObjectItemByType_ambiguousIdentifierException() throws ImpExException
	{
		IntegrationTestUtil.importCsv("INSERT_UPDATE IntegrationObject; code[unique = true];\n" +
				"; AmbiguousMatch\n" +
				"\n" +
				"INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)\n" +
				"; AmbiguousMatch ; Product      ; Product\n" +
				"; AmbiguousMatch ; OtherProduct ; Product\n");

		assertThatThrownBy(() -> integrationObjectService.findIntegrationObjectItemByTypeCode("AmbiguousMatch", "Product"))
				.isInstanceOf(AmbiguousIdentifierException.class)
				.hasMessage("The Integration Object and the ItemModel class provided have more than one match, "
						+ "please adjust the Integration Object definition of 'AmbiguousMatch'");
	}

	@Test
	public void findIntegrationObjectItemByType()
	{
		final IntegrationObjectItemModel itemModel = givenItem(PRODUCT).build();
		integrationObjectsContext.givenExist(
				givenIntegrationObject(OUTBOUND_PRODUCT).addIntegrationObjectItem(itemModel).build());

		final IntegrationObjectItemModel model = integrationObjectService.findIntegrationObjectItemByTypeCode(OUTBOUND_PRODUCT, PRODUCT);

		assertThat(model).isNotNull();
		assertThat(model).hasFieldOrPropertyWithValue(CODE_FIELD, PRODUCT);
		assertThat(model.getIntegrationObject()).isNotNull().hasFieldOrPropertyWithValue(CODE_FIELD, OUTBOUND_PRODUCT);
	}

	private IntegrationObjectItemBuilder givenItem(final String entityType)
	{
		return IntegrationObjectItemBuilder.item().forType(entityType);
	}

	private IntegrationObjectItemBuilder givenItem(final String entityType, final String integrationObjectItemCode)
	{
		return IntegrationObjectItemBuilder.item().forType(entityType).withCode(integrationObjectItemCode);
	}

	private IntegrationObjectItemAttributeBuilder givenAttribute(final ComposedTypeModel attrType)
	{
		return IntegrationObjectItemAttributeBuilder.attribute().forObjectOfType(attrType);
	}

	private IntegrationObjectItemAttributeBuilder givenAttribute(final String name, final String descriptorName)
	{
		return IntegrationObjectItemAttributeBuilder.attribute().named(name).withDescriptorName(descriptorName);
	}

	private IntegrationObjectBuilder givenIntegrationObject(final String codeName)
	{
		return IntegrationObjectBuilder.integrationObject().withCode(codeName);
	}
}

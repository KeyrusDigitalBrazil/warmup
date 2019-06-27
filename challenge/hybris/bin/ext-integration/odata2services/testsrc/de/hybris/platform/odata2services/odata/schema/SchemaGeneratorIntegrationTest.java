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
package de.hybris.platform.odata2services.odata.schema;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.util.IntegrationObjectItemsContext;
import de.hybris.platform.integrationservices.util.IntegrationObjectsContext;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.odata2services.odata.ODataEntityContainer;
import de.hybris.platform.odata2services.odata.ODataEntityType;
import de.hybris.platform.odata2services.odata.ODataSchema;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.olingo.odata2.api.edm.FullQualifiedName;
import org.apache.olingo.odata2.api.edm.provider.Association;
import org.apache.olingo.odata2.api.edm.provider.Schema;
import org.junit.Rule;
import org.junit.Test;

@IntegrationTest
public class SchemaGeneratorIntegrationTest extends ServicelayerTest
{
	private static final String SCHEMA_NAME = "HybrisCommerceOData";
	private static final String PART_OF = "s:IsPartOf";
	private static final String IS_UNIQUE = "s:IsUnique";

	@Resource(name = "oDataSchemaGenerator")
	private SchemaGenerator generator;

	@Rule
	public IntegrationObjectItemsContext definitionsContext = IntegrationObjectItemsContext.create();
	@Rule
	public IntegrationObjectsContext integrationObjectsContext = IntegrationObjectsContext.create();

	@Test
	public void testGenerateWhenNoIntegrationObjectItemsProvided()
	{
		final Collection<IntegrationObjectItemModel> noObjectDefinitions = Collections.emptyList();

		final Schema schema = generator.generateSchema(noObjectDefinitions);

		assertThat(schema.getNamespace()).isEqualTo(SCHEMA_NAME);
		assertThat(new ODataSchema(schema).isEmpty()).isTrue();
	}

	@Test
	public void testGeneratePerformsEntityTypeGeneration() throws ImpExException
	{
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE IntegrationObject; code[unique = true];",
				"; MyProduct",

				"INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)",
				"; MyProduct	; Unit		; Unit",

				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]",
				"; MyProduct:Unit		; code		; Unit:code				;						");

		final Schema generated = generator.generateSchema(getIntegrationObjectItemModelDefinitions());

		final ODataSchema schema = new ODataSchema(generated);
		assertThat(schema.getEntityTypeNames()).containsExactly("Unit");
		assertThat(schema.getDefaultEntityContainer().getEntitySetTypes()).containsExactly("Unit");
	}

	@Test
	public void testGeneratesIntegrationKeyForEntityType() throws ImpExException
	{
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE IntegrationObject; code[unique = true];",
				"; MyProduct",

				"INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)",
				"; MyProduct	; Unit		; Unit",

				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]",
				"; MyProduct:Unit		; code		; Unit:code				;						");

		final Schema generated = generator.generateSchema(getIntegrationObjectItemModelDefinitions());

		final ODataSchema schema = new ODataSchema(generated);
		assertThat(schema.getEntityType("Unit").getKeyProperties()).containsExactly("integrationKey");
		assertThat(schema.getEntityType("Unit").getPropertyNames()).contains("integrationKey");
	}

	@Test
	public void testGeneratePerformsEntityPropertyGeneration() throws ImpExException
	{
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE IntegrationObject; code[unique = true];",
				"; MyProduct",

				"INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)",
				"; MyProduct	; Unit		; Unit",

				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]",
				"; MyProduct:Unit		; code		; Unit:code				;						",
				"; MyProduct:Unit   	; name 		; Unit:name				;						");

		final Schema generated = generator.generateSchema(getIntegrationObjectItemModelDefinitions());

		final ODataEntityType unit = new ODataSchema(generated).getEntityType("Unit");
		assertThat(unit.getPropertyNames()).contains("code", "name");
	}

	@Test
	public void testGeneratePerformsAssociationGeneration() throws ImpExException
	{
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE IntegrationObject; code[unique = true];",
				"; MyProduct",

				"INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)",
				"; MyProduct	; Unit		; Unit",
				"; MyProduct	; Product 	; Product",
				"; MyProduct	; Address 	; Address",
				"; MyProduct	; Company 	; Company",

				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]",
				"; MyProduct:Unit		; code		; Unit:code				;						",
				"; MyProduct:Unit   	; name 		; Unit:name				;						",

				"; MyProduct:Product   	; code 		; Product:code			;						",
				"; MyProduct:Product   	; unit 		; Product:unit			; 	MyProduct:Unit		",

				"; MyProduct:Address   	; publicKey ; Address:publicKey		;						; true",

				"; MyProduct:Company   	; uid 		; Company:uid			;						",
				"; MyProduct:Company   	; addresses ; Company:addresses		; 	MyProduct:Address	");


		final Schema generated = generator.generateSchema(getIntegrationObjectItemModelDefinitions());

		final ODataSchema schema = new ODataSchema(generated);
		assertThat(schema.containsAssociationBetween("Product", "Unit")).isTrue();
		assertThat(schema.containsAssociationBetween("Company", "Address")).isTrue();

		assertThat(schema.getEntityType("Product").getNavigationPropertyNames()).containsExactly("unit");
		assertThat(schema.getEntityType("Company").getNavigationPropertyNames()).containsExactly("addresses");

		final ODataEntityContainer container = schema.getDefaultEntityContainer();
		assertThat(container.containsAssociationSetBetween("Product", "Unit")).isTrue();
		assertThat(container.containsAssociationSetBetween("Company", "Address")).isTrue();
	}

	@Test
	public void testGeneratePerformsAnnotationAttributeGeneration_NullableAndUnique() throws ImpExException
	{
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE IntegrationObject; code[unique = true];",
				"; MyProduct",

				"INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)",
				"; MyProduct	; Address 	; Address",
				"; MyProduct	; Company 	; Company",

				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]",
				"; MyProduct:Address   	; publicKey ; Address:publicKey		;						",

				"; MyProduct:Company   	; uid 		; Company:uid			;						",
				"; MyProduct:Company   	; addresses ; Company:addresses		; 	MyProduct:Address	");

		final Schema generated = generator.generateSchema(getIntegrationObjectItemModelDefinitions());

		final ODataSchema schema = new ODataSchema(generated);

		final ODataEntityType companySchema = schema.getEntityType("Company");
		assertThat(companySchema.getAnnotatableProperty("uid").getAnnotationNames()).contains(IS_UNIQUE, "Nullable");
		assertThat(companySchema.getAnnotatableProperty("uid").getAnnotation(IS_UNIQUE).orElse(null))
				.isNotNull()
				.hasFieldOrPropertyWithValue("text", "true");
		assertThat(companySchema.getAnnotatableProperty("uid").getAnnotation("Nullable").orElse(null))
				.isNotNull()
				.hasFieldOrPropertyWithValue("text", "false");
	}

	@Test
	public void testGenerateContainsCollectionOfPrimitiveEntityTypes() throws ImpExException
	{
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE IntegrationObject; code[unique = true];",
				"; PrimitiveTypesIntegrationObject",

				"INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)",
				"; PrimitiveTypesIntegrationObject ; Order      ; Order",
				"; PrimitiveTypesIntegrationObject ; OrderEntry ; OrderEntry",

				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]",
				"; PrimitiveTypesIntegrationObject:Order      	; code              ; Order:code                   	;",

				"; PrimitiveTypesIntegrationObject:OrderEntry 	; order             ; OrderEntry:order             	; PrimitiveTypesIntegrationObject:Order",
				"; PrimitiveTypesIntegrationObject:OrderEntry	; entryGroupNumbers	; OrderEntry:entryGroupNumbers	;");

		final Schema generated = generator.generateSchema(getIntegrationObjectItemModelDefinitions());

		final ODataSchema schema = new ODataSchema(generated);
		assertThat(schema.getEntityTypeNames()).containsExactlyInAnyOrder("Order", "OrderEntry", "Integer");
		assertThat(schema.containsAssociationBetween("OrderEntry", "Integer")).isTrue();
		assertThat(schema.getAssociations().stream().map(Association::getName).collect(Collectors.toList()))
				.contains("FK_OrderEntry_entryGroupNumbers");
		assertThat(schema.getDefaultEntityContainer().getEntitySetTypes()).contains("Integer");
		assertThat(schema.getDefaultEntityContainer().getAssociationSetNames()).contains("OrderEntry_Integers");
	}

	@Test
	public void testGenerateWithEnumTypeAttribute() throws ImpExException
	{
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE IntegrationObject; code[unique = true];",
				"; MyProduct",

				"INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)",
				"; MyProduct ; Product     ; Product",
				"; MyProduct ; ArticleApprovalStatus; ArticleApprovalStatus",

				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]",
				"; MyProduct:Product     	; code              ; Product:code                   	 ;",

				"; MyProduct:Product 		; approvalStatus    ; Product:approvalStatus             ; MyProduct:ArticleApprovalStatus",
				"; MyProduct:ArticleApprovalStatus	; code	; ArticleApprovalStatus:code			 ;");

		final Schema generated = generator.generateSchema(getIntegrationObjectItemModelDefinitions());

		final ODataSchema schema = new ODataSchema(generated);
		assertThat(schema.getEntityTypeNames()).containsExactlyInAnyOrder("Product", "ArticleApprovalStatus");
		assertThat(schema.containsAssociationBetween("Product", "ArticleApprovalStatus")).isTrue();
		assertThat(schema.getAssociations().stream().map(Association::getName).collect(Collectors.toList()))
				.contains("FK_Product_approvalStatus");
		assertThat(schema.getDefaultEntityContainer().getEntitySetTypes()).contains("ArticleApprovalStatus");
		assertThat(schema.getDefaultEntityContainer().getEntitySetTypes()).contains("Product");
		assertThat(schema.getDefaultEntityContainer().getAssociationSetNames()).contains("Product_ArticleApprovalStatuses");
	}

	@Test
	public void testGeneratesPartOfAnnotationWhenPartOfIsSetInTheTypeSystem() throws ImpExException
	{
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE IntegrationObject; code[unique = true];",
				"; MyProduct",

				"INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)",
				"; MyProduct	; CatalogVersion		; CatalogVersion",
				"; MyProduct	; Category				; Category",

				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]; autoCreate[default = false]",
				"; MyProduct:CatalogVersion ; version        ; CatalogVersion:version        ;",
				"; MyProduct:CatalogVersion ; active         ; CatalogVersion:active         ;",
				"; MyProduct:CatalogVersion ; rootCategories ; CatalogVersion:rootCategories ; MyProduct:Category ; ; false",
				"; MyProduct:Category       ; code           ; Category:code                 ;");

		final Schema generated = generator.generateSchema(getIntegrationObjectItemModelDefinitions());

		final ODataSchema schema = new ODataSchema(generated);
		assertThat(schema.getEntityTypeNames()).containsExactlyInAnyOrder("CatalogVersion", "Category");

		final ODataEntityType catalogVersionSchema = schema.getEntityType("CatalogVersion");
		assertThat(catalogVersionSchema.getAnnotatableProperty("rootCategories").getAnnotation(PART_OF).orElse(null))
				.isNotNull()
				.hasFieldOrPropertyWithValue("text", "true");
	}

	@Test
	public void testDoesNotGeneratePartOfAnnotationWhenAutoCreateIsTrueButPartOfIsFalse() throws ImpExException
	{
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE IntegrationObject; code[unique = true];",
				"; MyProduct",

				"INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)",
				"; MyProduct ; Address ; Address",
				"; MyProduct ; Country ; Country ",

				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]; autoCreate[default = false]",
				"; MyProduct:Address ; publicKey ; Address:publicKey ;                   ; true",
				"; MyProduct:Address ; country   ; Address:country   ; MyProduct:Country ;      ; true",
				"; MyProduct:Country ; isocode   ; Country:isocode   ;");

		final Schema generated = generator.generateSchema(getIntegrationObjectItemModelDefinitions());

		final ODataEntityType addressType = new ODataSchema(generated).getEntityType("Address");
		assertThat(addressType.getAnnotatableProperty("country").getAnnotation(PART_OF)).isEmpty();
	}

	@Test
	public void testGeneratesAssociationFromTypeToSameType() throws ImpExException
	{
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE IntegrationObject; code[unique = true];",
				"; MyCategory",

				"INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)",
				"; MyCategory	; Category  ; Category",                                

				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]",
				"; MyCategory:Category	; code		        ; Category:code	    	    ;						",
				"; MyCategory:Category 	; supercategories 	; Category:supercategories	; MyCategory:Category   ");


		final Schema generated = generator.generateSchema(getIntegrationObjectItemModelDefinitions());

		final ODataSchema oDataSchema = new ODataSchema(generated);
		assertThat(generated.getEntityTypes().get(0).getNavigationProperties().get(0).getToRole()).isEqualTo("Supercategories");

		final Association association = oDataSchema.getAssociation("FK_Category_supercategories");
		assertThat(association.getEnd1())
				.hasFieldOrPropertyWithValue("type", new FullQualifiedName(SCHEMA_NAME, "Category"))
				.hasFieldOrPropertyWithValue("role", "Category");

		assertThat(association.getEnd2())
				.hasFieldOrPropertyWithValue("type", new FullQualifiedName(SCHEMA_NAME, "Category"))
				.hasFieldOrPropertyWithValue("role", "Supercategories");
	}

	private Collection<IntegrationObjectItemModel> getIntegrationObjectItemModelDefinitions()
	{
		//noinspection unchecked
		return CollectionUtils.transformedCollection(
				IntegrationTestUtil.findAll(IntegrationObjectItemModel.class), o -> o);
	}
}

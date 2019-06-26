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

package de.hybris.platform.odata2services.odata.schema

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.odata2services.TestConstants
import de.hybris.platform.odata2services.odata.ODataSchema
import de.hybris.platform.servicelayer.ServicelayerTransactionalSpockSpecification
import org.junit.Test

import javax.annotation.Resource

@IntegrationTest
class SchemaGenerationLocalizationIntegrationTest extends ServicelayerTransactionalSpockSpecification {

	@Resource(name = "oDataSchemaGenerator")
	private SchemaGenerator generator

	@Test
	def "Schema for item with localized fields contains localized entity type"() {
		given:
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE IntegrationObject; code[unique = true];",
				"; LocalizedIntegrationObject",

				"INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)",
				"; LocalizedIntegrationObject ; Unit      ; Unit",

				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]",
				"; LocalizedIntegrationObject:Unit		; code		; Unit:code				;",
				"; LocalizedIntegrationObject:Unit   	; name 		; Unit:name				;")

		when:
		def schema = new ODataSchema(generator.generateSchema(getIntegrationObjectItemModelDefinitions()))

		then:
		with(schema)
		{
			getEntityTypeNames().containsAll("Unit", TestConstants.LOCALIZED_ENTITY_PREFIX + "Unit")
			getEntityType("${TestConstants.LOCALIZED_ENTITY_PREFIX}Unit").getPropertyNames().containsAll("name")
			!getEntityType( "${TestConstants.LOCALIZED_ENTITY_PREFIX}Unit").getPropertyNames().contains("code")
			getEntityType("Unit").getNavigationPropertyNames().contains("localizedAttributes")
			containsAssociationBetween("Unit", "${TestConstants.LOCALIZED_ENTITY_PREFIX}Unit")
			getDefaultEntityContainer().containsAssociationSetBetween("Unit", "${TestConstants.LOCALIZED_ENTITY_PREFIX}Unit")
		}
	}

	@Test
	def "Schema does not contain localized entity type when entities have no localized attributes"() {
		given:
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE IntegrationObject; code[unique = true];",
				"; LocalizedIntegrationObject",

				"INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)",
				"; LocalizedIntegrationObject ; Order     ; Order",

				"INSERT_UPDATE IntegrationObjectItemAttribute; integrationObjectItem(integrationObject(code), code)[unique = true]; attributeName[unique = true]; attributeDescriptor(enclosingType(code), qualifier); returnIntegrationObjectItem(integrationObject(code), code); unique[default = false]",
				"; LocalizedIntegrationObject:Order     ; code      ; Order:code            ;")

		when:
		def schema = new ODataSchema(generator.generateSchema(getIntegrationObjectItemModelDefinitions()))
		schema.getEntityType("${TestConstants.LOCALIZED_ENTITY_PREFIX}Order")

		then:
		thrown IllegalArgumentException
	}

	def getIntegrationObjectItemModelDefinitions() {
		IntegrationTestUtil.findAll(IntegrationObjectItemModel.class)
	}
}

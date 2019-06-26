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
package com.hybris.ymkt.common.odata;

import de.hybris.bootstrap.annotations.UnitTest;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ODataFilterBuilderTest
{
	Edm edm;

	// To be added to ODataService
	protected ODataFilterBuilder filter(final String entitySetName) throws EdmException
	{
		return ODataFilterBuilder.of(edm.getDefaultEntityContainer().getEntitySet(entitySetName), new ODataConvertEdmService());
	}

	@Before
	public void setUp() throws Exception
	{
		try (InputStream metadataXml = ODataFilterBuilderTest.class.getResourceAsStream("/CUAN_INITIATIVE_SRV_1702_$metadata.xml"))
		{
			edm = EntityProvider.readMetadata(metadataXml, false);
		}
	}

	@After
	public void tearDown() throws Exception
	{
		//
	}

	@Test
	public void test_0_Name_EQ() throws Exception
	{
		//<Property Name="Name" Type="Edm.String" Nullable="false" MaxLength="40" sap:label="Name" sap:creatable="false" sap:updatable="false"/>
		Assert.assertEquals("Name eq 'a'", this.filter("Initiatives").on("Name").eq("a").toExpression());
	}

	@Test
	public void test_1_CategoryCategoryType_Numeric_EQ() throws Exception
	{
		//<Property Name="CategoryType" Type="Edm.Int16" Nullable="false" sap:label="Permission Hdlg" sap:creatable="false" sap:updatable="false" sap:sortable="false"/>
		Assert.assertEquals("Category/CategoryType eq 2",
				this.filter("Initiatives").on("Category/CategoryType").eq(2).toExpression());
	}

	@Test
	public void test_1_LifecycleStatusStatusCode_String_EQ() throws Exception
	{
		//<Property Name="StatusCode" Type="Edm.String" Nullable="false" MaxLength="1" sap:label="Status Code"/>
		Assert.assertEquals("LifecycleStatus/StatusCode eq '2'",
				this.filter("Initiatives").on("LifecycleStatus/StatusCode").eq(2).toExpression());
	}

	@Test
	public void test_2_SearchTileFilterCategory_OR() throws Exception
	{
		final List<String> terms = Arrays.asList("1", "2");

		//<Property Name="TileFilterCategory" Type="Edm.String" Nullable="false" MaxLength="2" sap:label="Tile Filter Category" sap:creatable="false" sap:updatable="false" sap:sortable="false" sap:filterable="false"/>
		Assert.assertEquals("(Search/TileFilterCategory eq '1' or Search/TileFilterCategory eq '2')",
				this.filter("Initiatives").on("Search/TileFilterCategory").eq(terms).toExpression());
	}

	@Test
	public void test_3_FilterInteractionContactId_AND_InteractionContactIdOrigin() throws Exception
	{
		final List<String> contacts = Arrays.asList("contact01", "contact02", "contact03");
		final List<String> idOrigins = Arrays.asList("COOKIE_ID", "ANYTHING");

		//<Property Name="InteractionContactIdOrigin" Type="Edm.String" Nullable="false" MaxLength="20" sap:label="Interaction Contact ID Origin" sap:creatable="false" sap:updatable="false" sap:sortable="false"/>
		//<Property Name="InteractionContactId" Type="Edm.String" Nullable="false" sap:label="Interaction Contact Id" sap:creatable="false" sap:updatable="false" sap:sortable="false"/>
		Assert.assertEquals(
				"(Filter/InteractionContactId eq 'contact01' or Filter/InteractionContactId eq 'contact02' or Filter/InteractionContactId eq 'contact03')"
						+ " and (Filter/InteractionContactIdOrigin eq 'COOKIE_ID' or Filter/InteractionContactIdOrigin eq 'ANYTHING')",
				this.filter("Initiatives").on("Filter/InteractionContactId").eq(contacts).and("Filter/InteractionContactIdOrigin")
						.eq(idOrigins).toExpression());
	}

	@Test
	public void test_4_empty_collection() throws Exception
	{
		//<Property Name="Name" Type="Edm.String" Nullable="false" MaxLength="40" sap:label="Name" sap:creatable="false" sap:updatable="false"/>
		//<Property Name="StatusCode" Type="Edm.String" Nullable="false" MaxLength="1" sap:label="Status Code"/>
		Assert.assertEquals("Name eq 'a' and (LifecycleStatus/StatusCode eq '2' or LifecycleStatus/StatusCode eq '3')", this
				.filter("Initiatives").on("Name").eq("a").and("LifecycleStatus/StatusCode").eq(Arrays.asList(2, 3)).toExpression());

		Assert.assertEquals("Name eq 'a'",
				this.filter("Initiatives").on("Name").eq("a").and("LifecycleStatus/StatusCode").eq(Arrays.asList()).toExpression());
		Assert.assertEquals("Name eq 'a'",
				this.filter("Initiatives").on("Name").eq("a").and("LifecycleStatus/StatusCode").eq(null).toExpression());
		Assert.assertEquals("Name eq 'a'",
				this.filter("Initiatives").on("Name").eq("a").and("LifecycleStatus/StatusCode").eq().toExpression());

		Assert.assertEquals("", this.filter("Initiatives").on("Name").eq(null).toExpression());
	}

	@Test
	public void test_5_array_collection() throws Exception
	{
		//<Property Name="Name" Type="Edm.String" Nullable="false" MaxLength="40" sap:label="Name" sap:creatable="false" sap:updatable="false"/>
		//<Property Name="StatusCode" Type="Edm.String" Nullable="false" MaxLength="1" sap:label="Status Code"/>
		Assert.assertEquals("Name eq 'a' and (LifecycleStatus/StatusCode eq '2' or LifecycleStatus/StatusCode eq '3')",
				this.filter("Initiatives").on("Name").eq("a").and("LifecycleStatus/StatusCode").eq(2, 3).toExpression());
	}

}

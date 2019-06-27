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
package de.hybris.platform.odata2services.odata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.junit.Test;

@UnitTest
public class EdmxProviderValidatorUnitTest
{
	private static final String CONTENT_1 = "content1";

	private final EdmxProviderValidator validator = new EdmxProviderValidator();

	@Test
	public void testValidateResponseResponseValid() throws ODataException, IOException
	{
		final ODataResponse response = createResponse(CONTENT_1, validEdmx());
		validator.validateResponse(response);
		assertThat(response)
				.isNotNull()
				.hasFieldOrPropertyWithValue("contentHeader", CONTENT_1);
		assertThat(entityToString(response.getEntityAsStream())).isEqualTo(validEdmx());
	}

	@Test
	public void testValidateResponseResponseInvalid()
	{
		assertThatThrownBy(
				() -> validator.validateResponse(createResponse(CONTENT_1, invalidEdmx())))
				.isInstanceOf(InvalidODataSchemaException.class);
	}

	@Test
	public void testValidateNullResponse()
	{
		assertThatThrownBy(() -> validator.validateResponse(null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldThrowInvalidODataSchemaException_whenIOExceptionOccurs() throws ODataException
	{
		final ODataResponse response = mock(ODataResponse.class);
		when(response.getEntityAsStream()).thenThrow(IOException.class);
		assertThatThrownBy(() -> validator.validateResponse(response))
				.isInstanceOf(InvalidODataSchemaException.class);
	}

	@Test
	public void testValidateThrowsIOExceptionWhenClosingStream() throws IOException, ODataException
	{
		final InputStream inputStream = mock(InputStream.class);
		doThrow(new IOException()).when(inputStream).close();
		final ODataResponse response = givenODataResponse();
		when(response.getEntityAsStream()).thenReturn(inputStream);

		assertThatThrownBy(() -> validator.validateResponse(response))
				.isInstanceOf(InvalidODataSchemaException.class)
				.hasFieldOrPropertyWithValue("errorCode", "schema_generation_error")
				.hasCauseInstanceOf(IOException.class);
	}


	private ODataResponse createResponse(final String contentHeader, final String entityString)
	{
		return ODataResponse.newBuilder()
				.contentHeader(contentHeader)
				.entity(entityString)
				.build();
	}

	private static ODataResponse givenODataResponse()
	{
		final ODataResponse oDataResponse = mock(ODataResponse.class);
		final InputStream inputStream = new ByteArrayInputStream("responseValue".getBytes());
		when(oDataResponse.getEntity()).thenReturn(inputStream);
		return oDataResponse;
	}

	private String validEdmx()
	{
		return "<?xml version='1.0' encoding='UTF-8'?>\n" +
				"<edmx:Edmx xmlns:edmx=\"http://schemas.microsoft.com/ado/2007/06/edmx\" Version=\"1.0\">\n" +
				"    <edmx:DataServices m:DataServiceVersion=\"1.0\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\">\n" +
				"        <Schema Namespace=\"HybrisCommerceOData\" xmlns=\"http://schemas.microsoft.com/ado/2008/09/edm\" xmlns:s=\"http://schemas.sap.com/commerce\" s:schema-version=\"1\">\n" +
				"            <EntityType Name=\"Catalog\">\n" +
				"                <Key>\n" +
				"                    <PropertyRef Name=\"id\"/>\n" +
				"                </Key>\n" +
				"                <Property Name=\"id\" Type=\"Edm.String\" s:IsUnique=\"true\" Nullable=\"false\"/>\n" +
				"            </EntityType>\n" +
				"            <EntityType Name=\"VariantType\">\n" +
				"                <Key>\n" +
				"                    <PropertyRef Name=\"code\"/>\n" +
				"                </Key>\n" +
				"                <Property Name=\"code\" Type=\"Edm.String\" s:IsUnique=\"true\" Nullable=\"false\"/>\n" +
				"            </EntityType>\n" +
				"            <EntityType Name=\"CatalogVersion\">\n" +
				"                <Key>\n" +
				"                    <PropertyRef Name=\"version\"/>\n" +
				"                </Key>\n" +
				"                <Property Name=\"version\" Type=\"Edm.String\" s:IsUnique=\"true\" Nullable=\"false\"/>\n" +
				"                <Property Name=\"active\" Type=\"Edm.Boolean\" Nullable=\"true\"/>\n" +
				"                <NavigationProperty Name=\"catalog\" Relationship=\"HybrisCommerceOData.FK_CatalogVersion_Catalog\" FromRole=\"CatalogVersion\" ToRole=\"Catalog\" s:IsUnique=\"true\" Nullable=\"false\"/>\n" +
				"            </EntityType>\n" +
				"            <EntityType Name=\"Unit\">\n" +
				"                <Key>\n" +
				"                    <PropertyRef Name=\"code\"/>\n" +
				"                </Key>\n" +
				"                <Property Name=\"unitType\" Type=\"Edm.String\" Nullable=\"false\"/>\n" +
				"                <Property Name=\"code\" Type=\"Edm.String\" s:IsUnique=\"true\" Nullable=\"false\"/>\n" +
				"                <Property Name=\"name\" Type=\"Edm.String\" s:IsLanguageDependent=\"true\" Nullable=\"true\"/>\n" +
				"            </EntityType>\n" +
				"            <EntityType Name=\"ProductDiscountGroup\">\n" +
				"                <Key>\n" +
				"                    <PropertyRef Name=\"code\"/>\n" +
				"                </Key>\n" +
				"                <Property Name=\"code\" Type=\"Edm.String\" s:IsUnique=\"true\" Nullable=\"false\"/>\n" +
				"            </EntityType>\n" +
				"            <EntityType Name=\"Vendor\">\n" +
				"                <Key>\n" +
				"                    <PropertyRef Name=\"code\"/>\n" +
				"                </Key>\n" +
				"                <Property Name=\"code\" Type=\"Edm.String\" s:IsUnique=\"true\" Nullable=\"false\"/>\n" +
				"            </EntityType>\n" +
				"            <EntityType Name=\"ProductPriceGroup\">\n" +
				"                <Key>\n" +
				"                    <PropertyRef Name=\"code\"/>\n" +
				"                </Key>\n" +
				"                <Property Name=\"code\" Type=\"Edm.String\" s:IsUnique=\"true\" Nullable=\"false\"/>\n" +
				"            </EntityType>\n" +
				"            <EntityType Name=\"Product\">\n" +
				"                <Key>\n" +
				"                    <PropertyRef Name=\"code\"/>\n" +
				"                </Key>\n" +
				"                <Property Name=\"code\" Type=\"Edm.String\" s:IsUnique=\"true\" Nullable=\"false\"/>\n" +
				"                <Property Name=\"name\" Type=\"Edm.String\" s:IsLanguageDependent=\"true\" Nullable=\"true\"/>\n" +
				"                <Property Name=\"sapBlocked\" Type=\"Edm.Boolean\" Nullable=\"true\"/>\n" +
				"                <Property Name=\"sapBaseUnitConversion\" Type=\"Edm.Double\" Nullable=\"true\"/>\n" +
				"                <Property Name=\"sapConfigurable\" Type=\"Edm.Boolean\" Nullable=\"true\"/>\n" +
				"                <Property Name=\"sapBlockedDate\" Type=\"Edm.DateTime\" Nullable=\"true\"/>\n" +
				"                <Property Name=\"sapEAN\" Type=\"Edm.String\" Nullable=\"true\"/>\n" +
				"                <NavigationProperty Name=\"catalogVersion\" Relationship=\"HybrisCommerceOData.FK_Product_CatalogVersion\" FromRole=\"Product\" ToRole=\"CatalogVersion\" s:IsUnique=\"true\" Nullable=\"false\"/>\n" +
				"                <NavigationProperty Name=\"unit\" Relationship=\"HybrisCommerceOData.FK_Product_Unit\" FromRole=\"Product\" ToRole=\"Unit\" Nullable=\"true\"/>\n" +
				"                <NavigationProperty Name=\"Europe1PriceFactory_PTG\" Relationship=\"HybrisCommerceOData.FK_Product_ProductTaxGroup\" FromRole=\"Product\" ToRole=\"ProductTaxGroup\" Nullable=\"true\"/>\n" +
				"                <NavigationProperty Name=\"Europe1PriceFactory_PPG\" Relationship=\"HybrisCommerceOData.FK_Product_ProductPriceGroup\" FromRole=\"Product\" ToRole=\"ProductPriceGroup\" Nullable=\"true\"/>\n" +
				"                <NavigationProperty Name=\"Europe1PriceFactory_PDG\" Relationship=\"HybrisCommerceOData.FK_Product_ProductDiscountGroup\" FromRole=\"Product\" ToRole=\"ProductDiscountGroup\" Nullable=\"true\"/>\n" +
				"                <NavigationProperty Name=\"variantType\" Relationship=\"HybrisCommerceOData.FK_Product_VariantType\" FromRole=\"Product\" ToRole=\"VariantType\" Nullable=\"true\"/>\n" +
				"                <NavigationProperty Name=\"sapPlant\" Relationship=\"HybrisCommerceOData.FK_Product_Warehouse\" FromRole=\"Product\" ToRole=\"Warehouse\" Nullable=\"true\"/>\n" +
				"            </EntityType>\n" +
				"            <EntityType Name=\"ProductTaxGroup\">\n" +
				"                <Key>\n" +
				"                    <PropertyRef Name=\"code\"/>\n" +
				"                </Key>\n" +
				"                <Property Name=\"code\" Type=\"Edm.String\" s:IsUnique=\"true\" Nullable=\"false\"/>\n" +
				"            </EntityType>\n" +
				"            <EntityType Name=\"Warehouse\">\n" +
				"                <Key>\n" +
				"                    <PropertyRef Name=\"code\"/>\n" +
				"                </Key>\n" +
				"                <Property Name=\"code\" Type=\"Edm.String\" s:IsUnique=\"true\" Nullable=\"false\"/>\n" +
				"                <Property Name=\"name\" Type=\"Edm.String\" s:IsLanguageDependent=\"true\" Nullable=\"true\"/>\n" +
				"                <NavigationProperty Name=\"vendor\" Relationship=\"HybrisCommerceOData.FK_Warehouse_Vendor\" FromRole=\"Warehouse\" ToRole=\"Vendor\" Nullable=\"false\"/>\n" +
				"            </EntityType>\n" +
				"            <Association Name=\"FK_CatalogVersion_Catalog\">\n" +
				"                <End Type=\"HybrisCommerceOData.CatalogVersion\" Multiplicity=\"0..1\" Role=\"CatalogVersion\"/>\n" +
				"                <End Type=\"HybrisCommerceOData.Catalog\" Multiplicity=\"0..1\" Role=\"Catalog\"/>\n" +
				"            </Association>\n" +
				"            <Association Name=\"FK_Product_CatalogVersion\">\n" +
				"                <End Type=\"HybrisCommerceOData.Product\" Multiplicity=\"0..1\" Role=\"Product\"/>\n" +
				"                <End Type=\"HybrisCommerceOData.CatalogVersion\" Multiplicity=\"0..1\" Role=\"CatalogVersion\"/>\n" +
				"            </Association>\n" +
				"            <Association Name=\"FK_Product_Unit\">\n" +
				"                <End Type=\"HybrisCommerceOData.Product\" Multiplicity=\"0..1\" Role=\"Product\"/>\n" +
				"                <End Type=\"HybrisCommerceOData.Unit\" Multiplicity=\"0..1\" Role=\"Unit\"/>\n" +
				"            </Association>\n" +
				"            <Association Name=\"FK_Product_ProductTaxGroup\">\n" +
				"                <End Type=\"HybrisCommerceOData.Product\" Multiplicity=\"0..1\" Role=\"Product\"/>\n" +
				"                <End Type=\"HybrisCommerceOData.ProductTaxGroup\" Multiplicity=\"0..1\" Role=\"ProductTaxGroup\"/>\n" +
				"            </Association>\n" +
				"            <Association Name=\"FK_Product_ProductPriceGroup\">\n" +
				"                <End Type=\"HybrisCommerceOData.Product\" Multiplicity=\"0..1\" Role=\"Product\"/>\n" +
				"                <End Type=\"HybrisCommerceOData.ProductPriceGroup\" Multiplicity=\"0..1\" Role=\"ProductPriceGroup\"/>\n" +
				"            </Association>\n" +
				"            <Association Name=\"FK_Product_ProductDiscountGroup\">\n" +
				"                <End Type=\"HybrisCommerceOData.Product\" Multiplicity=\"0..1\" Role=\"Product\"/>\n" +
				"                <End Type=\"HybrisCommerceOData.ProductDiscountGroup\" Multiplicity=\"0..1\" Role=\"ProductDiscountGroup\"/>\n" +
				"            </Association>\n" +
				"            <Association Name=\"FK_Product_VariantType\">\n" +
				"                <End Type=\"HybrisCommerceOData.Product\" Multiplicity=\"0..1\" Role=\"Product\"/>\n" +
				"                <End Type=\"HybrisCommerceOData.VariantType\" Multiplicity=\"0..1\" Role=\"VariantType\"/>\n" +
				"            </Association>\n" +
				"            <Association Name=\"FK_Product_Warehouse\">\n" +
				"                <End Type=\"HybrisCommerceOData.Product\" Multiplicity=\"0..1\" Role=\"Product\"/>\n" +
				"                <End Type=\"HybrisCommerceOData.Warehouse\" Multiplicity=\"0..1\" Role=\"Warehouse\"/>\n" +
				"            </Association>\n" +
				"            <Association Name=\"FK_Warehouse_Vendor\">\n" +
				"                <End Type=\"HybrisCommerceOData.Warehouse\" Multiplicity=\"0..1\" Role=\"Warehouse\"/>\n" +
				"                <End Type=\"HybrisCommerceOData.Vendor\" Multiplicity=\"0..1\" Role=\"Vendor\"/>\n" +
				"            </Association>\n" +
				"            <EntityContainer Name=\"Container\" m:IsDefaultEntityContainer=\"true\">\n" +
				"                <EntitySet Name=\"Catalogs\" EntityType=\"HybrisCommerceOData.Catalog\"/>\n" +
				"                <EntitySet Name=\"VariantTypes\" EntityType=\"HybrisCommerceOData.VariantType\"/>\n" +
				"                <EntitySet Name=\"CatalogVersions\" EntityType=\"HybrisCommerceOData.CatalogVersion\"/>\n" +
				"                <EntitySet Name=\"Units\" EntityType=\"HybrisCommerceOData.Unit\"/>\n" +
				"                <EntitySet Name=\"ProductDiscountGroups\" EntityType=\"HybrisCommerceOData.ProductDiscountGroup\"/>\n" +
				"                <EntitySet Name=\"Vendors\" EntityType=\"HybrisCommerceOData.Vendor\"/>\n" +
				"                <EntitySet Name=\"ProductPriceGroups\" EntityType=\"HybrisCommerceOData.ProductPriceGroup\"/>\n" +
				"                <EntitySet Name=\"Products\" EntityType=\"HybrisCommerceOData.Product\"/>\n" +
				"                <EntitySet Name=\"ProductTaxGroups\" EntityType=\"HybrisCommerceOData.ProductTaxGroup\"/>\n" +
				"                <EntitySet Name=\"Warehouses\" EntityType=\"HybrisCommerceOData.Warehouse\"/>\n" +
				"                <AssociationSet Name=\"CatalogVersion_Catalogs\" Association=\"HybrisCommerceOData.FK_CatalogVersion_Catalog\">\n" +
				"                    <End EntitySet=\"CatalogVersions\" Role=\"CatalogVersion\"/>\n" +
				"                    <End EntitySet=\"Catalogs\" Role=\"Catalog\"/>\n" +
				"                </AssociationSet>\n" +
				"                <AssociationSet Name=\"Product_CatalogVersions\" Association=\"HybrisCommerceOData.FK_Product_CatalogVersion\">\n" +
				"                    <End EntitySet=\"Products\" Role=\"Product\"/>\n" +
				"                    <End EntitySet=\"CatalogVersions\" Role=\"CatalogVersion\"/>\n" +
				"                </AssociationSet>\n" +
				"                <AssociationSet Name=\"Product_Units\" Association=\"HybrisCommerceOData.FK_Product_Unit\">\n" +
				"                    <End EntitySet=\"Products\" Role=\"Product\"/>\n" +
				"                    <End EntitySet=\"Units\" Role=\"Unit\"/>\n" +
				"                </AssociationSet>\n" +
				"                <AssociationSet Name=\"Product_ProductTaxGroups\" Association=\"HybrisCommerceOData.FK_Product_ProductTaxGroup\">\n" +
				"                    <End EntitySet=\"Products\" Role=\"Product\"/>\n" +
				"                    <End EntitySet=\"ProductTaxGroups\" Role=\"ProductTaxGroup\"/>\n" +
				"                </AssociationSet>\n" +
				"                <AssociationSet Name=\"Product_ProductPriceGroups\" Association=\"HybrisCommerceOData.FK_Product_ProductPriceGroup\">\n" +
				"                    <End EntitySet=\"Products\" Role=\"Product\"/>\n" +
				"                    <End EntitySet=\"ProductPriceGroups\" Role=\"ProductPriceGroup\"/>\n" +
				"                </AssociationSet>\n" +
				"                <AssociationSet Name=\"Product_ProductDiscountGroups\" Association=\"HybrisCommerceOData.FK_Product_ProductDiscountGroup\">\n" +
				"                    <End EntitySet=\"Products\" Role=\"Product\"/>\n" +
				"                    <End EntitySet=\"ProductDiscountGroups\" Role=\"ProductDiscountGroup\"/>\n" +
				"                </AssociationSet>\n" +
				"                <AssociationSet Name=\"Product_VariantTypes\" Association=\"HybrisCommerceOData.FK_Product_VariantType\">\n" +
				"                    <End EntitySet=\"Products\" Role=\"Product\"/>\n" +
				"                    <End EntitySet=\"VariantTypes\" Role=\"VariantType\"/>\n" +
				"                </AssociationSet>\n" +
				"                <AssociationSet Name=\"Product_Warehouses\" Association=\"HybrisCommerceOData.FK_Product_Warehouse\">\n" +
				"                    <End EntitySet=\"Products\" Role=\"Product\"/>\n" +
				"                    <End EntitySet=\"Warehouses\" Role=\"Warehouse\"/>\n" +
				"                </AssociationSet>\n" +
				"                <AssociationSet Name=\"Warehouse_Vendors\" Association=\"HybrisCommerceOData.FK_Warehouse_Vendor\">\n" +
				"                    <End EntitySet=\"Warehouses\" Role=\"Warehouse\"/>\n" +
				"                    <End EntitySet=\"Vendors\" Role=\"Vendor\"/>\n" +
				"                </AssociationSet>\n" +
				"            </EntityContainer>\n" +
				"        </Schema>\n" +
				"    </edmx:DataServices>\n" +
				"</edmx:Edmx>";
	}

	private String invalidEdmx()
	{
		return "<very wrong edxm/>";
	}

	private String entityToString(final InputStream entityAsStream) throws IOException
	{
		final StringWriter writer = new StringWriter();
		IOUtils.copy(entityAsStream, writer);
		return writer.toString();
	}
}



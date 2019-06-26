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

package de.hybris.platform.odata2services.odata.content;

public class ODataAtomProductBuilder implements ChangeSetPartContentBuilder
{
	private String productCode;
	private String productName;
	private String catalogId;
	private String catalogVersion;

	private ODataAtomProductBuilder()
	{
		productCode = "TestProduct";
		catalogId = "Default";
		catalogVersion = "Staged";
	}

	public static ODataAtomProductBuilder product()
	{
		return new ODataAtomProductBuilder();
	}

	public ODataAtomProductBuilder withCode(final String code)
	{
		productCode = code;
		return this;
	}

	public ODataAtomProductBuilder withName(final String name)
	{
		productName = name;
		return this;
	}

	public ODataAtomProductBuilder withCatalog(final String id)
	{
		catalogId = id;
		return this;
	}

	@Override
	public String build()
	{
		return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
				"<entry xmlns=\"http://www.w3.org/2005/Atom\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\"\n" +
				"\t   xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\"\n" +
				"\t   xml:base=\"http://stouthost:9002/odata2webservices/InboundIO1/\">\n" +
				"\t<id>http://localhost:9002/odata2webservices/InboundIO1/Products('')</id>\n" +
				"\t<title type=\"text\">Products</title>\n" +
				"\t<updated>2018-12-06T18:35:16.959Z</updated>\n" +
				"\t<category term=\"HybrisCommerceOData.Product\" scheme=\"http://schemas.microsoft.com/ado/2007/08/dataservices/scheme\"></category>\n" +
				"\t<link href=\"Products('')\" rel=\"edit\" title=\"Product\"></link>\n" +
				"\t<link href=\"Products('')/catalogVersion\" rel=\"http://schemas.microsoft.com/ado/2007/08/dataservices/related/catalogVersion\"\n" +
				"\t\t  title=\"catalogVersion\" type=\"application/atom+xml;type=entry\">\n" +
				"\t\t<m:inline>\n" +
				"\t\t\t<entry xml:base=\"http://stouthost:9002/odata2webservices/InboundIO1/\">\n" +
				"\t\t\t\t<id>http://stouthost:9002/odata2webservices/InboundIO1/CatalogVersions('')</id>\n" +
				"\t\t\t\t<title type=\"text\">CatalogVersions</title>\n" +
				"\t\t\t\t<updated>2018-12-06T18:35:16.974Z</updated>\n" +
				"\t\t\t\t<category term=\"HybrisCommerceOData.CatalogVersion\"\n" +
				"\t\t\t\t\t\t  scheme=\"http://schemas.microsoft.com/ado/2007/08/dataservices/scheme\"></category>\n" +
				"\t\t\t\t<link href=\"CatalogVersions('')\" rel=\"edit\" title=\"CatalogVersion\"></link>\n" +
				"\t\t\t\t<link href=\"CatalogVersions('')/catalog\"\n" +
				"\t\t\t\t\t  rel=\"http://schemas.microsoft.com/ado/2007/08/dataservices/related/catalog\" title=\"catalog\"\n" +
				"\t\t\t\t\t  type=\"application/atom+xml;type=entry\">\n" +
				"\t\t\t\t\t<m:inline>\n" +
				"\t\t\t\t\t\t<entry xml:base=\"http://stouthost:9002/odata2webservices/InboundIO1/\">\n" +
				"\t\t\t\t\t\t\t<id>http://stouthost:9002/odata2webservices/InboundIO1/Catalogs('')</id>\n" +
				"\t\t\t\t\t\t\t<title type=\"text\">Catalogs</title>\n" +
				"\t\t\t\t\t\t\t<updated>2018-12-06T18:35:16.974Z</updated>\n" +
				"\t\t\t\t\t\t\t<category term=\"HybrisCommerceOData.Catalog\"\n" +
				"\t\t\t\t\t\t\t\t\t  scheme=\"http://schemas.microsoft.com/ado/2007/08/dataservices/scheme\"></category>\n" +
				"\t\t\t\t\t\t\t<link href=\"Catalogs('')\" rel=\"edit\" title=\"Catalog\"></link>\n" +
				"\t\t\t\t\t\t\t<content type=\"application/xml\">\n" +
				"\t\t\t\t\t\t\t\t<m:properties>\n" +
				"\t\t\t\t\t\t\t\t\t<d:id>" + catalogId + "</d:id>\n" +
				"\t\t\t\t\t\t\t\t\t<d:integrationKey></d:integrationKey>\n" +
				"\t\t\t\t\t\t\t\t</m:properties>\n" +
				"\t\t\t\t\t\t\t</content>\n" +
				"\t\t\t\t\t\t</entry>\n" +
				"\t\t\t\t\t</m:inline>\n" +
				"\t\t\t\t</link>\n" +
				"\t\t\t\t<content type=\"application/xml\">\n" +
				"\t\t\t\t\t<m:properties>\n" +
				"\t\t\t\t\t\t<d:version>" + catalogVersion + "</d:version>\n" +
				"\t\t\t\t\t\t<d:integrationKey></d:integrationKey>\n" +
				"\t\t\t\t\t</m:properties>\n" +
				"\t\t\t\t</content>\n" +
				"\t\t\t</entry>\n" +
				"\t\t</m:inline>\n" +
				"\t</link>\n" +
				"\t<content type=\"application/xml\">\n" +
				"\t\t<m:properties>\n" +
				"\t\t\t<d:code>" + productCode + "</d:code>\n" +
				"\t\t\t<d:name>" + productName + "</d:name>\n" +
				"\t\t\t<d:integrationKey></d:integrationKey>\n" +
				"\t\t</m:properties>\n" +
				"\t</content>\n" +
				"</entry>";
	}
}

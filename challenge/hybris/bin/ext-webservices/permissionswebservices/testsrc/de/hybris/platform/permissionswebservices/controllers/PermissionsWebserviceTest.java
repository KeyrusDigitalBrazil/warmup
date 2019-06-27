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
package de.hybris.platform.permissionswebservices.controllers;


import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.permissionswebservices.constants.PermissionswebservicesConstants;
import de.hybris.platform.permissionswebservices.dto.CatalogPermissionsListWsDTO;
import de.hybris.platform.permissionswebservices.dto.CatalogPermissionsWsDTO;
import de.hybris.platform.permissionswebservices.dto.PermissionsListWsDTO;
import de.hybris.platform.permissionswebservices.dto.PermissionsWsDTO;
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.jaxb.Jaxb2HttpMessageConverter;
import de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert;
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ PermissionswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class PermissionsWebserviceTest extends AbstractPermissionsWebServicesTest
{

	private static final String FALSE = "false";
	private static final String TRUE = "true";
	private static final String CHANGE_PERM = "changerights";
	private static final String REMOVE = "remove";
	private static final String CREATE = "create";
	private static final String CHANGE = "change";
	private static final String READ = "read";
	private static final String WRITE = "write";
	private static final String USER_TYPECODE = "User";
	private static final String ORDER_TYPECODE = "Order";
	private static final String USER_ORDER_TYPECODES = "User,Order";
	private static final String SUPERGROUP = "supergroup";
	private static final String GROUP1 = "group1";
	private static final String GROUP2 = "group2";
	private static final String SUBGROUP1 = "subgroup1";
	private static final String SUBGROUP2 = "subgroup2";
	private static final String USER1 = "user1";
	private static final String USER2 = "user2";
	private static final String USER3 = "user3";
	private static final String USER4 = "user4";
	private static final String USER5 = "user5";
	private static final String USER6 = "user6";
	private static final Map<String, String> ALL_FALSE_PERMISSIONS;
	private static final Map<String, String> ALL_TRUE_PERMISSIONS;
	private static final Map<String, String> READ_TRUE_PERMISSIONS;
	private static final Map<String, String> READ_TRUE_CVPERMISSIONS;
	private static final Map<String, String> WRITE_TRUE_CVPERMISSIONS;
	private static final Map<String, String> ALL_FALSE_CVPERMISSIONS;
	private static final String FIELDS_BASIC = "BASIC";
	private static final String FIELDS_FULL = "FULL";
	private static final String PASSWORD = "1234";

	static
	{
		ALL_FALSE_PERMISSIONS = new HashMap<String, String>();
		ALL_FALSE_PERMISSIONS.put(READ, FALSE);
		ALL_FALSE_PERMISSIONS.put(CHANGE, FALSE);
		ALL_FALSE_PERMISSIONS.put(CREATE, FALSE);
		ALL_FALSE_PERMISSIONS.put(REMOVE, FALSE);
		ALL_FALSE_PERMISSIONS.put(CHANGE_PERM, FALSE);

		ALL_TRUE_PERMISSIONS = new HashMap<String, String>();
		ALL_TRUE_PERMISSIONS.put(READ, TRUE);
		ALL_TRUE_PERMISSIONS.put(CHANGE, TRUE);
		ALL_TRUE_PERMISSIONS.put(CREATE, TRUE);
		ALL_TRUE_PERMISSIONS.put(REMOVE, TRUE);
		ALL_TRUE_PERMISSIONS.put(CHANGE_PERM, TRUE);

		READ_TRUE_PERMISSIONS = new HashMap<String, String>();
		READ_TRUE_PERMISSIONS.put(READ, TRUE);
		READ_TRUE_PERMISSIONS.put(CHANGE, FALSE);
		READ_TRUE_PERMISSIONS.put(CREATE, FALSE);
		READ_TRUE_PERMISSIONS.put(REMOVE, FALSE);
		READ_TRUE_PERMISSIONS.put(CHANGE_PERM, FALSE);

		READ_TRUE_CVPERMISSIONS = new HashMap<String, String>();
		READ_TRUE_CVPERMISSIONS.put(READ, TRUE);
		READ_TRUE_CVPERMISSIONS.put(WRITE, FALSE);

		WRITE_TRUE_CVPERMISSIONS = new HashMap<String, String>();
		WRITE_TRUE_CVPERMISSIONS.put(READ, TRUE);
		WRITE_TRUE_CVPERMISSIONS.put(WRITE, TRUE);

		ALL_FALSE_CVPERMISSIONS = new HashMap<String, String>();
		ALL_FALSE_CVPERMISSIONS.put(READ, FALSE);
		ALL_FALSE_CVPERMISSIONS.put(WRITE, FALSE);

	}


	@Resource(name = "jsonHttpMessageConverter")
	private Jaxb2HttpMessageConverter defaultJsonHttpMessageConverter;

	private static final String VERSION = "v1";

	private WsSecuredRequestBuilder wsSecuredRequestBuilder;

	@Before
	public void importTestData() throws ImpExException
	{
		wsSecuredRequestBuilder = new WsSecuredRequestBuilder()//
				.extensionName(PermissionswebservicesConstants.EXTENSIONNAME)//
				.path(VERSION)//
				.client("mobile_android", "secret");
		importData(new ClasspathImpExResource("/permissionswebservices/test/testpermissions.impex", "UTF-8"));
		insertGlobalPermission(SUBGROUP2, "globalpermission1");
	}

	@Test
	public void shouldReturnAllTypesAccessesForAdmin() throws IOException, JAXBException
	{
		//when retrieving type permissions
		final PermissionsListWsDTO entity = retrieveTypesPermissions("admin", "admin", USER_ORDER_TYPECODES, "nimda");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO(USER_TYPECODE, ALL_TRUE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO(ORDER_TYPECODE, ALL_TRUE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnsOtherUserPermissiondForAdmin() throws IOException, JAXBException
	{
		//when retrieving type permissions
		final PermissionsListWsDTO entity = retrieveTypesPermissions(USER1, "admin", USER_ORDER_TYPECODES, "nimda");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO(USER_TYPECODE, ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO(ORDER_TYPECODE, ALL_FALSE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnTypesAccessesForSuperGroup() throws JAXBException
	{
		//given user, when retrieving type permissions
		final PermissionsListWsDTO entity = retrieveTypesPermissions(SUPERGROUP, USER1, USER_ORDER_TYPECODES);

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO(USER_TYPECODE, ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO(ORDER_TYPECODE, ALL_FALSE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnTypesAccessesForUser1() throws JAXBException
	{
		//given user, when retrieving type permissions
		final PermissionsListWsDTO entity = retrieveTypesPermissions(USER1, USER1, USER_ORDER_TYPECODES);

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO(USER_TYPECODE, ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO(ORDER_TYPECODE, ALL_FALSE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnTypesAccessesForGroup1() throws JAXBException
	{
		//given user, when retrieving type permissions
		final PermissionsListWsDTO entity = retrieveTypesPermissions(GROUP1, USER2, USER_ORDER_TYPECODES);

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO(USER_TYPECODE, READ_TRUE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO(ORDER_TYPECODE, ALL_FALSE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnTypesAccessesForUser2() throws JAXBException
	{
		//given user, when retrieving type permissions
		final PermissionsListWsDTO entity = retrieveTypesPermissions(USER2, USER2, USER_ORDER_TYPECODES);

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO(USER_TYPECODE, READ_TRUE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO(ORDER_TYPECODE, ALL_FALSE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnTypesAccessesForGroup2() throws JAXBException
	{
		//given user, when retrieving type permissions
		final PermissionsListWsDTO entity = retrieveTypesPermissions(GROUP2, USER3, USER_ORDER_TYPECODES);

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO(USER_TYPECODE, ALL_TRUE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO(ORDER_TYPECODE, ALL_FALSE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnTypesAccessesForUser3() throws JAXBException
	{
		//given user, when retrieving type permissions
		final PermissionsListWsDTO entity = retrieveTypesPermissions(USER3, USER3, USER_ORDER_TYPECODES);

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO(USER_TYPECODE, ALL_TRUE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO(ORDER_TYPECODE, ALL_FALSE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnTypesAccessesForSubGroup1() throws JAXBException
	{
		//given user, when retrieving type permissions
		final PermissionsListWsDTO entity = retrieveTypesPermissions(SUBGROUP1, USER4, USER_ORDER_TYPECODES);

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO(USER_TYPECODE, READ_TRUE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO(ORDER_TYPECODE, READ_TRUE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnTypesAccessesForUser4() throws JAXBException
	{
		//given user, when retrieving type permissions
		final PermissionsListWsDTO entity = retrieveTypesPermissions(USER4, USER4, USER_ORDER_TYPECODES);

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO(USER_TYPECODE, READ_TRUE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO(ORDER_TYPECODE, READ_TRUE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnTypesAccessesForSubGroup2() throws JAXBException
	{
		//given user, when retrieving type permissions
		final PermissionsListWsDTO entity = retrieveTypesPermissions(SUBGROUP2, USER5, USER_ORDER_TYPECODES);

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO(USER_TYPECODE, ALL_TRUE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO(ORDER_TYPECODE, ALL_TRUE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}


	@Test
	public void shouldReturnTypesAccessesForUser5() throws JAXBException
	{
		//given user, when retrieving type permissions
		final PermissionsListWsDTO entity = retrieveTypesPermissions(USER5, USER5, USER_ORDER_TYPECODES);

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO(USER_TYPECODE, ALL_TRUE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO(ORDER_TYPECODE, ALL_TRUE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnTypesAccessesForUser6() throws JAXBException
	{
		//given user, when retrieving type permissions
		final PermissionsListWsDTO entity = retrieveTypesPermissions(USER6, USER6, USER_ORDER_TYPECODES);

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO(USER_TYPECODE, ALL_TRUE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO(ORDER_TYPECODE, ALL_FALSE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}


	@Test
	public void shouldReturnAttributeAccessesForSuperGroup() throws JAXBException
	{
		//given user, when retrieving attribute permissions
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(SUPERGROUP, USER1, "Item.owner");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.owner", ALL_FALSE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnAttributeAccessesForUser1() throws JAXBException
	{
		//given user, when retrieving attribute permissions
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(USER1, USER1, "Item.owner");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.owner", ALL_FALSE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnUserOwnerAttributeAccessesForSuperGroup() throws JAXBException
	{
		//given user, when retrieving attribute permissions
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(SUPERGROUP, USER1, "User.owner");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("User.owner", ALL_FALSE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnUserOwnerAttributeAccessesForUser1() throws JAXBException
	{
		//given user, when retrieving attribute permissions
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(USER1, USER1, "User.owner");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("User.owner", ALL_FALSE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnItemAttributesAccessesForSuperGroup() throws JAXBException
	{
		//given user, when retrieving attribute permissions
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(SUPERGROUP, USER1, "Item.owner,Item.pk");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.owner", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.pk", ALL_FALSE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnItemAttributesAccessesForUser1() throws JAXBException
	{
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(USER1, USER1, "Item.owner,Item.pk");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.owner", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.pk", ALL_FALSE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnUserAttributesAccessesForSuperGroup() throws JAXBException
	{
		//given user, when retrieving attribute permissions
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(SUPERGROUP, USER1, "User.owner,User.pk");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("User.owner", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("User.pk", ALL_FALSE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnUserAttributesAccessesForUser1() throws JAXBException
	{
		//given user, when retrieving attribute permissions
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(USER1, USER1, "User.owner,User.pk");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("User.owner", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("User.pk", ALL_FALSE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnItemWildcardAtrAttributeAccessesForSuperGroup() throws JAXBException
	{
		//given user, when retrieving attribute permissions
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(SUPERGROUP, USER1, "Item.*");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.allDocuments", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.comments", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.creationtime", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.itemtype", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.modifiedtime", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.owner", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.pk", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.savedValues", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.synchronizationSources", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.synchronizedCopies", ALL_FALSE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnItemWildcardAtrAttributeAccessesForUser1() throws JAXBException
	{
		//given user, when retrieving attribute permissions
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(USER1, USER1, "Item.*");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.allDocuments", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.comments", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.creationtime", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.itemtype", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.modifiedtime", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.owner", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.pk", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.savedValues", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.synchronizationSources", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.synchronizedCopies", ALL_FALSE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnItemOwnerAttributeAccessesForGroup1() throws JAXBException
	{
		//given user, when retrieving attribute permissions
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(GROUP1, USER2, "Item.owner");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.owner", READ_TRUE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnItemOwnerAttributeAccessesForUser2() throws JAXBException
	{
		//given user, when retrieving attribute permissions
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(USER2, USER2, "Item.owner");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.owner", READ_TRUE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnUserOwnerAttributesAccessesForGroup1() throws JAXBException
	{
		//given user, when retrieving attribute permissions
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(GROUP1, USER2, "User.owner");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("User.owner", READ_TRUE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnUserOwnerAttributesAccessesForUser2() throws JAXBException
	{
		//given user, when retrieving attribute permissions
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(USER2, USER2, "User.owner");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("User.owner", READ_TRUE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnItemAttributesAccessesForGroup1() throws JAXBException
	{
		//given user, when retrieving attribute permissions
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(GROUP1, USER2, "Item.owner,Item.pk");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.owner", READ_TRUE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.pk", ALL_FALSE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnItemAttributesAccessesForUser2() throws JAXBException
	{
		//given user, when retrieving attribute permissions
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(USER2, USER2, "Item.owner,Item.pk");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.owner", READ_TRUE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.pk", ALL_FALSE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnUserAttributesAccessesForGroup1() throws JAXBException
	{
		//given user, when retrieving attribute permissions
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(GROUP1, USER2, "User.owner,User.pk");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("User.owner", READ_TRUE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("User.pk", READ_TRUE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnUserAttributesAccessesForUser2() throws JAXBException
	{
		//given user, when retrieving attribute permissions
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(USER2, USER2, "User.owner,User.pk");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("User.owner", READ_TRUE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("User.pk", READ_TRUE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnItemWildcardAttributesAccessesForGroup1() throws JAXBException
	{
		//given user, when retrieving attribute permissions
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(GROUP1, USER2, "Item.*");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.allDocuments", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.comments", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.creationtime", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.itemtype", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.modifiedtime", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.owner", READ_TRUE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.pk", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.savedValues", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.synchronizationSources", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.synchronizedCopies", ALL_FALSE_PERMISSIONS));

		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnItemWildcardAttributesAccessesForUser2() throws JAXBException
	{
		//given user, when retrieving attribute permissions
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(USER2, USER2, "Item.*");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.allDocuments", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.comments", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.creationtime", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.itemtype", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.modifiedtime", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.owner", READ_TRUE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.pk", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.savedValues", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.synchronizationSources", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.synchronizedCopies", ALL_FALSE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnItemOwnerAttributeAccessesForGroup2() throws JAXBException
	{
		//given user, when retrieving attribute permissions
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(GROUP2, USER3, "Item.owner");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.owner", ALL_TRUE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnItemOwnerAttributeAccessesForUser3() throws JAXBException
	{
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(USER3, USER3, "Item.owner");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.owner", ALL_TRUE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnUserOwnerAttributeAccessesForGroup2() throws JAXBException
	{
		//given user, when retrieving attribute permissions
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(GROUP2, USER3, "User.owner");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("User.owner", ALL_TRUE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnUserOwnerAttributeAccessesForUser3() throws JAXBException
	{
		//given user, when retrieving attribute permissions
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(USER3, USER3, "User.owner");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("User.owner", ALL_TRUE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnItemAttributesAccessesForGroup2() throws JAXBException
	{
		//given user, when retrieving attribute permissions
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(GROUP2, USER3, "Item.owner,Item.pk");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.owner", ALL_TRUE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.pk", ALL_FALSE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnItemAttributesAccessesForUser3() throws JAXBException
	{
		//given user, when retrieving attribute permissions
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(USER3, USER3, "Item.owner,Item.pk");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.owner", ALL_TRUE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.pk", ALL_FALSE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnUserAttributesAccessesForGroup2() throws JAXBException
	{
		//given user, when retrieving attribute permissions
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(GROUP2, USER3, "User.owner,User.pk");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("User.owner", ALL_TRUE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("User.pk", ALL_TRUE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnUserAttributesAccessesForUser3() throws JAXBException
	{
		//given user, when retrieving attribute permissions
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(USER3, USER3, "User.owner,User.pk");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("User.owner", ALL_TRUE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("User.pk", ALL_TRUE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnItemWildcardAttributeAccessesForGroup2() throws JAXBException
	{
		//given user, when retrieving attribute permissions
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(GROUP2, USER3, "Item.*");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.allDocuments", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.comments", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.creationtime", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.itemtype", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.modifiedtime", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.owner", ALL_TRUE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.pk", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.savedValues", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.synchronizationSources", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.synchronizedCopies", ALL_FALSE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnItemWildcardAttributeAccessesForUser3() throws JAXBException
	{
		//given user, when retrieving attribute permissions
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(USER3, USER3, "Item.*");

		//then the following permissions should be returned
		final PermissionsListWsDTO expected = new PermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<PermissionsWsDTO>());
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.allDocuments", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.comments", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.creationtime", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.itemtype", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.modifiedtime", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.owner", ALL_TRUE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.pk", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.savedValues", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.synchronizationSources", ALL_FALSE_PERMISSIONS));
		expected.getPermissionsList().add(generateExpectedPermissionsWsDTO("Item.synchronizedCopies", ALL_FALSE_PERMISSIONS));
		assertEqualsPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnGlobalPermissionForSubgroup2() throws JAXBException
	{
		//given user, when retrieving global permissions
		final PermissionsWsDTO entity = retrieveGlobalPermissions(SUBGROUP2, USER5);

		//then the following permissions should be returned
		final HashMap<String, String> permissions = new HashMap<String, String>();
		permissions.put("globalpermission1", "true");
		final PermissionsWsDTO expected = generateExpectedPermissionsWsDTO("global", permissions);
		assertEqualsPermissionsWsDTO(expected, entity);
	}


	@Test
	public void shouldReturnGlobalPermissionForUser5() throws JAXBException
	{
		//given user, when retrieving global permissions
		final PermissionsWsDTO entity = retrieveGlobalPermissions(USER5, USER5);

		//then the following permissions should be returned
		final HashMap<String, String> permissions = new HashMap<String, String>();
		permissions.put("globalpermission1", "true");
		final PermissionsWsDTO expected = generateExpectedPermissionsWsDTO("global", permissions);
		assertEqualsPermissionsWsDTO(expected, entity);
	}

	@Test
	public void shouldReturn403WhenUser1AccessGroup1Global() throws JAXBException
	{
		final Response entity = retrieveGlobalPermissionsResponse(GROUP1, USER1);
		Assert.assertEquals(403, entity.getStatus());
	}

	@Test
	public void shouldReturn403WhenUser1AccessUser5Global() throws JAXBException
	{
		final Response entity = retrieveGlobalPermissionsResponse(USER5, USER1);
		Assert.assertEquals(403, entity.getStatus());
	}

	@Test
	public void shouldReturn403WhenUser1AccessUser5Type() throws JAXBException
	{
		final Response entity = retrieveTypesPermissionsResponse(USER5, USER1, USER_TYPECODE, null);
		Assert.assertEquals(403, entity.getStatus());
	}

	@Test
	public void shouldReturn403WhenUser1AccessGroup1Type() throws JAXBException
	{
		final Response entity = retrieveTypesPermissionsResponse(GROUP1, USER1, USER_TYPECODE, null);
		Assert.assertEquals(403, entity.getStatus());
	}

	@Test
	public void shouldReturn403WhenUser1AccessUser5Attributes() throws JAXBException
	{
		final Response entity = retrieveAttributesPermissionsResponse(USER5, USER1, USER_TYPECODE + ".*");
		Assert.assertEquals(403, entity.getStatus());
	}

	@Test
	public void shouldReturn403WhenUser1AccessGroupAttributes() throws JAXBException
	{
		final Response entity = retrieveAttributesPermissionsResponse(GROUP1, USER1, USER_TYPECODE + ".*");
		Assert.assertEquals(403, entity.getStatus());
	}

	@Test
	public void shouldReturn200WhenUser5AccessSupergroupGlobal() throws JAXBException
	{
		final Response entity = retrieveGlobalPermissionsResponse(SUPERGROUP, USER5);
		Assert.assertEquals(200, entity.getStatus());
	}

	@Test
	public void shouldReturn200WhenUser5AccessGroup2Global() throws JAXBException
	{

		final Response entity = retrieveGlobalPermissionsResponse(GROUP2, USER5);
		Assert.assertEquals(200, entity.getStatus());
	}

	@Test
	public void shouldReturn200WhenUser5AccessSubgroup2Global() throws JAXBException
	{
		final Response entity = retrieveGlobalPermissionsResponse(SUBGROUP2, USER5);
		Assert.assertEquals(200, entity.getStatus());
	}

	@Test
	public void shouldReturn200WhenUser5AccessSupergroupType() throws JAXBException
	{
		final Response entity = retrieveTypesPermissionsResponse(SUPERGROUP, USER5, USER_TYPECODE, null);
		Assert.assertEquals(200, entity.getStatus());
	}

	@Test
	public void shouldReturn200WhenUser5AccessGroup2Type() throws JAXBException
	{
		final Response entity = retrieveTypesPermissionsResponse(GROUP2, USER5, USER_TYPECODE, null);
		Assert.assertEquals(200, entity.getStatus());
	}

	@Test
	public void shouldReturn200WhenUser5AccessSubgroup2Type() throws JAXBException
	{
		final Response entity = retrieveTypesPermissionsResponse(SUBGROUP2, USER5, USER_TYPECODE, null);
		Assert.assertEquals(200, entity.getStatus());
	}

	@Test
	public void shouldReturn200WhenUser5AccessSupergroupAttributes() throws JAXBException
	{
		final Response entity = retrieveAttributesPermissionsResponse(SUPERGROUP, USER5, USER_TYPECODE + ".*");
		Assert.assertEquals(200, entity.getStatus());
	}

	@Test
	public void shouldReturn200WhenUser5AccessGroup2Attributes() throws JAXBException
	{
		final Response entity = retrieveAttributesPermissionsResponse(GROUP2, USER5, USER_TYPECODE + ".*");
		Assert.assertEquals(200, entity.getStatus());

	}

	@Test
	public void shouldReturn200WhenEmployeemanager5AccessGroup2Attributes() throws JAXBException
	{
		final Response entity = retrieveAttributesPermissionsResponse(GROUP2, "employeemanager", USER_TYPECODE + ".*");
		Assert.assertEquals(200, entity.getStatus());

	}

	@Test
	public void shouldReturn200WhenUser5AccessSubgroup2() throws JAXBException
	{
		final Response entity = retrieveAttributesPermissionsResponse(SUBGROUP2, USER5, USER_TYPECODE + ".*");
		Assert.assertEquals(200, entity.getStatus());
	}

	@Test
	public void shouldFailValidationForAttributes() throws IOException, JAXBException
	{
		final Response result = wsSecuredRequestBuilder//
				.path("permissions")//
				.path("principals")//
				.path("user2")//
				.path("attributes")//
				.queryParam("permissionNames", "read,change,create,remove,changerights")//
				.queryParam("attributes", "somattributewithouttype")//
				.resourceOwner(USER2, PASSWORD)//
				.grantResourceOwnerPasswordCredentials()//
				.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.get();

		result.bufferEntity();

		Assert.assertEquals(400, result.getStatus());
		final ErrorListWsDTO errorsList = result.readEntity(ErrorListWsDTO.class);
		Assert.assertNotNull(errorsList);
		Assert.assertEquals(1, errorsList.getErrors().size());
		Assert.assertEquals("invalid", errorsList.getErrors().get(0).getReason());
		Assert.assertEquals("attributes", errorsList.getErrors().get(0).getSubject());
		Assert.assertEquals("parameter", errorsList.getErrors().get(0).getSubjectType());
		Assert.assertEquals("ValidationError", errorsList.getErrors().get(0).getType());
	}

	@Test
	public void shouldReturnGlobalPermissionsIdOnlyWhenFieldsSetToBasic() throws JAXBException
	{
		final PermissionsWsDTO entity = retrieveGlobalPermissions(USER5, USER5, FIELDS_BASIC);
		Assert.assertNotNull(entity.getId());
		Assert.assertNull(entity.getPermissions());
	}

	@Test
	public void shouldReturnGlobalPermissionsIdOnlyWhenFieldsSetToId() throws JAXBException
	{
		final PermissionsWsDTO entity = retrieveGlobalPermissions(USER5, USER5, "id");
		Assert.assertNotNull(entity.getId());
		Assert.assertNull(entity.getPermissions());
	}

	@Test
	public void shouldReturnGlobalPermissionsAllFieldsWhenFieldsSetToFull() throws JAXBException
	{
		final PermissionsWsDTO entity = retrieveGlobalPermissions(USER5, USER5, FIELDS_FULL);
		Assert.assertNotNull(entity.getId());
		Assert.assertNotNull(entity.getPermissions());
	}

	@Test
	public void shouldReturnGlobalPermissions400WhenFieldsSetIncorrectly()
	{
		final Response response = retrieveGlobalPermissionsResponse(USER5, USER5, "nonexistentField");
		Assert.assertEquals(400, response.getStatus());
	}

	@Test
	public void shouldReturnAttributesPermissionsIdOnlyWhenFieldsSetToBasic() throws JAXBException
	{
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(SUPERGROUP, USER1, "Item.owner", FIELDS_BASIC);
		Assert.assertNotNull(entity.getPermissionsList().get(0).getId());
		Assert.assertNull(entity.getPermissionsList().get(0).getPermissions());
	}

	@Test
	public void shouldReturnAttributesPermissionsIdOnlyWhenFieldsSetToId() throws JAXBException
	{
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(SUPERGROUP, USER1, "Item.owner", "permissionsList(id)");
		Assert.assertNotNull(entity.getPermissionsList().get(0).getId());
		Assert.assertNull(entity.getPermissionsList().get(0).getPermissions());
	}

	@Test
	public void shouldReturnAttributesPermissionsAllFieldsWhenFieldsSetToFull() throws JAXBException
	{
		final PermissionsListWsDTO entity = retrieveAttributesPermissions(SUPERGROUP, USER1, "Item.owner", FIELDS_FULL);
		Assert.assertNotNull(entity.getPermissionsList().get(0).getId());
		Assert.assertNotNull(entity.getPermissionsList().get(0).getPermissions());
	}

	@Test
	public void shouldReturnAttributesPermissions400WhenFieldsSetIncorrectly()
	{
		final Response response = retrieveAttributesPermissionsResponse(SUPERGROUP, USER1, "Item.owner", "nonexistentField");
		Assert.assertEquals(400, response.getStatus());
	}

	@Test
	public void shouldReturnTypesPermissionsIdOnlyWhenFieldsSetToBasic() throws JAXBException
	{
		final PermissionsListWsDTO entity = retrieveTypesPermissions(USER1, USER1, USER_ORDER_TYPECODES, null, FIELDS_BASIC);
		Assert.assertNotNull(entity.getPermissionsList().get(0).getId());
		Assert.assertNull(entity.getPermissionsList().get(0).getPermissions());
	}

	@Test
	public void shouldReturnTypesPermissionsIdOnlyWhenFieldsSetToId() throws JAXBException
	{
		final PermissionsListWsDTO entity = retrieveTypesPermissions(USER1, USER1, USER_ORDER_TYPECODES, null,
				"permissionsList(id)");
		Assert.assertNotNull(entity.getPermissionsList().get(0).getId());
		Assert.assertNull(entity.getPermissionsList().get(0).getPermissions());
	}

	@Test
	public void shouldReturnTypesPermissionsAllFieldsWhenFieldsSetToFull() throws JAXBException
	{
		final PermissionsListWsDTO entity = retrieveTypesPermissions(USER1, USER1, USER_ORDER_TYPECODES, null, FIELDS_FULL);
		Assert.assertNotNull(entity.getPermissionsList().get(0).getId());
		Assert.assertNotNull(entity.getPermissionsList().get(0).getPermissions());
	}

	@Test
	public void shouldReturnTypesPermissions400WhenFieldsSetIncorrectly()
	{
		final Response response = retrieveTypesPermissionsResponse(USER1, USER1, USER_ORDER_TYPECODES, null, "nonexistentField");
		Assert.assertEquals(400, response.getStatus());
	}

	@Test
	public void shouldReturn403WhenUser1AccessGroup1Catalogs() throws JAXBException
	{
		final Response entity = retrieveCatalogVersionPermissionsResponse(GROUP1, USER1, "catalog1", "Staged");
		Assert.assertEquals(403, entity.getStatus());
	}

	@Test
	public void shouldReturn400WhenNoCatalogIsPassed() throws JAXBException
	{
		final Response entity = retrieveCatalogVersionPermissionsResponse(GROUP1, USER1, null, "Staged");
		Assert.assertEquals(403, entity.getStatus());
	}

	@Test
	public void shouldReturn400WhenNoVersionIsPassed() throws JAXBException
	{
		final Response entity = retrieveCatalogVersionPermissionsResponse(GROUP1, USER1, "catalog1", null);
		Assert.assertEquals(403, entity.getStatus());
	}

	@Test
	public void shouldReturnReadPermissionsFor1CatalogVersion() throws JAXBException
	{
		final CatalogPermissionsListWsDTO entity = retrieveCatalogVersionPermissions(GROUP1, USER2, "catalog1", "Staged");
		final CatalogPermissionsListWsDTO expected = new CatalogPermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<>());
		expected.getPermissionsList().add(generateExpectedCatalogPermissionsWsDTO("catalog1", "Staged", READ_TRUE_CVPERMISSIONS));
		assertEqualsCatalogPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnReadPermissionsFor2CatalogVersion() throws JAXBException
	{
		final CatalogPermissionsListWsDTO entity = retrieveCatalogVersionPermissions(GROUP1, USER2, "catalog1", "Staged,Online");
		final CatalogPermissionsListWsDTO expected = new CatalogPermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<>());
		expected.getPermissionsList().add(generateExpectedCatalogPermissionsWsDTO("catalog1", "Staged", READ_TRUE_CVPERMISSIONS));
		expected.getPermissionsList().add(generateExpectedCatalogPermissionsWsDTO("catalog1", "Online", ALL_FALSE_CVPERMISSIONS));
		assertEqualsCatalogPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnReadPermissionsFor2Catalogs() throws JAXBException
	{
		final CatalogPermissionsListWsDTO entity = retrieveCatalogVersionPermissions(GROUP1, USER2, "catalog1,catalog2",
				"Staged,Online");
		final CatalogPermissionsListWsDTO expected = new CatalogPermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<>());
		expected.getPermissionsList().add(generateExpectedCatalogPermissionsWsDTO("catalog1", "Staged", READ_TRUE_CVPERMISSIONS));
		expected.getPermissionsList().add(generateExpectedCatalogPermissionsWsDTO("catalog1", "Online", ALL_FALSE_CVPERMISSIONS));
		expected.getPermissionsList().add(generateExpectedCatalogPermissionsWsDTO("catalog2", "Online", READ_TRUE_CVPERMISSIONS));
		assertEqualsCatalogPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturnWritePermissionsFor1CatalogVersion() throws JAXBException
	{
		final CatalogPermissionsListWsDTO entity = retrieveCatalogVersionPermissions(GROUP2, USER3, "catalog1", "Staged");
		final CatalogPermissionsListWsDTO expected = new CatalogPermissionsListWsDTO();
		expected.setPermissionsList(new ArrayList<>());
		expected.getPermissionsList().add(generateExpectedCatalogPermissionsWsDTO("catalog1", "Staged", WRITE_TRUE_CVPERMISSIONS));
		assertEqualsCatalogPermissionsListWsDTO(expected, entity);
	}

	@Test
	public void shouldReturn400WithLocalizedErrorMessageInEnglish() throws JAXBException
	{
		final Response result = retrieveAttributesPermissionsResponseInLanguage(USER1, USER1, "testWrongAttr", null);
		Assert.assertEquals(400, result.getStatus());
		final ErrorListWsDTO errorsList = result.readEntity(ErrorListWsDTO.class);
		Assert.assertNotNull(errorsList);
		Assert.assertEquals(1, errorsList.getErrors().size());
		Assert.assertEquals("Input does not match regex.", errorsList.getErrors().get(0).getMessage());
	}

	@Test
	public void shouldReturn400WithLocalizedErrorMessageInGerman() throws JAXBException
	{
		final Response result = retrieveAttributesPermissionsResponseInLanguage(USER1, USER1, "testWrongAttr", Locale.GERMAN);
		Assert.assertEquals(400, result.getStatus());
		final ErrorListWsDTO errorsList = result.readEntity(ErrorListWsDTO.class);
		Assert.assertNotNull(errorsList);
		Assert.assertEquals(1, errorsList.getErrors().size());
		Assert.assertEquals("Eingabe entspricht nicht Regex.", errorsList.getErrors().get(0).getMessage());
	}

	protected PermissionsListWsDTO retrieveAttributesPermissions(final String principal, final String user,
			final String attributes) throws JAXBException
	{
		return retrieveAttributesPermissions(principal, user, attributes, null);
	}

	protected PermissionsListWsDTO retrieveAttributesPermissions(final String principal, final String user,
			final String attributes, final String fields) throws JAXBException
	{
		final Response result = retrieveAttributesPermissionsResponse(principal, user, attributes, fields);
		Assert.assertEquals(200, result.getStatus());

		final PermissionsListWsDTO entity = unmarshallResult(result, PermissionsListWsDTO.class);
		return entity;
	}

	protected Response retrieveAttributesPermissionsResponse(final String principal, final String user, final String attributes)
	{
		return retrieveAttributesPermissionsResponse(principal, user, attributes, null);
	}

	protected Response retrieveAttributesPermissionsResponseInLanguage(final String principal, final String user,
			final String attributes, final Locale language)
	{
		final WsSecuredRequestBuilder builder = wsSecuredRequestBuilder//
				.path("permissions")//
				.path("principals")//
				.path(principal)//
				.path("attributes")//
				.queryParam("fields", FIELDS_FULL)//
				.queryParam("permissionNames", "read,change,create,remove,changerights")//
				.queryParam("attributes", attributes)//
				.resourceOwner(user, PASSWORD)//
				.grantResourceOwnerPasswordCredentials();

		final Response result = builder.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.acceptLanguage(language)//
				.get();

		result.bufferEntity();

		return result;
	}

	protected Response retrieveAttributesPermissionsResponse(final String principal, final String user, final String attributes,
			final String fields)
	{
		WsSecuredRequestBuilder builder = wsSecuredRequestBuilder//
				.path("permissions")//
				.path("principals")//
				.path(principal)//
				.path("attributes")//
				.queryParam("permissionNames", "read,change,create,remove,changerights")//
				.queryParam("attributes", attributes)//
				.resourceOwner(user, PASSWORD)//
				.grantResourceOwnerPasswordCredentials();

		if (fields != null)
		{
			builder = builder.queryParam("fields", fields);
		}

		final Response result = builder.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.get();

		result.bufferEntity();
		return result;
	}

	protected PermissionsListWsDTO retrieveTypesPermissions(final String principal, final String user, final String types)
			throws JAXBException
	{
		return retrieveTypesPermissions(principal, user, types, null);
	}

	protected PermissionsListWsDTO retrieveTypesPermissions(final String principal, final String user, final String types,
			final String password) throws JAXBException
	{
		return retrieveTypesPermissions(principal, user, types, password, null);
	}

	protected PermissionsListWsDTO retrieveTypesPermissions(final String principal, final String user, final String types,
			final String password, final String fields) throws JAXBException
	{
		final Response response = retrieveTypesPermissionsResponse(principal, user, types, password, fields);
		WebservicesAssert.assertResponse(Status.OK, response);

		final PermissionsListWsDTO entity = unmarshallResult(response, PermissionsListWsDTO.class);
		return entity;
	}

	protected Response retrieveTypesPermissionsResponse(final String principal, final String user, final String types,
			final String password)
	{
		return retrieveTypesPermissionsResponse(principal, user, types, password, null);
	}

	protected Response retrieveTypesPermissionsResponse(final String principal, final String user, final String types,
			final String password, final String fields)
	{
		WsSecuredRequestBuilder builder = wsSecuredRequestBuilder//
				.path("permissions")//
				.path("principals")//
				.path(principal)//
				.path("types")//
				.queryParam("types", types)//
				.queryParam("permissionNames", "read,change,create,remove,changerights")//
				.resourceOwner(user, password == null ? PASSWORD : password)//
				.grantResourceOwnerPasswordCredentials();

		if (fields != null)
		{
			builder = builder.queryParam("fields", fields);
		}

		final Response result = builder.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.get();

		result.bufferEntity();
		return result;
	}

	protected PermissionsWsDTO retrieveGlobalPermissions(final String principal, final String user) throws JAXBException
	{
		return retrieveGlobalPermissions(principal, user, null);
	}

	protected PermissionsWsDTO retrieveGlobalPermissions(final String principal, final String user, final String fields)
			throws JAXBException
	{
		final Response response = retrieveGlobalPermissionsResponse(principal, user, fields);
		WebservicesAssert.assertResponse(Status.OK, response);

		final PermissionsWsDTO entity = unmarshallResult(response, PermissionsWsDTO.class);
		return entity;
	}

	protected Response retrieveGlobalPermissionsResponse(final String principal, final String user)
	{
		return retrieveGlobalPermissionsResponse(principal, user, null);
	}

	protected Response retrieveGlobalPermissionsResponse(final String principal, final String user, final String fields)
	{
		WsSecuredRequestBuilder builder = wsSecuredRequestBuilder//
				.path("permissions")//
				.path("principals")//
				.path(principal)//
				.path("global")//
				.queryParam("permissionNames", "globalpermission1").resourceOwner(user, "1234")
				.grantResourceOwnerPasswordCredentials();

		if (fields != null)
		{
			builder = builder.queryParam("fields", fields);
		}

		final Response result = builder.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.get();

		result.bufferEntity();
		return result;
	}

	protected CatalogPermissionsListWsDTO retrieveCatalogVersionPermissions(final String principal, final String user,
			final String catalogIds, final String catalogVersions) throws JAXBException
	{
		final Response response = retrieveCatalogVersionPermissionsResponse(principal, user, catalogIds, catalogVersions);
		WebservicesAssert.assertResponse(Status.OK, response);

		final CatalogPermissionsListWsDTO entity = unmarshallResult(response, CatalogPermissionsListWsDTO.class);
		return entity;
	}


	protected Response retrieveCatalogVersionPermissionsResponse(final String principal, final String user,
			final String catalogIds, final String catalogVersions)
	{
		final WsSecuredRequestBuilder builder = wsSecuredRequestBuilder//
				.path("permissions")//
				.path("principals")//
				.path(principal)//
				.path("catalogs")//
				.queryParam("catalogId", catalogIds)//
				.queryParam("catalogVersion", catalogVersions)//
				.resourceOwner(user, PASSWORD)//
				.grantResourceOwnerPasswordCredentials();


		final Response result = builder.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.get();

		result.bufferEntity();
		return result;
	}

	protected PermissionsWsDTO generateExpectedPermissionsWsDTO(final String id, final Map<String, String> permissions)
	{
		final PermissionsWsDTO permissionsWsDTO = new PermissionsWsDTO();
		permissionsWsDTO.setId(id);
		permissionsWsDTO.setPermissions(permissions);
		return permissionsWsDTO;
	}

	protected CatalogPermissionsWsDTO generateExpectedCatalogPermissionsWsDTO(final String catalogId, final String catalogVersion,
			final Map<String, String> permissions)
	{
		final CatalogPermissionsWsDTO permissionsWsDTO = new CatalogPermissionsWsDTO();
		permissionsWsDTO.setCatalogId(catalogId);
		permissionsWsDTO.setCatalogVersion(catalogVersion);
		permissionsWsDTO.setPermissions(permissions);
		return permissionsWsDTO;
	}

	public static void assertEqualsPermissionsListWsDTO(final PermissionsListWsDTO expected, final PermissionsListWsDTO tested)
	{
		for (int i = 0; i < expected.getPermissionsList().size(); i++)
		{
			final PermissionsWsDTO expectedPermission = expected.getPermissionsList().get(i);
			final PermissionsWsDTO testedPermission = (PermissionsWsDTO) CollectionUtils.find(tested.getPermissionsList(),
					new Predicate()
					{
						@Override
						public boolean evaluate(final Object arg0)
						{
							final PermissionsWsDTO testedPermission = (PermissionsWsDTO) arg0;
							return testedPermission.getId().equals(expectedPermission.getId());
						}
					});

			Assert.assertNotNull(testedPermission);
			Assert.assertEquals(expectedPermission.getPermissions(), testedPermission.getPermissions());
		}
	}

	public static void assertEqualsPermissionsWsDTO(final PermissionsWsDTO expected, final PermissionsWsDTO tested)
	{
		Assert.assertEquals(expected.getId(), tested.getId());
		Assert.assertEquals(expected.getPermissions(), tested.getPermissions());
	}

	public static void assertEqualsCatalogPermissionsListWsDTO(final CatalogPermissionsListWsDTO expected,
			final CatalogPermissionsListWsDTO tested)
	{
		Assert.assertEquals(expected.getPermissionsList().size(), tested.getPermissionsList().size());
		expected.getPermissionsList().stream()
				.forEach(p -> Assert.assertTrue(tested.getPermissionsList().stream()//
						.filter(t -> areCatalogPermissionsWsDTOEquals(p, t))//
						.findAny()//
						.isPresent()));
	}

	public static boolean areCatalogPermissionsWsDTOEquals(final CatalogPermissionsWsDTO expected,
			final CatalogPermissionsWsDTO tested)
	{

		return expected.getCatalogId().equals(tested.getCatalogId())
				&& expected.getCatalogVersion().equals(tested.getCatalogVersion())
				&& expected.getPermissions().equals(tested.getPermissions());
	}

	protected <C> C unmarshallResult(final Response result, final Class<C> c) throws JAXBException
	{

		final Unmarshaller unmarshaller = defaultJsonHttpMessageConverter.createUnmarshaller(c);
		final StreamSource source = new StreamSource(result.readEntity(InputStream.class));
		final C entity = unmarshaller.unmarshal(source, c).getValue();
		return entity;
	}



}
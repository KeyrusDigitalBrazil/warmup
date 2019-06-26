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
package de.hybris.platform.permissionswebservices

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.oauth2.constants.OAuth2Constants
import de.hybris.platform.permissionswebservices.constants.PermissionswebservicesConstants
import de.hybris.platform.permissionswebservices.controllers.AbstractPermissionsWebServicesTest
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer
import org.junit.Assert
import org.junit.Before
import org.junit.Test

import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


@IntegrationTest
@NeedsEmbeddedServer(webExtensions =
        [PermissionswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME])
public class PermissionsWebServicesSecurityTest extends AbstractPermissionsWebServicesTest {
    static final String SUBGROUP2 = "subgroup2"
    private static final String NOT_EXISTING_SCOPE = "not_existing_scope";
    private static final String PERMISSIONSWEBSERVICES_SCOPE = "permissionswebservices";
    private static final String BASIC_SCOPE = "basic";

    WsSecuredRequestBuilder wsSecuredRequestBuilder

    @Before
    void setup() {
        wsSecuredRequestBuilder = new WsSecuredRequestBuilder()
                .extensionName(PermissionswebservicesConstants.EXTENSIONNAME)
                .path("v1")
                .client("mobile_android", "secret");
        importData(new ClasspathImpExResource("/permissionswebservices/test/testpermissions.impex", "UTF-8"));
        insertGlobalPermission(SUBGROUP2, "globalpermission1");
    }

    @Test
    public void shouldReturn401ForAdminWithNotExistingScope() throws IOException {

        //when posting with a not existing scope
        final Response result = wsSecuredRequestBuilder//
                .path("permissions")//
                .path("principals")//
                .path("admin")//
                .path("types")//
                .queryParam("types", "User,Order")//
                .queryParam("permissionNames", "read,change,create,remove,changerights")//
                .resourceOwner("admin", "nimda")//
                .scope(NOT_EXISTING_SCOPE)//
                .grantResourceOwnerPasswordCredentials()//
                .build()//
                .accept(MediaType.APPLICATION_JSON)//
                .get();

        //then we receive a 401
        Assert.assertEquals(401, result.getStatus());
    }

    @Test
    public void shouldReturn403ForAdminWithBasicScope() throws IOException {

        //when posting with a not existing scope
        final Response result = wsSecuredRequestBuilder//
                .path("permissions")//
                .path("principals")//
                .path("admin")//
                .path("types")//
                .queryParam("types", "User,Order")//
                .queryParam("permissionNames", "read,change,create,remove,changerights")//
                .resourceOwner("admin", "nimda")//
                .scope(BASIC_SCOPE)//
                .grantResourceOwnerPasswordCredentials()//
                .build()//
                .accept(MediaType.APPLICATION_JSON)//
                .get();

        //then we receive a 403
        Assert.assertEquals(403, result.getStatus());
    }

    @Test
    public void shouldReturn200ForAdminWithProperScope() throws IOException {

        //when posting with a with proper scope
        final Response result = wsSecuredRequestBuilder//
                .path("permissions")//
                .path("principals")//
                .path("admin")//
                .path("types")//
                .queryParam("types", "User,Order")//
                .queryParam("permissionNames", "read,change,create,remove,changerights")//
                .resourceOwner("admin", "nimda")//
                .scope(PERMISSIONSWEBSERVICES_SCOPE)//
                .grantResourceOwnerPasswordCredentials()//
                .build()//
                .accept(MediaType.APPLICATION_JSON)//
                .get();

        //then we receive a 200
        Assert.assertEquals(200, result.getStatus());
    }

    @Test
    public void shouldReturn200ForAdminWithNoSpecificScope() throws IOException {

        //when posting with no scope specified
        final Response result = wsSecuredRequestBuilder//
                .path("permissions")//
                .path("principals")//
                .path("admin")//
                .path("types")//
                .queryParam("types", "User,Order")//
                .queryParam("permissionNames", "read,change,create,remove,changerights")//
                .resourceOwner("admin", "nimda")//
                .grantResourceOwnerPasswordCredentials()//
                .build()//
                .accept(MediaType.APPLICATION_JSON)//
                .get();

        //then we receive a 200
        Assert.assertEquals(200, result.getStatus());
    }

}

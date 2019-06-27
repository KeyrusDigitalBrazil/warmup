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
package de.hybris.platform.apiregistryservices.factory.client;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.hybris.charon.annotations.Control;
import com.hybris.charon.annotations.Http;
import com.hybris.charon.annotations.OAuth;

import rx.Observable;


/**
 * Client reading information from yaas profile service.
 */

@OAuth
@Http
public interface ProfileDataServiceClient
{
    /**
     * Read profile data from yaas profile service.
     *
     * @param id
     *           Profile identifier
     * @return Profile data
     */
    @GET
    @Control(retries = "${retries:0}", retriesInterval = "${retriesInterval:500}", timeout = "${timeout:2000}")
    @Path("/${tenant}/profiles/{id}")
    Object getProfile(@PathParam("id") String id);

    /**
     * Read profile data from yaas profile service.
     *
     * @param id
     *           Profile identifier
     * @param fields
     *           List of fields which should be read from profile service separated by comma.<br/>
     *           Example : insights.affinities.products,insights.affinities.categories
     * @return Profile data
     */
    @GET
    @Control(retries = "${retries:0}", retriesInterval = "${retriesInterval:500}", timeout = "${timeout:2000}")
    @Path("/${tenant}/profiles/{id}?fields={fields}")
    Object getProfile(@PathParam("id") String id, @PathParam("fields") String fields);


    /**
     * Read profile data from yaas profile service. Method runs asynchronously.
     *
     * @param id
     *           Profile identifier
     * @return Profile data
     */
    @GET
    @Control(retries = "${retries:0}", retriesInterval = "${retriesInterval:500}", timeout = "${timeout:2000}")
    @Path("/${tenant}/profiles/{id}")
    Observable<Object> getProfileAsync(@PathParam("id") String id);

    /**
     * Read profile data from yaas profile service. Method runs asynchronously.
     *
     * @param id
     *           Profile identifier
     * @param fields
     *           List of fields which should be read from profile service separated by comma.<br/>
     *           Example : insights.affinities.products,insights.affinities.categories
     * @return Profile data
     */
    @GET
    @Control(retries = "${retries:0}", retriesInterval = "${retriesInterval:500}", timeout = "${timeout:2000}")
    @Path("/${tenant}/profiles/{id}?fields={fields}")
    Observable<Object> getProfileAsync(@PathParam("id") String id, @PathParam("fields") String fields);

}

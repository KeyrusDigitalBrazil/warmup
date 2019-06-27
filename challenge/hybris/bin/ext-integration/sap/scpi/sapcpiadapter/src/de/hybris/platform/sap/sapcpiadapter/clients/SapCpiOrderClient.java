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
package de.hybris.platform.sap.sapcpiadapter.clients;

import com.hybris.charon.annotations.Control;
import com.hybris.charon.annotations.Http;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrder;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrderCancellation;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiResponse;
import rx.Observable;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Http(value = "http")
@Control(retries = "${retries:3}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:10000}")
public interface SapCpiOrderClient {

    @POST
    @Path("${prefix}/ECP/S4HANA/HybrisOrder")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Observable<SapCpiResponse>  sendOrder(@HeaderParam("Authorization") String oauthToken, SapCpiOrder sapCpiOrder);

    @POST
    @Path("${prefix}/ECP/S4HANA/HybrisOrder")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Observable<SapCpiResponse>  sendOrderCancellation(@HeaderParam("Authorization") String oauthToken, SapCpiOrderCancellation sapCpiOrderCancellation);

}

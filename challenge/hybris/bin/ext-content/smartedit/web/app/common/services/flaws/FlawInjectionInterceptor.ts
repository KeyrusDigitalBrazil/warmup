import {SeInjectable} from "smarteditcommons/services/dependencyInjection/di";

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
type Predicate = (config: angular.IRequestConfig) => boolean;
type RequestHandler = (config: angular.IRequestConfig) => angular.IRequestConfig;
type ResponseHandler = <T>(response: angular.IHttpResponse<T>) => angular.IHttpResponse<T>;

/*
 * interceptor that will inject flaw into outbound and inbound http calls.
 * It is mainly used to validate reliability and consitency of test frameworks
 */
/** @internal */
@SeInjectable()
export class FlawInjectionInterceptor implements angular.IHttpInterceptor {

	/*
	 * probability of flaw occurrence ranging from 0 to 1
	 */
	private static PROBABILITY = 0;

	private flawWindow: any;

	private requestMutations: {test: Predicate, mutate: RequestHandler}[] = [];
	private responseMutations: {test: Predicate, mutate: ResponseHandler}[] = [];

	constructor(
		private $log: angular.ILogService,
		private interceptorHelper: any) {

		this.flawWindow = window;
		this.flawWindow.allRequests = 0;
		this.flawWindow.flawedRequests = 0;
		this.flawWindow.allResponses = 0;
		this.flawWindow.flawedResponses = 0;

		this.request = this.request.bind(this);
		this.response = this.response.bind(this);

	}

	registerRequestFlaw(mutation: {test: Predicate, mutate: RequestHandler}) {
		this.requestMutations.push(mutation);
	}

	registerResponseFlaw(mutation: {test: Predicate, mutate: ResponseHandler}) {
		this.responseMutations.push(mutation);
	}

	request(config: angular.IRequestConfig) {

		if (FlawInjectionInterceptor.PROBABILITY !== 0 && this._isCRUDRequest(config) && !this._isGET(config)) {
			return this.interceptorHelper.handleRequest(config, () => {

				this.flawWindow.allRequests++;
				if (this._activateWithProbability(FlawInjectionInterceptor.PROBABILITY)) {
					this.flawWindow.flawedRequests++;

					const requestMutation = this.requestMutations.find((mutation) => mutation.test(config));
					if (requestMutation) {
						config = requestMutation.mutate(config);
						this.$log.error(`FLAWED REQUEST-"${config.url}`);
					}

				}

				return config;
			});
		} else {
			return config;
		}
	}

	response(response: angular.IHttpResponse<any>) {
		if (FlawInjectionInterceptor.PROBABILITY !== 0 && this._isCRUDResponse(response) && !this._isGET(response.config)) {

			this.flawWindow.allResponses++;
			if (this._activateWithProbability(FlawInjectionInterceptor.PROBABILITY)) {
				this.flawWindow.flawedResponses++;

				const responseMutation = this.responseMutations.find((mutation) => mutation.test(response.config));
				if (responseMutation) {
					response = responseMutation.mutate(response);
					this.$log.error(`FLAWED RESPONSE-"${response.config.url}`);
				}
			}

			return response;
		} else {
			return response;
		}
	}

	private _isCRUDRequest(config: angular.IRequestConfig) {
		return config.url
			&& (config.url.indexOf(".html") === -1
				&& config.url.indexOf(".js") === -1
			);
	}

	private _isCRUDResponse(response: angular.IHttpResponse<any>) {
		return response.config && response.config.url
			&& response.config.url.indexOf(".html") === -1
			&& response.headers('Content-Type')
			&& response.headers('Content-Type').indexOf("json") > -1;
	}

	private _isGET(config: angular.IRequestConfig) {
		return config.method === "GET";
	}

	private _activateWithProbability(probabilityTrue: number) {
		return Math.random() >= 1.0 - probabilityTrue;
	}


}

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
import * as lo from 'lodash';
import {SeModule} from "smarteditcommons/services/dependencyInjection/di";
/** @internal */
function $exceptionHandler($log: angular.ILogService) {
	'ngInject';

	const ignorePatterns = [
		/^Possibly unhandled rejection/
	];

	const patternsForE2EErrorLogs = [
		/Unexpected request/, // missing http mock patterns in e2e
		/No more request expected/ // missing http mock patterns in e2e
	];
	return function exceptionHandler(exception: any, cause: any) {

        /*
         * original exception occuring in a promise based API won't show here
         * the catch set in decoration is necessary to log them
         */
		if (ignorePatterns.some((pattern) => pattern.test(exception))) {
			return;
		}

		if (patternsForE2EErrorLogs.some((pattern) => pattern.test(exception))) {
			$log.error(`E2E mock issue: ${exception}`);
			return;
		}

		$log.error(exception);
	};
}

function isAjaxError(error: any) {
	return error.hasOwnProperty("headers");
}
/*
 * Helper function used on all known promise based Angular 1.6 APIs
 * to handle promise rejection in an AOP fashion through Angular decorators
 */
function handlePromiseRejections(
	$q: angular.IQService,
	$log: angular.ILogService,
	lodash: lo.LoDashStatic,
	promise: angular.IPromise<any>): angular.IPromise<any> {

	const defaultFailureCallback = (error: any) => {
		if (undefined !== error && "canceled" !== error) {
			if (lodash.isPlainObject(error)) {
				if (!isAjaxError(error)) {
					$log.error(`exception caught in promise: ${JSON.stringify(error)}`);
				}
			} else if (!lodash.isBoolean(error)) {
				$log.error(error);
			}
		}
		return $q.reject(error);
	};

	const oldThen = promise.then;

	promise.then = function(successCallback, _failureCallback, notifyCallback) {
		const failureCallback = _failureCallback ? _failureCallback : defaultFailureCallback;
		return oldThen.call(this, successCallback, failureCallback, notifyCallback);
	};
	return promise;
}

/** @internal */
@SeModule({
	providers: [
		$exceptionHandler
	],
	/*
	 * Decoration all known promise based Angular 1.6 APIs
	 * to handle promise rejection in an AOP fashion
	 */
	config: ($qProvider: angular.IQProvider, $provide: angular.auto.IProvideService) => {
		'ngInject';

		$qProvider.errorOnUnhandledRejections(true);

		$provide.decorator('$q', (
			$delegate: angular.IQService,
			$log: angular.ILogService,
			lodash: lo.LoDashStatic
		) => {
			'ngInject';

			const originalWhen = $delegate.when;
			$delegate.when = function() {
				if (arguments[0] && !arguments[0].then) {
					return handlePromiseRejections($delegate, $log, lodash, originalWhen.apply(this, arguments));
				} else {
					return originalWhen.apply(this, arguments);
				}
			};

			const originalAll = $delegate.all;
			$delegate.all = function() {
				return handlePromiseRejections($delegate, $log, lodash, originalAll.apply($delegate, arguments));
			};

			const originalDefer = $delegate.defer;
			$delegate.defer = function(): angular.IDeferred<any> {
				const deferred = originalDefer.bind($delegate)();

				handlePromiseRejections($delegate, $log, lodash, deferred.promise);

				return deferred;
			};

			return $delegate;
		});

		$provide.decorator('$timeout', (
			$delegate: angular.ITimeoutService,
			$q: angular.IQService,
			$log: angular.ILogService,
			lodash: lo.LoDashStatic
		) => {
			'ngInject';

			const originalTimeout = $delegate;

			function wrappedTimeout() {
				return handlePromiseRejections($q, $log, lodash, originalTimeout.apply($delegate, arguments));
			}

			lodash.merge(wrappedTimeout, originalTimeout);

			return wrappedTimeout;
		});
	}
})
export class ConfigModule {}

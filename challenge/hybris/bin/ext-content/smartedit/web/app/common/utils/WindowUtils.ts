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
import {SeInjectable} from 'smarteditcommons/services/dependencyInjection/di';
import * as angular from "angular";

/**
 * @ngdoc service
 * @name functionsModule.service:WindowUtils
 *
 * @description
 * A collection of utility methods for windows.
 */
@SeInjectable()
export class WindowUtils {

	constructor(
		private isIframe: any,
		private SMARTEDIT_IFRAME_ID: string,
		private $window: angular.IWindowService,
	) {}

    /**
     * @ngdoc method
     * @name functionsModule.service:WindowUtils#getTargetIFrame
     * @methodOf functionsModule.service:WindowUtils
     *
     * @description
     * Retrieves the iframe from the inner or outer app.
     *
     * @returns {Window} The content window or null if it does not exists.
     */
	getTargetIFrame(): Window {
		if (this.isIframe()) {
			return this.$window.parent;
		} else if (this.$window.document.getElementById(this.SMARTEDIT_IFRAME_ID)) {
			return (this.$window.document.getElementById(this.SMARTEDIT_IFRAME_ID) as HTMLIFrameElement).contentWindow;
		}
		return null;
	}

}
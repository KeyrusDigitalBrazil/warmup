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
import {SeModule} from "smarteditcommons";

import {GenericEditorBreadcrumbComponent} from "./GenericEditorBreadcrumbComponent";
import {GenericEditorServicesModule} from "smarteditcommons/components/genericEditor/services/GenericEditorServicesModule";

/**
 * @ngdoc overview
 * @name genericEditorBreadcrumbModule
 *
 * @description
 * This module provides the genericEditorBreadcrumbModule component, which is used to show a breadcrumb on top of the generic editor
 * when there is more than one editor opened on top of each other. This will happen when editing nested components.
 */
@SeModule({
	imports: [
		GenericEditorServicesModule
	],
	declarations: [
		GenericEditorBreadcrumbComponent
	]
})
export class GenericEditorBreadcrumbModule {}
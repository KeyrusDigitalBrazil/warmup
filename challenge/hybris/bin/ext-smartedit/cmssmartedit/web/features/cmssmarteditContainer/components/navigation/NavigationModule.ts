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
import {SeModule, SeValueProvider} from "smarteditcommons";
import {NavigationNodeEditorModalService} from "./navigationNodeEditor/NavigationNodeEditorModalService";
import {NavigationEditorTreeComponent} from "./navigationEditor/NavigationEditorTreeComponent";

/* @internal */
const NAVIGATION_NODE_ROOT_NODE_UID: SeValueProvider = {
	provide: 'NAVIGATION_NODE_ROOT_NODE_UID',
	useValue: 'root'
};

/**
 * @ngdoc object
 * @name NavigationModule.object:NAVIGATION_NODE_TYPECODE
 * @description
 * A constant defining the CMS Navigation Node's typecode.
 */
export const NAVIGATION_NODE_TYPECODE: SeValueProvider = {
	provide: 'NAVIGATION_NODE_TYPECODE',
	useValue: 'CMSNavigationNode'
};

/**
 * @ngdoc overview
 * @name NavigationModule
 * @description
 *
 * The navigation node editor modal service module provides a service that allows opening an editor modal for a given navigation node or an entry. The editor modal is populated with a save and cancel button, and is loaded with the
 * editorTabset of cmssmarteditContainer as its content, providing a way to edit
 * various fields of the given navigation node.
 */
@SeModule({
	imports: [
		'smarteditServicesModule',
		'editorModalServiceModule',
		'genericEditorModalServiceModule',
		'functionsModule',
		'navigationModule',
		'navigationEditorNodeServiceModule',
		'resourceLocationsModule',
		'confirmationModalServiceModule',
		'resourceModule',
		'yLoDashModule'
	],
	declarations: [
		NavigationEditorTreeComponent
	],
	providers: [
		NAVIGATION_NODE_TYPECODE,
		NAVIGATION_NODE_ROOT_NODE_UID,
		NavigationNodeEditorModalService,
	]
})
export class NavigationModule {}
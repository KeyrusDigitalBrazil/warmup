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
import {IDragAndDropEvents, PolyfillService, SeInjectable, SystemEventService, TypedMap} from "smarteditcommons";
import * as lo from "lodash";
import {ISakExecutorDirectiveScope} from "./SmarteditElementComponent";

/* @internal */
@SeInjectable()
export class SakExecutorService {

	private ATTR_DATA = 'data-';
	private ATTR_SMARTEDIT = 'smartedit';
	private ATTR_DATA_SMARTEDIT = this.ATTR_DATA + this.ATTR_SMARTEDIT;
	private componentDecoratorEnabled = true;

	private scopes: {scope: ISakExecutorDirectiveScope, element: angular.IAugmentedJQuery}[] = [];
	private decoratorsCondition: string;

	constructor(
		private $log: angular.ILogService,
		private $compile: angular.ICompileService,
		private yjQuery: JQueryStatic,
		private lodash: lo.LoDashStatic,
		private polyfillService: PolyfillService,
		private decoratorService: any,
		private systemEventService: SystemEventService,
		private ID_ATTRIBUTE: string,
		private ELEMENT_UUID_ATTRIBUTE: string,
		private SMARTEDIT_DRAG_AND_DROP_EVENTS: IDragAndDropEvents
	) {
		// we can't listen to these events in the controller because they could be sent before the component compilation.
		this.systemEventService.subscribe(this.SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_END, () => {
			this.componentDecoratorEnabled = true;
			if (this.polyfillService.isEligibleForEconomyMode()) {
				this.scopes.forEach((scope) => {
					scope.scope.componentDecoratorEnabled = true;
				});
			}
		});

		this.systemEventService.subscribe(this.SMARTEDIT_DRAG_AND_DROP_EVENTS.DRAG_DROP_START, (eventId: string, smarteditComponentClosestToDraggedElement: angular.IAugmentedJQuery) => {
			this.componentDecoratorEnabled = false;
			if (this.polyfillService.isEligibleForEconomyMode()) {
				const itemIndex = smarteditComponentClosestToDraggedElement ? this._getElementIndex(smarteditComponentClosestToDraggedElement) : -1;
				this.scopes.forEach((scope, index: number) => {
					if (itemIndex === -1 || itemIndex !== index) {
						scope.scope.componentDecoratorEnabled = false;
					}
				});
			}
		});

		this.decoratorsCondition = this.polyfillService.isEligibleForEconomyMode() ? "data-ng-if='componentDecoratorEnabled'" : "";
	}

	_getElementIndex(element: angular.IAugmentedJQuery) {
		return this.scopes.findIndex((item) => {
			return this.yjQuery(item.element).attr(this.ELEMENT_UUID_ATTRIBUTE) === this.yjQuery(element).attr(this.ELEMENT_UUID_ATTRIBUTE);
		});
	}

	getScopes() {
		return this.scopes;
	}

	wrapDecorators(transcludeFn: angular.ITranscludeFunction, smarteditComponentId: string, smarteditComponentType: string): angular.IPromise<angular.ITemplateLinkingFunction> {
		return this.decoratorService.getDecoratorsForComponent(smarteditComponentType, smarteditComponentId).then((decorators: string[]) => {

			let template = "<div " + this.decoratorsCondition + " data-ng-transclude></div>";

			decorators.forEach((decorator: string) => {
				template = "<div " + this.decoratorsCondition + " class='" + decorator + " se-decorator-wrap' data-active='active' data-smartedit-component-id='{{$ctrl.smarteditComponentId}}' " +
					"data-smartedit-component-type='{{$ctrl.smarteditComponentType}}' data-smartedit-container-id='{{$ctrl.smarteditContainerId}}' " +
					"data-smartedit-container-type='{{$ctrl.smarteditContainerType}}' data-component-attributes='componentAttributes'>" + template;
				template += "</div>";
			});

			if (this.polyfillService.isEligibleForEconomyMode()) {
				template = "<div>" + template + "<div data-ng-if='!componentDecoratorEnabled' data-ng-transclude></div></div>";
			}

			return this.$compile(template, transcludeFn);
		});
	}

	registerScope(scope: ISakExecutorDirectiveScope, element: angular.IAugmentedJQuery) {
		this.scopes.push({
			scope,
			element: {...element}
		});
	}

	destroyScope(element: angular.IAugmentedJQuery) {
		const itemIndex = this._getElementIndex(element);
		if (itemIndex !== -1) {
			this.scopes[itemIndex].scope.$destroy();
			this.scopes.splice(itemIndex, 1);
		} else {
			this.$log.warn('sakExecutor::destroyScope failed to retrieve element:', this.yjQuery(element).attr(this.ID_ATTRIBUTE), this.yjQuery(element).attr(this.ELEMENT_UUID_ATTRIBUTE));
		}
	}

	prepareScope(scope: ISakExecutorDirectiveScope, element: angular.IAugmentedJQuery) {
		this.registerScope(scope, element);
		const attributes: TypedMap<string> = {};
		Array.prototype.slice.apply(element.get(0).attributes).forEach((node: TypedMap<string>) => {
			let attrName = node.nodeName;
			if (this.isValidSmartEditAttribute(attrName)) {
				attrName = this.parseAttributeName(attrName);
				attributes[attrName] = node.nodeValue;
			}
		});

		scope.componentAttributes = attributes;
	}

	isDecoratorEnabled() {
		return this.componentDecoratorEnabled;
	}

	/*
	 * Validates if a given attribute name present on the decorated element is eligible
	 * to be added as a smartedit property.
	 */
	private isValidSmartEditAttribute(nodeName: string) {
		return this.lodash.startsWith(nodeName, this.ATTR_DATA_SMARTEDIT) || this.lodash.startsWith(nodeName, this.ATTR_SMARTEDIT);
	}

	/*
	 * Parses the attribute name by removing this.ATTR_DATA prefix and
	 * converting to a camel case string representation.
	 */
	private parseAttributeName(nodeName: string) {
		if (this.lodash.startsWith(nodeName, this.ATTR_DATA)) {
			nodeName = nodeName.substring(this.ATTR_DATA.length);
		}
		return this.lodash.camelCase(nodeName);
	}

}
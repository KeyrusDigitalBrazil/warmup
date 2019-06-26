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
import * as angular from 'angular';

/* @ngInject */
export class TriggerService {

	public DEFAULT_TRIGGER = 'defaultTriggerData';
	public SEGMENT_TRIGGER = 'segmentTriggerData';
	public EXPRESSION_TRIGGER = 'expressionTriggerData';
	public supportedTypes = [this.DEFAULT_TRIGGER, this.SEGMENT_TRIGGER, this.EXPRESSION_TRIGGER];
	public actions = [{
		id: 'AND',
		name: 'personalization.modal.customizationvariationmanagement.targetgrouptab.expression.and'
	}, {
		id: 'OR',
		name: 'personalization.modal.customizationvariationmanagement.targetgrouptab.expression.or'
	}, {
		id: 'NOT',
		name: 'personalization.modal.customizationvariationmanagement.targetgrouptab.expression.not'
	}];

	private GROUP_EXPRESSION = 'groupExpressionData';
	private SEGMENT_EXPRESSION = 'segmentExpressionData';
	private NEGATION_EXPRESSION = 'negationExpressionData';

	private CONTAINER_TYPE = 'container';
	private ITEM_TYPE = 'item';
	private DROPZONE_TYPE = 'dropzone';

	isContainer(element: any) {
		return this.isElementOfType(element, this.CONTAINER_TYPE);
	}

	isEmptyContainer(element: any): boolean {
		return this.isContainer(element) && element.nodes.length === 0;
	}

	isNotEmptyContainer(element: any): boolean {
		return this.isContainer(element) && element.nodes.length > 0;
	}

	isDropzone(element: any): boolean {
		return this.isElementOfType(element, this.DROPZONE_TYPE);
	}

	isItem(element: any): boolean {
		return this.isElementOfType(element, this.ITEM_TYPE);
	}

	isValidExpression(element: any): boolean {
		if (!element) {
			return false;
		}
		if (this.isContainer(element)) {
			return element.nodes && element.nodes.length > 0 &&
				element.nodes.every((node: any) => {
					return this.isValidExpression(node);
				});
		} else {
			return angular.isDefined(element.selectedSegment);
		}
	}

	buildTriggers(form: any, existingTriggers: any): any {
		let trigger = {};
		form = form || {};

		if (this.isDefaultData(form)) {
			trigger = this.buildDefaultTrigger();
		} else if (form.expression && form.expression.length > 0) {
			const element = form.expression[0];

			if (this.isDropzone(element.nodes[0])) {
				trigger = {};
			} else if (this.isExpressionData(element)) {
				trigger = this.buildExpressionTrigger(element);
			} else {
				trigger = this.buildSegmentTrigger(element);
			}
		}

		return this.mergeTriggers(existingTriggers, trigger);
	}

	buildData(triggers: any): any {
		let trigger = {};
		let data = this.getBaseData();
		if (triggers && triggers.length > 0) {
			trigger = triggers.filter((elem: any) => {
				return this.isSupportedTrigger(elem);
			})[0];
		}
		if (this.isDefaultTrigger(trigger)) { // jshint ignore:line
			// nothing to do here
			// we leave baseData - it will be used if user removes default trigger
		} else if (this.isExpressionTrigger(trigger)) {
			data = this.buildExpressionTriggerData(trigger);
		} else if (this.isSegmentTrigger(trigger)) {
			data = this.buildSegmentTriggerData(trigger);
		}
		return data;
	}

	isDefault(triggers: any): boolean {
		const defaultTrigger = (triggers || []).filter((elem: any) => {
			return this.isDefaultTrigger(elem);
		})[0];
		return (triggers && defaultTrigger) ? true : false;
	}

	getExpressionAsString(expressionContainer: any): string {
		let retStr = "";
		if (expressionContainer === undefined) {
			return retStr;
		}

		const currOperator = this.isNegation(expressionContainer) ? "AND" : expressionContainer.operation.id;
		retStr += this.isNegation(expressionContainer) ? " NOT " : "";
		retStr += "(";

		expressionContainer.nodes.forEach((element: any, index: number) => {
			if (this.isDropzone(element)) {
				retStr += " [] ";
			} else {
				retStr += (index > 0) ? " " + currOperator + " " : "";
				retStr += this.isItem(element) ? element.selectedSegment.code : this.getExpressionAsString(element);
			}
		});

		retStr += ")";

		return retStr;
	}

	private isElementOfType(element: any, myType: any) {
		return angular.isDefined(element) ? (element.type === myType) : false;
	}

	private isNegation(element: any): boolean {
		return this.isContainer(element) && element.operation.id === 'NOT';
	}

	private isDefaultData(form: any): boolean {
		return form.isDefault;
	}

	private isExpressionData(element: any): boolean {
		return element.operation.id === 'NOT' || element.nodes.some((item: any) => {
			return !this.isItem(item);
		});
	}

	private isSupportedTrigger(trigger: any): boolean {
		return this.supportedTypes.indexOf(trigger.type) >= 0;
	}

	private isDefaultTrigger(trigger: any): boolean {
		return this.isElementOfType(trigger, this.DEFAULT_TRIGGER);
	}

	private isSegmentTrigger(trigger: any): boolean {
		return this.isElementOfType(trigger, this.SEGMENT_TRIGGER);
	}

	private isExpressionTrigger(trigger: any): boolean {
		return this.isElementOfType(trigger, this.EXPRESSION_TRIGGER);
	}

	private isGroupExpressionData(expression: any): boolean {
		return this.isElementOfType(expression, this.GROUP_EXPRESSION);
	}

	private isSegmentExpressionData(expression: any): boolean {
		return this.isElementOfType(expression, this.SEGMENT_EXPRESSION);
	}

	private isNegationExpressionData(expression: any): boolean {
		return this.isElementOfType(expression, this.NEGATION_EXPRESSION);
	}

	// ------------------------ FORM DATA -> TRIGGER ---------------------------

	private buildSegmentsForTrigger(element: any): any {
		return element.nodes.filter((node: any) => {
			return this.isItem(node);
		}).map((item: any) => {
			return item.selectedSegment;
		});
	}

	private buildExpressionForTrigger(element: any): any {
		if (this.isNegation(element)) {
			const negationElements: any = [];
			element.nodes.forEach((elem: any) => {
				negationElements.push(this.buildExpressionForTrigger(elem));
			});
			return {
				type: this.NEGATION_EXPRESSION,
				element: {
					type: this.GROUP_EXPRESSION,
					operator: 'AND',
					elements: negationElements
				}
			};
		} else if (this.isContainer(element)) {
			const groupElements: any = [];
			element.nodes.forEach((elem: any) => {
				groupElements.push(this.buildExpressionForTrigger(elem));
			});
			return {
				type: this.GROUP_EXPRESSION,
				operator: element.operation.id,
				elements: groupElements
			};
		} else {
			return {
				type: this.SEGMENT_EXPRESSION,
				code: element.selectedSegment.code
			};
		}
	}

	private buildDefaultTrigger(): any {
		return {
			type: this.DEFAULT_TRIGGER
		};
	}

	private buildExpressionTrigger(element: any): any {
		return {
			type: this.EXPRESSION_TRIGGER,
			expression: this.buildExpressionForTrigger(element)
		};
	}

	private buildSegmentTrigger(element: any): any {
		return {
			type: this.SEGMENT_TRIGGER,
			groupBy: element.operation.id,
			segments: this.buildSegmentsForTrigger(element)
		};
	}

	private mergeTriggers(triggers: any, trigger: any): any {
		if (!angular.isDefined(triggers)) {
			return [trigger];
		}

		const index = triggers.findIndex((t: any) => {
			return t.type === trigger.type;
		});
		if (index >= 0) {
			trigger.code = triggers[index].code;
		}

		// remove other instanced of supported types (there can be only one) but maintain unsupported types
		const result = triggers.filter((t: any) => {
			return !this.isSupportedTrigger(t);
		});
		if (!angular.equals(trigger, {})) {
			result.push(trigger);
		}
		return result;
	}

	// ------------------------ TRIGGER -> FORM DATA ---------------------------

	private buildContainer(actionId: any): any {
		const action = this.actions.filter((a: any) => {
			return a.id === actionId;
		})[0];
		return {
			type: this.CONTAINER_TYPE,
			operation: action,
			nodes: []
		};
	}

	private buildItem(value: any): any {
		return {
			type: this.ITEM_TYPE,
			operation: '',
			selectedSegment: {
				code: value
			},
			nodes: []
		};
	}

	private getBaseData(): any {
		const data = this.buildContainer('AND');
		return [data];
	}

	private buildExpressionFromTrigger(expression: any): any {
		let data;
		if (this.isGroupExpressionData(expression)) {
			data = this.buildContainer(expression.operator);
			data.nodes = expression.elements.map((item: any) => {
				return this.buildExpressionFromTrigger(item);
			});
		} else if (this.isNegationExpressionData(expression)) {
			data = this.buildContainer('NOT');
			const element = this.buildExpressionFromTrigger(expression.element);

			if (this.isGroupExpressionData(expression.element) && expression.element.operator === 'AND') {
				data.nodes = element.nodes;
			} else {
				data.nodes.push(element);
			}
		} else if (this.isSegmentExpressionData(expression)) {
			data = this.buildItem(expression.code);
		}
		return data;
	}

	private buildSegmentTriggerData(trigger: any): any {
		const data = this.buildContainer(trigger.groupBy);

		trigger.segments.forEach((segment: any) => {
			data.nodes.push(this.buildItem(segment.code));
		});
		return [data];
	}

	private buildExpressionTriggerData(trigger: any): any {
		const data = this.buildExpressionFromTrigger(trigger.expression);
		return [data];
	}

}

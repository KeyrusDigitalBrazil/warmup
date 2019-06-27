import {SeDirective} from "smarteditcommons/services/dependencyInjection/SeDirective";

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
/**
 * @ngdoc directive
 * @name smarteditCommonsModule.directive:compileHtml
 * @scope
 * @restrict A
 * @attribute compile-html
 *
 * @description
 * Directive responsible for evaluating and compiling HTML markup.
 *
 * @param {String} String HTML string to be evaluated and compiled in the parent scope.
 * @example
 * <pre>
 *      <div compile-html="<a data-ng-click=\"injectedContext.onLink( item.path )\">{{ item[key.property] }}</a>"></div>
 * </pre>
 */

@SeDirective({
	selector: '[compile-html]'
})
export class CompileHtmlDirective {

	constructor(
		private $compile: angular.ICompileService,
		private $scope: angular.IScope,
		private $element: JQuery<HTMLElement>,
		private $attrs: angular.IAttributes
	) {
	}

	$postLink() {
		this.$scope.$parent.$watch(
			(scope) => scope.$eval(this.$attrs.compileHtml),
			(value) => {
				this.$element.html(value);
				this.$compile(this.$element.contents() as any)(this.$scope.$parent);
			}
		);
	}
}
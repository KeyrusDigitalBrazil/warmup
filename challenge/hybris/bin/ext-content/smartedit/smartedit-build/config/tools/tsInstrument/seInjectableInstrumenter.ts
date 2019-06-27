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
import {readFileSync, writeFileSync} from "fs";
import * as ts from "typescript";
import * as lodash from 'lodash';

/**
 * @SeInjectabe TypeScript instrumenter
 * This module visit the TypeScript source code to search for DI-specific class decorators like @SeInjectable() and @SeComponent;
 * It will instrument the class to as to be usable by Smartedit DI and, more precisely, by underlying
 * AngularJS 1.6 DI 
 * 
 */
module.exports = function(fileNames: string[], injectableDecorators: string[]) {

	const DELEGATING_HINT = "/* @ngInject */";

	const program = ts.createProgram(fileNames, {
		target: ts.ScriptTarget.ES5, module: ts.ModuleKind.CommonJS
	});
	let checker = program.getTypeChecker(); // do not remove, it's necessary to have type check
	for (const sourceFile of program.getSourceFiles()) {
		ts.forEachChild(sourceFile, walkNode);
	}

	function walkNode(node: ts.Node) {
		if (!isNodeExported(node)) {
			return;
		}

		if (node.kind === ts.SyntaxKind.ClassDeclaration && ts.isClassDeclaration(node)) {

			const classDeclaration = node as ts.ClassDeclaration;

			if (classDeclaration.decorators && classDeclaration.decorators.length) {
				const classDecorators = classDeclaration.decorators.map(serializeDecorator);
				if (lodash.intersection(classDecorators, injectableDecorators).length) {
					const fileName: string = node.getSourceFile().fileName;
					const className: string = node.name.getFullText().trim();
					writeFileSync(fileName, getTransformedSource(fileName, className));
				}
			}
		} else if (node.kind === ts.SyntaxKind.ModuleDeclaration) {
			// This is a namespace, visit its children
			ts.forEachChild(node, walkNode);
		}
	}

	function serializeDecorator(decorator: ts.Decorator) {
		return checker.getSymbolAtLocation(decorator.expression.getFirstToken()).getName();
	}

	function getTransformedSource(fileName: string, className: string): string {
		const fullText: string = readFileSync(fileName).toString();
		return fullText.replace(new RegExp("(export class " + className + ")", 'g'), DELEGATING_HINT + "\n $1");
	}

	function isNodeExported(node: ts.Node): boolean {
		return (ts.getCombinedModifierFlags(node) & ts.ModifierFlags.Export) !== 0 || (!!node.parent && node.parent.kind === ts.SyntaxKind.SourceFile);
	}

};

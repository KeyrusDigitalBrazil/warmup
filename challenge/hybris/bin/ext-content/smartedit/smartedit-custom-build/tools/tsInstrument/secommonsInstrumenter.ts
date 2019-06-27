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
import * as utils from "tsutils";

/**
 * smarteditcommons TypeScript instrumenter
 * This module visit the TypeScript source code to search for the StringLiteral: 'se:smarteditcommons';
 * Note: tslint rule "no-unused-expression" must be set to false. If it's set to true, we can create a custom TypeScript tslint rule similar to: https://github.com/kwonoj/tslint-no-unused-expression-chai/blob/master/src/noUnusedExpressionChaiRule.ts
 * The elligible code next to the "se:smarteditcommons" comment line will be automatically added to the "smarteditcommons" global (window) namespace.
 * The smarteditcommons namespace is used by other extensions in run-time, and it is NOT used by SmartEdit extension itself.
 * 
 * @example
 * 
 * Given a TypeScript file with one StringLiteral 'se:smarteditcommons';
 * ```ts
 * 'se:smarteditcommons';
 * export const Cached = annotationService.getMethodAnnotationFactory(...) => MethodDecorator;
 * ```
 * 
 * The instrumented code will be the following:
 * ```ts
 * 'se:smarteditcommons';
 * export const Cached = annotationService.getMethodAnnotationFactory(...) => MethodDecorator;
 * (window as any).smarteditcommons = (window as any).smarteditcommons ? (window as any).smarteditcommons : {};
 * (window as any).smarteditcommons.Cached = Cached;
 * ```
 * 
 * Technical notes:
 * The TypeScript Compiler API (see https://github.com/Microsoft/TypeScript/wiki/Using-the-Compiler-API) give us two ways of walking through TypeScript code:
 * 1- Use ts.createProgram(fileNames: string[]) and program.getTypeChecker()
 * 		-> This solution give us the AST and Type Check.
 * 2- Use ts.createSourceFile(fileName: string)
 * 		-> This solution give us the AST only, the Type Check is NOT available.
 * 
 * We must use the solution 1. (ts.createProgram) because we must have access to TypeCheck to discover the nodes types.
 * 
 * This module is not tight to webpack but is a pre-requisite to webpack: it will instrument directly the fileNames passed to module.exports.
 * Another approach could be to use a webpack loader to instrument the .ts files one by one, but in this case if we use ts.createProgram(), there is a huge performance impact (instrumentation takes too much time).
 */
module.exports = function(fileNames: string[]) {
	const SMARTEDIT_COMMONS_REGEX: RegExp = /'se(\s?):(\s?)smarteditcommons'/g;
	const SMARTEDIT_COMMONS_VARIABLE_NAME: string = 'smarteditcommons';
	const SMARTEDIT_COMMONS_DECLARATION: string = `\n(window as any).${SMARTEDIT_COMMONS_VARIABLE_NAME} = (window as any).smarteditcommons ? (window as any).smarteditcommons : {};`;
	const DEBUG: boolean = false;

	const program = ts.createProgram(fileNames, {
		target: ts.ScriptTarget.ES5, module: ts.ModuleKind.CommonJS
	});
	program.getTypeChecker(); // do not remove, it's necessary to have type check
	for (const sourceFile of program.getSourceFiles()) {
		utils.forEachTokenWithTrivia(sourceFile, visit);
	}
	
	function visit(fullText: string, kind: ts.SyntaxKind, range: ts.TextRange, parent: ts.Node) {
		if (hasSmarteditCommonsStringLiteral(parent.getFullText())) {
			if (fullText.indexOf(SMARTEDIT_COMMONS_DECLARATION) === -1) {
				writeFileSync(parent.getSourceFile().fileName, fullText + SMARTEDIT_COMMONS_DECLARATION);
			}
			ts.forEachChild(parent, walkNode);
		}
	}

	function hasSmarteditCommonsStringLiteral(fullText: string): boolean {
		return SMARTEDIT_COMMONS_REGEX.test(fullText);
	}

	function walkNode(node: ts.Node) {
		if (!isNodeExported(node)) {
            return;
		}
		const fileName: string = node.getSourceFile().fileName;
		if (DEBUG) {
			const { line, character } = node.getSourceFile().getLineAndCharacterOfPosition(node.getStart());
			console.log(fileName, line + 1, character, node.kind);
		}
		if (node.kind === ts.SyntaxKind.VariableDeclaration && ts.isVariableDeclaration(node) && node.name) {
			const variableName: string = node.name.getFullText().trim();
			DEBUG && console.log(`variable:${variableName}`);
			writeFileSync(fileName, getTransformedSource(fileName, variableName));
			return;
		} else if (node.kind === ts.SyntaxKind.FunctionDeclaration && ts.isFunctionDeclaration(node) && node.name) {
			const functionName: string = node.name.getFullText().trim();
			DEBUG && console.log(`function:${functionName}`);
			writeFileSync(fileName, getTransformedSource(fileName, functionName));
			return;
		}
		ts.forEachChild(node, walkNode);
	}

	function isNodeExported(node: ts.Node): boolean {
        return (ts.getCombinedModifierFlags(node) & ts.ModifierFlags.Export) !== 0 || (!!node.parent && node.parent.kind === ts.SyntaxKind.SourceFile);
    }

	function getTransformedSource(fileName: string, identifier: string): string {
		const addExternalCode: string = `\n(window as any).${SMARTEDIT_COMMONS_VARIABLE_NAME}.${identifier} = ${identifier};`;
		const fullText: string = readFileSync(fileName).toString(); // read file in case multiple annotations in same file
		return fullText.indexOf(addExternalCode) !== -1 ? fullText : (fullText + addExternalCode);
	}
};
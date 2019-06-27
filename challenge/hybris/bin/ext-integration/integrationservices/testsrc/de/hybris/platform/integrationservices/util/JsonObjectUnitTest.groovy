/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.integrationservices.util

import com.jayway.jsonpath.PathNotFoundException
import de.hybris.bootstrap.annotations.UnitTest
import org.apache.commons.io.IOUtils
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class JsonObjectUnitTest extends Specification
{
	@Test
	@Unroll
	def "Creates JsonObject from #desc"() throws IOException
	{
		expect:
		json // != null

		where:
		desc                | json
		"a valid stream"    | JsonObject.createFrom(asStream('{ "code": "red" }'))
		"an empty stream"   | JsonObject.createFrom(asStream(''))
		"a non-json stream" | JsonObject.createFrom(asStream('ha-ha!'))
		"a valid content"   | JsonObject.createFrom('{ "code": "green" }')
		"a non-json content"| JsonObject.createFrom("ha-ha!")
	}

	@Test
	def "Creates JsonObject from closed stream"() throws IOException
	{
		given:
		InputStream stream = asStream '{ "code": "red" }'
		stream.close()

		expect:
		JsonObject.createFrom(stream)
	}

	@Test
    @Unroll
	def "getString('#path') returns '#expected'"()
	{
        given:
        def json = JsonObject.createFrom('{ "string": "Donald", "boolean": true, "number": 5, "array": [] }')

        expect:
		json.getString(path) == expected

        where:
        path           | expected
        'string'       | "Donald"
        'boolean'      | "true"
        'number'       | "5"
        'array'        | '[]'
        'non-existent' | null
	}

	@Test
    @Unroll
	def "getBoolean() returns #res for '#value' value"()
	{
        given:
		JsonObject json = JsonObject.createFrom("{ 'enabled': $value }")

        expect:
		json.getBoolean("enabled") == res

        where:
        value  | res
        'true' | true
        'TRUE' | true
        'yes'  | false
        'false'| false
        100    | false
        true   | true
        false  | false
        null   | false
	}

    @Test
    @Unroll
    def "exists() is #res for #desc path"(){
        given:
        JsonObject json = JsonObject.createFrom("{ games: [{ 'seq': 1, 'players': ['Fred', 'Vera']}, {'seq': 2, 'players': []}] }")

        expect:
        json.exists(path) == res

        where:
        desc                         | path                            | res
        'existing'                   | 'games'                         | true
		'not existing'               | 'competitions'                  | false
		'existing array element'     | 'games[0].players[1]'           | true
		'not existing array element' | 'games[2]'                      | false
		'matching'                   | 'games[?(@.seq == 1)].players'  | true
		'not matching'               | 'games[?(@.seq == 3)]'          | false
		'empty array'                | 'games[1].players'              | false

    }

    @Test
    @Unroll
    def "getObject() returns #result for #condition value"()
    {
        given:
        JsonObject json = JsonObject.createFrom('{\n' +
                '  "books": [\n' +
                '    {\n' +
                '      "id": 1,\n' +
                '      "title": "Clean Code",\n' +
                '      "authors": [ "Robert C. Martin" ],\n' +
                '      "available": true\n' +
                '    },\n' +
                '    {\n' +
                '      "id": 2,\n' +
                '      "title": "ActiveMQ in Action",\n' +
                '      "authors": ["Bruce Snyder", "Dejan Bosanac", "Rob Davies"],\n' +
                '      "available": false,\n' +
                '      "holds": {\n' +
                '        "results": [\n' +
                '          {\n' +
                '            "name": "John Doe",\n' +
                '            "date": "09/01/2018"\n' +
                '          }\n' +
                '        ]\n' +
                '      }\n' +
                '    }\n' +
                '  ]\n' +
                '}')

        expect:
        json.getObject(path) == result

        where:
        condition                  | path                        | result
        'numeric'                  | '\$.books[1].id'            | 2
        'string'                   | 'books[0].title'            | 'Clean Code'
        'boolean'                  | 'books[0].available'        | true
        'Object'                   | 'books[1].holds.results[0]' | ['name':'John Doe', 'date':'09/01/2018']
        'List<Map<String, Object>>'| 'books[1].holds.results'    | [['name':'John Doe', 'date':'09/01/2018']]
        'conditional element'      | 'books[?(@.id==1)].title'   | ['Clean Code']
        'array'                    | 'books[1].authors'          | ["Bruce Snyder", "Dejan Bosanac", "Rob Davies"]
        'cross elements selection' | 'books[*].title'            | ["Clean Code", 'ActiveMQ in Action']
        'non existing path'        | 'books[0].holds'            | null
    }

    @Test
    @Unroll
    def "getCollectionOfObjects() returns #result for '#path' path in #json"()
    {
        given:
        JsonObject obj = JsonObject.createFrom json

        expect:
        obj.getCollectionOfObjects(path) == result

        where:
        json                                  | path                | result
        '{"numbers": [1, 2, 3]}'              | 'numbers'           | [1, 2, 3]
        '{"strings": ["one", "two"]}'         | 'strings'           | ['one', 'two']
        '{"booleans": [false, true]}'         | 'booleans'          | [false, true]
        '{"objects": [{"id": 1}, {"id": 2}]}' | 'objects'           | [['id': 1], ['id': 2]]
        '{"objects": [{"id": 1}, {"id": 2}]}' | 'objects[*].id'     | [1, 2]
        '{"empty": []}'                       | 'empty'             | []
        '{"non-array": "NaA"}'                | 'non-array'         | null
        '{}'                                  | 'not-existing-path' | null
    }

    @Test
    @Unroll
    def "getCollection() returns #result for '#path' path in #json"()
    {
        given:
        JsonObject obj = JsonObject.createFrom json

        expect:
        obj.getCollection(path) == result

        where:
        json                                  | path                | result
        '{"numbers": [1, 2, 3]}'              | 'numbers'           | [1, 2, 3]
        '{"strings": ["one", "two"]}'         | 'strings'           | ['one', 'two']
        '{"booleans": [true]}'                | 'booleans'          | [true]
        '{"objects": [{"id": 1}, {"id": 2}]}' | 'objects'           | [['id': 1], ['id': 2]]
        '{"objects": [{"id": 1}, {"id": 2}]}' | 'objects[*].id'     | [1, 2]
        '{"empty": []}'                       | 'empty'             | []
    }

    @Test
    @Unroll
    def "getCollection() throws #exception for '#path' path"()
    {
        given:
        JsonObject obj = JsonObject.createFrom json

        when:
        obj.getCollection(path)

        then:
        thrown(exception)

        where:
        json                   | path           | exception
        '{"non-array": "NaA"}' | 'non-array'    | ClassCastException
        '{}'                   | 'not-existing' | PathNotFoundException
    }

	private static InputStream asStream(String content) {
		IOUtils.toInputStream(content)
	}
}
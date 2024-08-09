/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.myorg.quickstart;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.net.URL;

public class FromFileToConsole {

	public static void main(String[] args) throws Exception {
		// Sets up the execution environment, which is the main entry point
		// to building Flink applications.
		// obtain execution environment and set setBufferTimeout to 1 to enable
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment().setBufferTimeout(1);


		URL resource = FromFileToConsole.class.getClassLoader().getResource("input.txt");
		var path = resource.getPath();
		DataStream<String> text = env.readTextFile(path);

		DataStream<Tuple2<String, Integer>> counts =
				text.flatMap(new Tokenizer())
				.name("tokenizer")
				.keyBy(value -> value.f0)
				.sum(1)
				.name("counter");


		counts.print().name("print-sink");
		System.out.println("Executing Iterate example with file input data set.");

		// Execute program, beginning computation.
		env.execute("Flink File to Console");
	}

	public static final class Tokenizer
			implements FlatMapFunction<String, Tuple2<String, Integer>> {

		@Override
		public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
			// normalize and split the line
			String[] tokens = value.toLowerCase().split("\\W+");

			// emit the pairs
			for (String token : tokens) {
				if (token.length() > 0) {
					out.collect(new Tuple2<>(token, 1));
				}
			}
		}
	}
}

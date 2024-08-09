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
import util.SocketConnection;
import util.SocketTextStreamSink;
import util.SocketTextStreamSource;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class FromStreamToStream {
	public static final SocketConnection CONNECTION = new SocketConnection("localhost", 8080);

	public static void main(String[] args) throws Exception {
		// Sets up the execution environment, which is the main entry point
		// to building Flink applications.
		// obtain execution environment and set setBufferTimeout to 1 to enable
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment().setBufferTimeout(1);

		String path = "C:\\Workspace\\flink\\input.txt";
		DataStream<String> text = env.readTextFile(path);
		DataStream<Tuple2<String, Integer>> counts =
				text.flatMap(new Tokenizer())
				.name("tokenizer")
				.keyBy(value -> value.f0)
				.sum(1)
				.name("counter");

		//counts.print().name("print-sink");

		env.addSource(new SocketTextStreamSource())
				.addSink(new SocketTextStreamSink());
//		counts.addSink(new SocketTextStreamSink());
		System.out.println("Executing Iterate example with default input data set.");
		System.out.println("Use --input to specify file input.");
		env.execute("Flink Java API Skeleton");
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

//		@PublicEvolving
//		public DataStreamSink<T> writeToSocket(String hostname, int port, SerializationSchema<T> schema){
//			DataStreamSink<T> returnStream = addSink(new SocketClientSink<>(hostname,port, schema,0));
//			returnStream.setParallelism(1);
//			return returnStream
//		}
	}

	public static final class TokenizerString
			implements FlatMapFunction<String, String> {

		@Override
		public void flatMap(String value, Collector<String> out) {
			// normalize and split the line
			String[] tokens = value.toLowerCase().split("\\W+");

			// emit the pairs
			for (String token : tokens) {
				if (token.length() > 0) {
					out.collect(token);
				}
			}
		}
	}
}

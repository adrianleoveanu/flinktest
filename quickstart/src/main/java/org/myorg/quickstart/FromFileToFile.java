package org.myorg.quickstart;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;

import java.time.Duration;

public class FromFileToFile {

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

		var output = "output";
		counts.sinkTo(
				FileSink.<Tuple2<String, Integer>>forRowFormat(
								new Path(output), new SimpleStringEncoder<>())
						//.withRollingPolicy(
						//		DefaultRollingPolicy.builder()
						//				.withMaxPartSize(MemorySize.ofMebiBytes(1))
						//				.withRolloverInterval(Duration.ofSeconds(10))
						//				.build())
						.build());
		//counts.print().name("print-sink");
		System.out.println("Executing Iterate example with file input data set.");

		// Execute program, beginning computation.
		env.execute("Flink File to File");
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

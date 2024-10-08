package net.consensys.arrays;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class ArrayCopyingBenchmark {

    @Param({"10","100","1000","10000"}) int SIZE;
    byte[] src;

    @Setup
    public void setup() {
        src = new byte[SIZE];
        final Random r = new Random(7L);
        r.nextBytes(src);
    }

    @Benchmark
    public byte[] systemCopy() {
        var dest = new byte[src.length];
        System.arraycopy(src,0,dest,0, src.length);
        return dest;
    }

    @Benchmark
    public byte[] arrayCopy() {
        return Arrays.copyOf(src, src.length);
    }

    @Benchmark
    public byte[] manualCopy() {
        var dest = new byte[src.length];
        for (int i = 0; i < src.length; i++) {
            dest[i] = src[i];
        }
        return dest;
    }

    public static void main(String[] args) throws RunnerException, IOException {
        Options opt = new OptionsBuilder()
                .include(ArrayCopyingBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

}

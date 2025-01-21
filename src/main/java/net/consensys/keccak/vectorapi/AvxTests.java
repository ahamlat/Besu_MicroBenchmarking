package net.consensys.keccak.vectorapi;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorSpecies;
import net.consensys.keccak.KeccakBenchmark;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 1000, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 100, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@Fork(jvmArgs = {
        "--enable-preview",
        "--add-modules=jdk.incubator.vector"
})
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class AvxTests {
    static final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_256;
    int[] array1 ;
    int[] array2 ;
    @Setup
    public void setup() {
        array1 = new int[] {1, 2, 3, 4, 5, 6, 7, 8};
        array2 = new int[] {10, 20, 30, 40, 50, 60, 70, 80};
    }


    @Benchmark
    public void addTwoScalarArrays(Blackhole bh) {

        int[] result = new int[array1.length];
        for (int i = 0; i < array1.length; i++) {
            result[i] = array1[i] * array2[i];
        }
        bh.consume(result);
    }

    @Benchmark
    public void addTwoVectorArrays(Blackhole bh) {
        var v1 = IntVector.fromArray(SPECIES, array1, 0);
        var v2 = IntVector.fromArray(SPECIES, array2, 0);
        var result = v1.mul(v2).toArray();
        bh.consume(result);
    }

    public static void main(String[] args) throws RunnerException, IOException {
        Options opt = new OptionsBuilder()
                .include(AvxTests.class.getSimpleName())
                //      .addProfiler(AsyncProfiler.class)
                .build();

        new Runner(opt).run();
    }

}

package net.consensys.keccak;

import net.consensys.keccak.cryptohash.Keccak256;
import net.consensys.keccak.bouncycastle.Hash;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.bytes.Bytes32;
import org.hyperledger.besu.ethereum.trie.verkle.adapter.TrieKeyAdapter;
import org.hyperledger.besu.ethereum.trie.verkle.hasher.PedersenHasher;
import org.hyperledger.besu.nativelib.constantine.LibConstantineKeccak256;
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
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.profile.AsyncProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 50, time = 1)
@Measurement(iterations = 50, time = 1)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class KeccakBenchmark {

    @Param({"32"}) int SIZE;
    Bytes32 bytes;


    @Param({"20"}) int ADDRESS_SIZE;
    Bytes address;

    @Setup

    public void setup() {
        byte[] src = new byte[SIZE];
        final Random r = new Random(7L);
        r.nextBytes(src);
        bytes = Bytes32.wrap(src);
        byte[] srcAddress = new byte[ADDRESS_SIZE];
        r.nextBytes(srcAddress);
        address = Bytes.wrap(srcAddress);
        //register first time
        LibConstantineKeccak256.keccak256(bytes.toArrayUnsafe());
    }

    @Benchmark
    public void keccakBCVersionjdk15on (final Blackhole blackhole) {
        Bytes32 hash = Hash.keccak256(bytes);
        blackhole.consume(hash);
    }

    @Benchmark
    public void keccakConstantine (final Blackhole blackhole) {
        Bytes32 hash = Bytes32.wrap(LibConstantineKeccak256.keccak256(bytes.toArrayUnsafe()));
        blackhole.consume(hash);
    }


    public static byte[] sha3(byte[] input) {
        Keccak256 digest =  new Keccak256();
        digest.update(input);
        return digest.digest();
    }

    public static void main(String[] args) throws RunnerException, IOException {
        Options opt = new OptionsBuilder()
                .include(KeccakBenchmark.class.getSimpleName())
                .addProfiler(AsyncProfiler.class)
                .build();

        new Runner(opt).run();
    }
}

package net.consensys.keccak;

import net.consensys.keccak.cryptohash.Keccak256;
import net.consensys.keccak.bouncycastle.Hash;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.bytes.Bytes32;
import org.hyperledger.besu.ethereum.trie.verkle.adapter.TrieKeyAdapter;
import org.hyperledger.besu.ethereum.trie.verkle.hasher.PedersenHasher;
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
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 100, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 100, time = 100, timeUnit = TimeUnit.MILLISECONDS)
@Fork(jvmArgs = {
        "--enable-preview",
        "--add-modules=jdk.incubator.vector"
})
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class KeccakBenchmark {

    @Param({"32"}) int SIZE;
    Bytes32 bytes;

    byte[] src;

/*
    @Param({"20"}) int ADDRESS_SIZE;
    Bytes address;*/

    //TrieKeyAdapter trieKeyAdapter;

    @Setup
    public void setup() {
        src = new byte[SIZE];
        final Random r = new Random(7L);
        r.nextBytes(src);
        bytes = Bytes32.wrap(src);
       /* byte[] srcAddress = new byte[ADDRESS_SIZE];
        r.nextBytes(srcAddress);
        address = Bytes.wrap(srcAddress);
        trieKeyAdapter = new TrieKeyAdapter(new PedersenHasher());
        trieKeyAdapter.storageKey(address, bytes);*/
    }

    @Benchmark
    public void keccakBCVersionjdk15on (final Blackhole blackhole) {
     //  System.out.println(bytes.toHexString());
        Bytes32 hash = Hash.keccak256(bytes);
     //   System.out.println(hash.toHexString());
        blackhole.consume(hash);
    }

    /*
    @Benchmark
    public void stemCryptoHash(final Blackhole blackhole) {
        Bytes storageStem = trieKeyAdapter.storageKey(address, bytes);
        blackhole.consume(storageStem);
    }*/
/*
    @Benchmark
    public void keccakWithVecorAPI(final Blackhole blackhole) {
    //    System.out.println(bytes.toHexString());
        KeccakDigest keccak = new KeccakDigest(256); // Instantiate with a fixed output length (e.g., 256 bits)
        keccak.update(src, 0, src.length);
        byte[] hash = new byte[keccak.getDigestSize()];
        keccak.doFinal(hash, 0);
      //  System.out.println(Bytes.wrap(hash).toHexString());
        blackhole.consume(hash);
    }*/

    @Benchmark
    public void keccakCryptoHash(final Blackhole blackhole) {
   //     System.out.println(Bytes.wrap(src).toHexString());
        Bytes32 hash =  Bytes32.wrap(sha3(src));
   //      System.out.println(hash.toHexString());
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
             //   .addProfiler(AsyncProfiler.class)
                .build();

        new Runner(opt).run();
    }
}

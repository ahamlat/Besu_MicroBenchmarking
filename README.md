# Besu_MicroBenchmarking
Some microbenchmarks on Hyperledger Besu

## Prerequisites
AsyncProfiler setup:
`-Djava.library.path=/path/to/your/async-profiler-3.0-macos/lib`

## Building

```shell
mvn clean install
```

## Running

### Without profiling

```shell
java -jar target/Besu_MicroBenchmarking-1.0-SNAPSHOT.jar
```

### With profiling (async-profiler)

```shell
java -XX:+UnlockDiagnosticVMOptions -jar target/Besu_MicroBenchmarking-1.0-SNAPSHOT.jar --profile async:output=jfr;event=cpu
```
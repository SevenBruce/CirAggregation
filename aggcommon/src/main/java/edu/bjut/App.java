package edu.bjut;

import java.io.IOException;

import org.openjdk.jmh.annotations.Benchmark;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {
        try {
            org.openjdk.jmh.Main.main(args);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Benchmark
    public void init() {
    // Do nothing
    }
}

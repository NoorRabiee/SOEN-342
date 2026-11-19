package main.java.ats;

import main.java.ats.controller.SystemController;
import main.java.ats.ui.Console;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        Scanner in = new Scanner(System.in, StandardCharsets.UTF_8);
        Console console = new Console(new SystemController(), in, out);
        console.printBanner();
        if (args.length > 0) {
            console.preload(args[0]);
        }
        console.run();
    }

}
/* MATLAB-like language to LLVM IR */
import java.io.IOException;
import java.io.FileReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0 || args.length > 2) {
            System.out.println("USAGE: java Main <in_file> (<optional_ir_out_file>)?");
            System.exit(1);
        }

        String ir;
        try (Reader reader = new FileReader(args[0], StandardCharsets.UTF_8)) {
            /* Read the source program */
            scanner l = new scanner(reader);
            /* Parse it and build the final IR */
            @SuppressWarnings("deprecation")
            parser p = new parser(l);
            p.parse();
            ir = p.getGeneratedIr();
        } catch (IOException e) {
            throw new RuntimeException("Unable to read source file: " + args[0], e);
        }

        if (args.length == 2) {
            /* Optional file output */
            Path outputPath = Paths.get(args[1]);
            Files.writeString(outputPath, ir, StandardCharsets.UTF_8);
        } else {
            runIr(ir);
        }
    }

    private static void runIr(String ir) throws Exception {
        Path tempDir = Files.createTempDirectory("matlab-like-parser-");
        Path irPath = tempDir.resolve("program.ll");
        Files.writeString(irPath, ir, StandardCharsets.UTF_8);

        if (commandExists("lli")) {
            runCommand(Arrays.asList("lli", irPath.toString()));
            return;
        }

        if (commandExists("clang")) {
            Path exePath = tempDir.resolve(isWindows() ? "program.exe" : "program");
            runCommand(Arrays.asList("clang", "-w", irPath.toString(), "-o", exePath.toString()));
            runCommand(Arrays.asList(exePath.toString()));
            return;
        }

        throw new RuntimeException("Install lli or clang to run the generated program");
    }

    private static boolean commandExists(String command) {
        List<String> check = new ArrayList<String>();
        if (isWindows()) {
            check.add("where");
            check.add(command);
        } else {
            check.add("sh");
            check.add("-c");
            check.add("command -v " + command);
        }

        try {
            Process process = new ProcessBuilder(check).redirectError(ProcessBuilder.Redirect.DISCARD).start();
            return process.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private static void runCommand(List<String> command) throws Exception {
        Process process = new ProcessBuilder(command).inheritIO().start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Command failed: " + String.join(" ", command));
        }
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }
}

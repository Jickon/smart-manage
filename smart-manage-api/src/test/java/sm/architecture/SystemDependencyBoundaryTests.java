package sm.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SystemDependencyBoundaryTests {

    @Test
    void systemAndFrameworkMustNotDependOnDomainPackages() throws IOException {
        assertNoDomainImports(Path.of("src/main/java/sm/system"));
        assertNoDomainImports(Path.of("src/main/java/sm/framework"));
    }

    private void assertNoDomainImports(Path sourceRoot) throws IOException {
        try (var paths = Files.walk(sourceRoot)) {
            var violations = paths
                    .filter(path -> path.toString().endsWith(".java"))
                    .filter(this::containsDomainImport)
                    .map(Path::toString)
                    .toList();
            assertTrue(violations.isEmpty(), () -> "基础层禁止反向依赖领域包: " + violations);
        }
    }

    private boolean containsDomainImport(Path path) {
        try {
            return Files.readString(path).contains("import sm.domain.");
        } catch (IOException exception) {
            throw new IllegalStateException("读取源码失败: " + path, exception);
        }
    }
}

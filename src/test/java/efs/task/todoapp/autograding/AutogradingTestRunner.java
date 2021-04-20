package efs.task.todoapp.autograding;

import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.io.PrintWriter;

import static java.util.Objects.nonNull;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

public final class AutogradingTestRunner {

    private static final String ROOT_PACKAGE = "efs.task.todoapp.autograding";
    private static final int STACK_TRACE_LIMIT = 9;

    private final String packageName;

    private AutogradingTestRunner(String packageName) {
        this.packageName = packageName;
    }

    public static void main(String[] args) {
        AutogradingTestRunner runner = new AutogradingTestRunner(ROOT_PACKAGE);
        runner.runAllTestsIn();
    }

    void runAllTestsIn() {
        TestExecutionSummary summary = executeTests();
        printSummary(summary);
    }

    private TestExecutionSummary executeTests() {
        var listener = new SummaryGeneratingListener();
        var request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectPackage(packageName))
                .build();

        var launcher = LauncherFactory.create();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        return listener.getSummary();
    }

    private void printSummary(TestExecutionSummary summary) {
        summary.printTo(new PrintWriter(System.err));

        summary.getFailures()
                .forEach(this::printFailure);
    }

    private void printFailure(TestExecutionSummary.Failure failure) {
        var testIdentifier = failure.getTestIdentifier();

        if (!testIdentifier.isTest()) {
            return;
        }

        var stringBuilder = new StringBuilder();
        stringBuilder
                .append("[")
                .append(getTestName(testIdentifier))
                .append("]")
                .append(System.lineSeparator());

        var exception = failure.getException();
        if (nonNull(exception)) {
            StackTraceElement[] stackTrace = exception.getStackTrace();
            for (int i = 0, stackTraceLength = stackTrace.length; i < stackTraceLength; i++) {
                StackTraceElement element = stackTrace[i];
                stringBuilder.append("\t").append(element).append(System.lineSeparator());
                if (i == STACK_TRACE_LIMIT) {
                    stringBuilder.append("\t...");
                    break;
                }
            }
        }

        System.out.println(stringBuilder);
    }

    private String getTestName(TestIdentifier testIdentifier) {
        return testIdentifier.getSource()
                .map(testSource -> testSource instanceof MethodSource ? (MethodSource) testSource : null)
                .map(methodSource -> methodSource.getClassName() + "#" + methodSource.getMethodName())
                .orElse(testIdentifier.getDisplayName());
    }
}

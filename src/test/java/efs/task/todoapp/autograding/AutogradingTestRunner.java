package efs.task.todoapp.autograding;

import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.io.PrintWriter;

import static java.util.Objects.nonNull;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

public final class AutogradingTestRunner {

    private static final String ROOT_PACKAGE = "efs.task.todoapp.autograding";
    private static final int STACK_TRACE_LIMIT = 9;

    public static void main(String[] args) {
        AutogradingTestRunner runner = new AutogradingTestRunner();
        runner.runAllTestsIn(args.length > 0 ? args[0] : null);
    }

    void runAllTestsIn(String methodName) {
        TestExecutionSummary summary = executeTests(methodName);
        printSummary(summary);
    }

    private TestExecutionSummary executeTests(String methodName) {
        var listener = new SummaryGeneratingListener();
        var request = LauncherDiscoveryRequestBuilder.request()
                .selectors(nonNull(methodName) ? selectMethod(methodName) : selectPackage(ROOT_PACKAGE))
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

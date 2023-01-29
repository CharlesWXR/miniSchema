package cn.edu.njnu.minischema.exception;

public class DecoderException extends Exception {
	public static final String IllegalParamCount = "Error: illegal param count";
	public static final String IllegalParamType = "Error: illegal param type";
	public static final String UnexpectedSentenceEnd = "Error: Encountered unexpected sentence end " +
			"(may caused by illegal param count)";
	public static final String TooManyEndSign = "Error: Encountered sentence ending indicator more than once " +
			"(please ensure right brackets matches left brackets)";
	public static final String MissingStartSign = "Error: Fail to find the starting indicator " +
			"(please ensure codes starts with left brackets)";
	public static final String MissingEndSign = "Error: Fail to find the ending indicator " +
			"(please ensure codes ends with right brackets)";
	public static final String UndefinedOperator = "Error: Undefined operator: ";
	public static final String ExecutionError = "Error: Fail to execute the code though successfully decoded " +
			"(please check operators' executor classes)";

	public static final String UndefinedVariable = "Warning: Undefined variable, will use default value: ";
	public static final String RedundantElementsFount = "Warning: The leading parts of the code was successfully" +
			"executed though the remaining ones are ignored " +
			"(please check the brackets or structure and split code into multiple lines)\n" +
			"The ignored parts:";
	public static final String ConvertFailure = "Warning: Failed to convert the target type";

	public DecoderException(String message) {
		super(message);
	}
}
package cn.edu.njnu.minischema.base;

import cn.edu.njnu.minischema.annotation.SentenceDefinition;
import cn.edu.njnu.minischema.annotation.SentenceScanPath;
import cn.edu.njnu.minischema.enums.VariableType;
import cn.edu.njnu.minischema.exception.DecoderException;
import javassist.compiler.ast.Variable;
import org.reflections.Reflections;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Pattern;

@SentenceScanPath("cn.edu.njnu.minischema.sentence")
public class Decoder {
	// Store the defined sentence definitions under the scan path
	protected static Map<String, SentenceDescriptor> SentenceStructure = new HashMap<String, SentenceDescriptor>();
	// Store the global variables created by codes
	protected static Map<String, Data> GlobalVariables = new HashMap<String, Data>();
	// The valid pattern of variables
	protected static String ValidPattern = "^[A-Za-z_][\\w]*$";
	// The variables with specific meanings
	protected static String[] InWords = {"_GlobalVariables"};

	public Decoder() {
		try {
			// Load sentence definitions from the package in @SentenceScanPath.value
			// Sentence definitions are classed with annotation @SentenceDefinition
			// and implements interface SentenceBase
			Class clazz = this.getClass();
			String path = ((SentenceScanPath) clazz.getAnnotation(SentenceScanPath.class)).value();
			Reflections reflections = new Reflections(path);
			Set<Class<?>> classes = reflections.getTypesAnnotatedWith(SentenceDefinition.class);
			for (Class c : classes) {
				// Load Class and the Method Data operation(List<Object>)
				SentenceDefinition s = (SentenceDefinition) c.getDeclaredAnnotation(SentenceDefinition.class);
				SentenceDescriptor sd = new SentenceDescriptor();
				sd.method = c.getMethod("operation", List.class);
				sd.paramCount = s.paramCount();
				sd.paramTypes = s.paramTypes();
				sd.clazz = c;
				// The clazz's instance will load in lazy mode
				sd.instance = null;
				// The operator will transform into lowercase
				SentenceStructure.put(s.operator().toLowerCase(), sd);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void clearGlobalVariables() {
		this.GlobalVariables.clear();
	}

	public void execute(String code) {
		// Execute multiply lines of code separated by \n
		code = code.trim();
		String[] sentences = code.split("\n");
		for (String sentence : sentences) {
			try {
				decode(sentence);
			} catch (DecoderException e) {
				System.out.println(e.getMessage());
				System.out.println("Execution aborted due to previous errors!");
				break;
			}
		}
	}

	private void decode(String code) throws DecoderException {
		// Execute one line code
		code = code.trim();
		String[] elements = code.split("[\\s]+");
		if (elements.length == 0)
			throw new DecoderException(DecoderException.MissingStartSign);

		// Decode code from beginning
		DecoderData res = decode(elements, 0);
		if (res.returnData.type == VariableType.UPDATE_VARIABLE) {
			// Update global variables
			Map<String, Data> m = (Map<String, Data>) res.returnData.content;
			for (String key : m.keySet()) {
				// New value for the variable
				Data d = m.get(key);
				if (d.type == VariableType.VARIABLE_NAME) {
					// If the new value is another variable
					if (GlobalVariables.containsKey(d.content)) {
						// The other variable is already defined before
						Data temp = GlobalVariables.get(d.content);
						GlobalVariables.put(key, temp);
						output(temp);
					} else {
						// The other variable is undefined, user the default value
						System.out.println(DecoderException.UndefinedVariable);
						Data temp = new Data();
						temp.content = 0;
						temp.type = VariableType.INT;
						GlobalVariables.put(key, temp);
						output(temp);
					}
				} else {
					// If the new value is plain number
					GlobalVariables.put(key, m.get(key));
					output(m.get(key));
				}
			}
		} else {
			// Display result
			output(res.returnData);
		}
		// Test whether the code is executed thoroughly
		if (res.index < elements.length - 1) {
			// Warning the un executed parts of the code
			StringBuilder sb = new StringBuilder();
			for (int i = res.index + 1; i < elements.length; i++) {
				sb.append(" " + elements[i]);
			}
			System.out.println(DecoderException.RedundantElementsFount + sb.toString());
		}
	}

	private DecoderData decode(String[] elements, int index) throws DecoderException {
		// Decode the element by sentence

		// Missing the '(' in the beginning of the sentence
		if (elements[index].startsWith("(") == false)
			throw new DecoderException(DecoderException.MissingStartSign);

		// Operator to lowercase and search the definition
		String operator = elements[index].substring(1).toLowerCase();
		if (operator == "" || !SentenceStructure.containsKey(operator))
			throw new DecoderException(DecoderException.UndefinedOperator + operator);

		SentenceDescriptor descriptor = SentenceStructure.get(operator);

		// Init the params in the sentence and state
		List<Object> params = new ArrayList<Object>();
		StateMachine stateMachine = new StateMachine(descriptor);
		index++;

		// Decode the params in the sentence
		while (index < elements.length) {
			Data param = new Data();
			boolean codeEnd = false;

			if (elements[index].startsWith("(")) {
				// The param is another sentence, recursion
				DecoderData innerResult = decode(elements, index);
				// The index the inner decoder stopped at
				index = innerResult.index;
				param = innerResult.returnData;
			} else {
				// The param is a plain element instead of a sentence
				String present = elements[index];

				// Test if the param contains the sentence ending (e.g. '12)')
				int i = present.indexOf(")");
				if (i != -1) {
					// Find the word's first ')' and exaggerate the param and put the rest back
					// in case of '12)))' in the recursion process => '12' and '))'
					if (i < present.length() - 1) {
						elements[index] = present.substring(i + 1);
						// Next loop still decode the remaining parts
						index--;
					}
					present = present.substring(0, i);
					codeEnd = true;
				}

				if (present.length() != 0) {
					// Parse the element into suitable variable type
					try {
						// Try to interpret as int
						int temp = Integer.parseInt(present);
						param.content = temp;
						param.type = VariableType.INT;
					} catch (NumberFormatException e) {
						// Try to interpret as variable name
						param = decodeInWords(present);
						if (param.type == VariableType.ERROR) {
							if (Pattern.matches(ValidPattern, present)) {
								param.type = VariableType.VARIABLE_NAME;
								param.content = present;
							} else {
								throw new DecoderException(DecoderException.UndefinedOperator + present);
							}
						}
					}
				}

			}

			for (VariableType vt : descriptor.paramTypes) {
				// Try to convert the param into suitable data type for the operator
				Data d = convert(param, vt);
				if (d.type != VariableType.ERROR) {
					try {
						// Find a suitable type and update the state
						stateMachine.nextState(d.type);
						// Put into the param list
						params.add(d.content);
						break;
					} catch (DecoderException e) {
						if (DecoderException.IllegalParamCount.equals(e.getMessage()))
							throw e;
					}
				}
			}

			if (codeEnd) {
				// Prepare to return the result of the code
				stateMachine.nextState(VariableType.SENTENCE_END);
				try {
					// Load the instance of the definition's clazz
					if (descriptor.instance == null)
						descriptor.instance = descriptor.clazz.newInstance();
					// Invoke the method
					Data data = (Data) descriptor.method.invoke(descriptor.instance, params);
					DecoderData res = new DecoderData();
					res.returnData = data;
					res.index = index;
					return res;
				} catch (Exception e) {
					throw new DecoderException(DecoderException.ExecutionError);
				}
			}

			index++;
		}

		// If the code ended normally will return early than here
		throw new DecoderException(DecoderException.MissingEndSign);
	}

	private Data convert(Data original, VariableType targetType) throws DecoderException {
		// Try to convert the original data into possible types or will return VariableType.ERROR
		if (original.type == targetType) {
			return original;
		}

		Data res = new Data();
		if (original.type == VariableType.INT) {
			if (targetType == VariableType.BOOLEAN) {
				res.type = VariableType.BOOLEAN;
				res.content = !((int) original.content == 0);
			} else if (targetType == VariableType.KEY_VALUE) {
				res.type = VariableType.KEY_VALUE;
				res.content = new HashMap<String, Data>() {{
					put("1", original);
				}};
			} else {
				res.type = VariableType.ERROR;
			}
		} else if (original.type == VariableType.BOOLEAN) {
			if (targetType == VariableType.INT) {
				res.type = VariableType.INT;
				res.content = (boolean) original.content ? 1 : 0;
			} else if (targetType == VariableType.KEY_VALUE) {
				res.type = VariableType.KEY_VALUE;
				res.content = new HashMap<String, Data>() {{
					put("1", original);
				}};
			} else {
				res.type = VariableType.ERROR;
			}
		} else if (original.type == VariableType.VARIABLE_NAME) {
			if (targetType == VariableType.KEY_VALUE) {
				res.type = VariableType.KEY_VALUE;
				Map<String, Data> m = new HashMap<String, Data>();
				Data temp = new Data();
				if (GlobalVariables.containsKey(original.content)) {
					temp = GlobalVariables.get(original.content);
				} else {
					temp.content = 0;
					temp.type = VariableType.INT;
					System.out.println(DecoderException.UndefinedVariable + original.content);
				}
				m.put((String) original.content, temp);
				res.content = m;
			} else {
				if (GlobalVariables.containsKey(original.content)) {
					res = GlobalVariables.get(original.content);
				} else {
					res.content = 0;
					res.type = VariableType.INT;
					System.out.println(DecoderException.UndefinedVariable + original.content);
				}
				return convert(res, targetType);
			}
		} else {
			res.type = VariableType.ERROR;
		}
		return res;
	}

	private void output(Data data) {
		// Output data in default form
		switch (data.type) {
			case INT: {
				System.out.println((int) data.content);
				break;
			}
			case BOOLEAN: {
				System.out.println((boolean) data.content);
				break;
			}
			case ERROR: {
				System.out.println((String) data.content);
				break;
			}
		}
	}

	private Data decodeInWords(String element) {
		Data res = new Data();
		res.type = VariableType.ERROR;
		for (String s : this.InWords) {
			if (s.equals(element)) {
				try {
					return (Data) this.getClass().getDeclaredMethod(s).invoke(this);
				} catch (Exception e) {
					res.content = e.getMessage();
					return res;
				}
			}
		}
		res.content = DecoderException.UndefinedOperator;
		return res;
	}

	private Data _GlobalVariables() {
		// Serving for (output _GlobalVariables) to inspect all the _GlobalVariables
		Data res = new Data();
		res.content = this.GlobalVariables;
		res.type = VariableType.KEY_VALUE;
		return res;
	}
}

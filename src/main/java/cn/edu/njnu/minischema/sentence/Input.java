package cn.edu.njnu.minischema.sentence;

import cn.edu.njnu.minischema.annotation.SentenceDefinition;
import cn.edu.njnu.minischema.base.Data;
import cn.edu.njnu.minischema.base.SentenceBase;
import cn.edu.njnu.minischema.enums.VariableType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@SentenceDefinition(operator = "input", paramCount = -1, paramTypes = {VariableType.VARIABLE_NAME})
public class Input implements SentenceBase {
	private static final String Hint = "请输入${count}个整数值：";

	@Override
	public Data operation(List<Object> args) {
		Data res = new Data();

		int count = args.size();
		Scanner scanner = new Scanner(System.in);

		Map<String, Data> content = new HashMap<String, Data>();

		System.out.println(Hint.replaceAll("\\$\\{count\\}", Integer.toString(count)));
		for (int i = 0; i < count; i++) {
			int temp = scanner.nextInt();
			Data tempData = new Data();
			tempData.type = VariableType.INT;
			tempData.content = temp;
			content.put((String) args.get(i), tempData);
		}

		res.type = VariableType.UPDATE_VARIABLE;
		res.content = content;
		return res;
	}
}

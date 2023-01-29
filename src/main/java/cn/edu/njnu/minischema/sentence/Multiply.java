package cn.edu.njnu.minischema.sentence;

import cn.edu.njnu.minischema.annotation.SentenceDefinition;
import cn.edu.njnu.minischema.base.Data;
import cn.edu.njnu.minischema.base.SentenceBase;
import cn.edu.njnu.minischema.enums.VariableType;

import java.util.List;

@SentenceDefinition(operator = "*", paramCount = -1, paramTypes = {VariableType.INT})
public class Multiply implements SentenceBase {
	@Override
	public Data operation(List<Object> args) {
		Data res = new Data();

		int ans = 1;
		for (Object arg : args) {
			ans *= (int) arg;
		}

		res.content = ans;
		res.type = VariableType.INT;
		return res;
	}
}

package cn.edu.njnu.minischema.sentence;

import cn.edu.njnu.minischema.annotation.SentenceDefinition;
import cn.edu.njnu.minischema.base.Data;
import cn.edu.njnu.minischema.base.SentenceBase;
import cn.edu.njnu.minischema.enums.VariableType;

import java.util.List;

@SentenceDefinition(operator = "+", paramCount = -1, paramTypes = {VariableType.INT})
public class Plus implements SentenceBase {
	public Data operation(List<Object> args) {
		Data res = new Data();

		int ans = 0;
		for (Object arg : args)
			ans += (int) arg;

		res.type = VariableType.INT;
		res.content = ans;
		return res;
	}
}

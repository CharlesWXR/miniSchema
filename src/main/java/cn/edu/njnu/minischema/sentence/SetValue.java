package cn.edu.njnu.minischema.sentence;

import cn.edu.njnu.minischema.annotation.SentenceDefinition;
import cn.edu.njnu.minischema.base.Data;
import cn.edu.njnu.minischema.base.SentenceBase;
import cn.edu.njnu.minischema.enums.VariableType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SentenceDefinition(operator = ":=", paramCount = 2, paramTypes = {VariableType.VARIABLE_NAME, VariableType.INT})
public class SetValue implements SentenceBase {
	@Override
	public Data operation(List<Object> args) {
		Data res = new Data();

		Data v = new Data();
		Map<String, Data> m = new HashMap<String, Data>();
		String key = (String) args.get(0);
		Object value = args.get(1);
		if (value instanceof Integer) {
			int i = (int)value;
			v.content = i;
			v.type = VariableType.INT;
		}
		else {
			v.content = value;
			v.type = VariableType.VARIABLE_NAME;
		}

		m.put(key, v);
		res.content = m;
		res.type = VariableType.UPDATE_VARIABLE;
		return res;
	}
}

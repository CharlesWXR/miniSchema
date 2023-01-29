package cn.edu.njnu.minischema.sentence;

import cn.edu.njnu.minischema.annotation.SentenceDefinition;
import cn.edu.njnu.minischema.base.Data;
import cn.edu.njnu.minischema.base.SentenceBase;
import cn.edu.njnu.minischema.enums.VariableType;

import java.util.List;
import java.util.Map;

@SentenceDefinition(operator = "output", paramCount = -1, paramTypes = {VariableType.KEY_VALUE})
public class Output implements SentenceBase {
	@Override
	public Data operation(List<Object> args) {
		Data res = new Data();

		for (Object arg : args) {
			Map<String, Data> m = (Map<String, Data>) arg;
			for (String key : m.keySet()) {
				if (key == "1") {
					System.out.println(m.get(key).content);
				} else {
					System.out.print(key + ":");
					Data d = m.get(key);
					if (d.type == VariableType.INT)
						System.out.println((int) d.content);
					else if (d.type == VariableType.BOOLEAN)
						System.out.println(d.content);
				}
			}
		}
		res.type = VariableType.VOID;
		return res;
	}
}

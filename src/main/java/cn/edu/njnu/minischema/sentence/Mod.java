package cn.edu.njnu.minischema.sentence;

import cn.edu.njnu.minischema.annotation.SentenceDefinition;
import cn.edu.njnu.minischema.base.Data;
import cn.edu.njnu.minischema.base.SentenceBase;
import cn.edu.njnu.minischema.enums.VariableType;

import java.util.List;

@SentenceDefinition(operator = "%", paramCount = 2, paramTypes = {VariableType.INT})
public class Mod implements SentenceBase {
	@Override
	public Data operation(List<Object> args) {
		Data res = new Data();

		res.content = (int) args.get(0) % (int) args.get(1);
		res.type = VariableType.INT;
		return res;
	}
}

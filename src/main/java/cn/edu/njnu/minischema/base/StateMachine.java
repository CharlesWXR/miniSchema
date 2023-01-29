package cn.edu.njnu.minischema.base;

import cn.edu.njnu.minischema.enums.State;
import cn.edu.njnu.minischema.enums.VariableType;
import cn.edu.njnu.minischema.exception.DecoderException;

public class StateMachine {
	private State presentState;
	private int paramCount;
	private VariableType[] types;

	public StateMachine() {
		this.presentState = State.OPERATOR;
	}

	public StateMachine(SentenceDescriptor sentenceDescriptor) {
		this.paramCount = sentenceDescriptor.paramCount;
		this.types = sentenceDescriptor.paramTypes;
		this.presentState = State.PARAM;
	}

	public boolean setOperator(SentenceDescriptor sentenceDescriptor) {
		if (this.presentState != State.OPERATOR) {
			return false;
		}

		this.paramCount = sentenceDescriptor.paramCount;
		this.types = sentenceDescriptor.paramTypes;
		this.presentState = State.PARAM;
		return true;
	}

	public void nextState(VariableType variableType) throws DecoderException {
		if (variableType == VariableType.SENTENCE_END) {
			if (this.presentState == State.FINISH
					|| (this.presentState == State.PARAM && this.paramCount <= 0))
				this.presentState = State.CLOSED;
			else if (this.presentState == State.CLOSED)
				throw new DecoderException(DecoderException.TooManyEndSign);
			else
				throw new DecoderException(DecoderException.UnexpectedSentenceEnd);
			return;
		}
		if (this.paramCount == 0 || this.presentState != State.PARAM)
			throw new DecoderException(DecoderException.IllegalParamCount);
		for (VariableType type : types) {
			if (variableType == type) {
				this.paramCount--;
				if (this.paramCount == 0)
					this.presentState = State.FINISH;
				return;
			}
		}

		throw new DecoderException(DecoderException.IllegalParamType);
	}

	public boolean isClosed() {
		return this.presentState == State.CLOSED;
	}
}

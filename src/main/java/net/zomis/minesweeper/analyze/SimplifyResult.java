package net.zomis.minesweeper.analyze;

public enum SimplifyResult {

	FAILED_NEGATIVE_RESULT, FAILED_TOO_BIG_RESULT, NO_EFFECT, SIMPLIFIED;
	
	public boolean isFailure() {
		return this == FAILED_NEGATIVE_RESULT || this == FAILED_TOO_BIG_RESULT;
	}
	
}

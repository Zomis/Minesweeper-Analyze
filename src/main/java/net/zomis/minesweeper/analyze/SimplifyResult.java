package net.zomis.minesweeper.analyze;

public enum SimplifyResult {

	FAILED_NEGATIVE_RESULT(-1), FAILED_TOO_BIG_RESULT(-2), NO_EFFECT(0), SIMPLIFIED(1);
	
	private SimplifyResult(int i) {
		
	}
	
}

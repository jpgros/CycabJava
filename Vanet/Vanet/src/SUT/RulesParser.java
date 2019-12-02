package SUT;

public class RulesParser {
	String expression;
	String reconfName;
	int utility;
	
	public RulesParser(String s) {
		expression =s;
	}
	
	public boolean parseExpression() {
		String[] splitsComma = expression.split(";");
		utility= Integer.parseInt(splitsComma[splitsComma.length-1]);
		reconfName=splitsComma[splitsComma.length-2];
		
		String[] splitsAndExpr= splitsComma[0].split("and");
		for(String expr : splitsAndExpr) {
			if(!evaluateExpr(expr)) return false;
		}
		return true;
	}
	public boolean evaluateExpr(String expr) {
		String[] splits = expr.split("or");
		return true;
	}
}

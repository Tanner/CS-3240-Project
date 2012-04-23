import java.util.ArrayList;
import java.util.List;


public class Rule {
	private Variable leftSide;
	private List<RuleElement> rightSide;
	
	public Rule(Variable leftSide, List<RuleElement> tailRuleRightSide) {
		this.leftSide = leftSide;
		this.rightSide = tailRuleRightSide;
	}
	
	public Rule(Variable leftSide, RuleElement rightSide) {
		this.leftSide = leftSide;
		this.rightSide = new ArrayList<RuleElement>();
		this.rightSide.add(rightSide);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(leftSide + " : ");
		
		for (RuleElement re : rightSide) {
			sb.append(re + " ");
		}
		
		return sb.toString();
	}

	public boolean hasLeftRecursion() {
		return leftSide == rightSide.get(0);
	}

	public Variable getLeftSide() {
		return leftSide;
	}

	public List<RuleElement> getRightSide() {
		return rightSide;
	}

	public void addToRightSide(Variable ruleElement) {
		rightSide.add(ruleElement);
	}
}

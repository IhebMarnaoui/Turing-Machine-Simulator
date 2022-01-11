package application.model;

//This class represents a tape

public class Tape {
	
	private String left;
	private String right;
	private char current;
	private char blank;

	public Tape(String tape, char blk) throws Exception {
		blank=blk;
		left = right = blank + "" + blank;
		if(tape.length() == 0) {
			current = blank;
		}else if(tape.length() == 1) {
			current=tape.charAt(0);
		} else {
			left=tape.substring(1, tape.length())+left;
			current=tape.charAt(0);	
		}
	}
	//write the given symbol in the current position
	public void write(char replace) {
		current=replace;
	}
	//read the current symbol
	public char read() {
		return current;
	}
	//move to the right
	public void right() {
		if(right.length()==0) {
			left=current+left;
			current=blank;
		} else {
			left=current+left;
			current=right.charAt(right.length()-1);
			right=right.substring(0, right.length()-1);
		}
	}
	//move to the left
	public void left() {
		if(left.length()==0) {
			right=right+current;
			current=blank;
		} else {
			right=right+current;
			current=left.charAt(0);
			left=left.substring(1, left.length());
		}
	}
	
	@Override
	public String toString() {
		return right+current+left;
	}
	
	public String getLeft() {
		return left;
	}

	public String getRight() {
		return right;
	}

	public char getCurrent() {
		return this.read();
	}

	public char getBlank() {
		return blank;
	}

	public void setBlank(char blank) {
		this.blank = blank;
	}
	

}

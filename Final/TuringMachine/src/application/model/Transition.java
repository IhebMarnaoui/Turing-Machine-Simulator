package application.model;

//This class represents a TM transition

public class Transition {
	
	private String currentState;
	private char symbolRead;
	private String nextState;
	private char symboleToWrite;
	private Move move;
	
	
	// Constructor	
	public Transition(String currentState, char symbolRead, String nextState, char symboleToWrite, Move move){
		this.currentState=currentState;
		this.symbolRead=symbolRead;
		this.nextState=nextState;
		this.symboleToWrite=symboleToWrite;
		this.move=move;
	}
	
	// Minimal constructor
	public Transition(String currentState, char symbolRead){
		this.currentState=currentState;
		this.symbolRead=symbolRead;
		this.nextState="";
		this.symboleToWrite=0;
		this.move=Move.STOP;
	}

	public String getCurrentState() {
		return currentState;
	}

	public char getSymbolRead() {
		return symbolRead;
	}

	public String getNextState() {
		return nextState;
	}

	public char getSymboleToWrite() {
		return symboleToWrite;
	}

	public Move getMove() {
		return move;
	}

	@Override
	public int hashCode() { 
		final int prime = 31;
		int result = 1;
		result = prime * result + ((currentState == null) ? 0 : currentState.hashCode());
		result = prime * result + symbolRead;
		return result;
	}

	@Override
	public boolean equals(Object obj) { // Compares two transitions
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transition other = (Transition) obj;
		if (currentState == null) {
			if (other.currentState != null)
				return false;
		} else if (!currentState.equals(other.currentState))
			return false;
		if (symbolRead != other.symbolRead)
			return false;
		return true;
	}	
	
	@Override
	public String toString() {
		return currentState + ", " + symbolRead + ", " + nextState + ", " + symboleToWrite + ", " + move;
	}
	
}

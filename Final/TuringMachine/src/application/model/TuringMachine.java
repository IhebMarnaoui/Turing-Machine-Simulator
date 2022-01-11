package application.model;


import java.util.NoSuchElementException;
import java.util.Set;

//This class represents a Turing Machine(TM)

public class TuringMachine { 
	
	private String name;
	private String initialState;
	private char blank;
	private Set<Transition> transitions;
	private Set<String> finalstates;
	private String comment;
	private Usage usage;

	private String currentState;
	private boolean halt;

	// Constructor
	public TuringMachine(String name, Set<Transition> transitions, String initialState, char blank, Set<String> finalstates){
		this.name=name;
		this.comment=null;
		this.transitions=transitions;
		this.initialState = this.currentState = initialState;
		this.blank=blank;
		this.finalstates=finalstates;
		this.halt=false;
		this.usage = ((finalstates==null || finalstates.size()==0)?Usage.CALCULATE:Usage.ACCEPT);
	}

	public void init() { // Initialise the TM 
		this.halt=false;
		this.currentState =initialState;
	}
	
	public boolean oneStep(Tape tape) throws NoSuchElementException  { // Reads/writes one symbol 
		Transition tr = transitionExists(currentState,tape.read());	
		if(tr == null) {
			throw new NoSuchElementException();
		}

		tape.write(tr.getSymboleToWrite());
		currentState=tr.getNextState();
		if(tr.getMove() == Move.STOP) halt = true;
		else if(tr.getMove() == Move.RIGHT) tape.right();
		else tape.left();
		
		return halt;
	}

	public Transition transitionExists(String currentState, char symbolRead) { // verifies if the next transition exists or not.
		Transition tr= new Transition(currentState,symbolRead);
		for(Transition transition : transitions){
			if(transition.equals(tr))
				return transition;
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public String getInitialState() {
		return initialState;
	}

	public Set<Transition> getTransitions() {
		return transitions;
	}

	public Set<String> getFinalstates() {
		return finalstates;
	}

	public char getBlank() {
		return blank;
	}

	public String getCurrentState() {
		return currentState;
	}

	public boolean isHalt() {
		return halt;
	}

	public Usage getUsage() {
		return usage;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	

}
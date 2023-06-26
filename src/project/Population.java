package project;

import java.util.ArrayList; 

public class Population  implements Cloneable {
	
	protected ArrayList<SingleSolution> solutions = new ArrayList<SingleSolution>();  //Ensemble de solutions
	
	public Population() {
		
	}
	
	//Gettter et Setter de solutions
	public ArrayList<SingleSolution> getSolutions() {
		return solutions;
	}
	public void setSolutions(ArrayList<SingleSolution> solutions) {
		this.solutions = solutions;
	}
	
	
	public void addSolution(SingleSolution solution) {
//		ArrayList<SingleSolution> solsArrayList = this.solutions; 
//		solsArrayList.add(solution);
//		this.setSolutions(solsArrayList);
		//
		this.solutions.add(solution);
	}
	
	
	 @Override
    public Population clone() {
        try {
            return (Population) super.clone();
        } catch (CloneNotSupportedException e) {
            // Handle the exception appropriately
            return null;
        }
    }
	 
	 public Population dupliPopulation() {
		 Population p = new Population();
		 p.setSolutions(this.solutions);
		 return p; 
	 }
	 
	 public 

	
	
}

package project;

import java.io.Console;
import java.util.ArrayList;
import java.util.Random; 

public class Population  implements Cloneable {
	
	protected ArrayList<SingleSolution> solutions = new ArrayList<SingleSolution>();  //Ensemble de solutions
	protected double scores [];
	protected ArrayList<Worker> workers; 
	protected ArrayList<Job> jobs; 
	protected SingleSolution parent1; 
	protected SingleSolution parent2; 
	
	public Population(ArrayList<Worker> workers, ArrayList<Job> jobs) {
		this.workers = workers;
		this.jobs = jobs;
	}
	
	//Gettter et Setter de solutions
	public ArrayList<SingleSolution> getSolutions() {
		return solutions;
	}
	
	public void setSolutions(ArrayList<SingleSolution> solutions) {
		this.solutions = solutions;
	}
	
	
	public void addSolution(SingleSolution solution) { 
		this.solutions.add(solution);
	}
	
	
    @Override
    public Population clone() {
        try {
            Population clonedPopulation = (Population) super.clone();
            clonedPopulation.solutions = new ArrayList<>(solutions);
            return clonedPopulation;
        } catch (CloneNotSupportedException e) {
            // Gérer l'exception de manière appropriée
            return null;
        }
    }
	  
	 
	 public double[] getScorePopulation() {
		double scores [] = new double[this.solutions.size()];
		for (int i = 0; i < scores.length; i++) {
			scores[i] = this.solutions.get(i).getScore(this.workers);
			System.out.println("Pop. initiale score "+i+": "+scores[i]);
		}
		this.scores = scores;
		return scores;
	 }
	 
	 public void sortScorePopulation() {
		 ArrayList<SingleSolution> solutions = this.solutions; 
		 for (int i = 0; i < this.scores.length; i++) {
			double min = this.scores[i]; 
			int pos = i; 
			for (int j = i; j < this.scores.length; j++) {
				if(this.scores[j]<min) {
					min= this.scores[j]; 
					pos = j; 
				}
			}
			this.scores[pos] = this.scores[i];
			this.scores[i] = min; 
			SingleSolution s = solutions.get(i);
			solutions.set(i, solutions.get(pos)); 
			solutions.set(pos, s);
		}
		this.setSolutions(solutions);
	 }
	 
	 public void makeSelection() {
		 this.parent1 = this.solutions.get(0);
		 this.parent2 = this.solutions.get(1);
	 }
	 
	 public void doCrossOver() {  
		System.out.println("------ start crossover ------ ");
		int c = (new Random()).nextInt(this.parent1.solution.length);
		int s1[] = this.parent1.solution; 
		int s2[] = this.parent2.solution; 
		
		for (int i = 0; i < c; i++) {
			int val = s1[i];
			s1[i] = s2[i];
			if(!changeRespectsConstrains(s1)) {
				s1[i] = val; 
			}
			int tempS2 = s2[i];
			s2[i] = val;	
			if(!changeRespectsConstrains(s2)) {	
				s2[i] = tempS2;
			}
		}
		
		this.parent1.setSolution(s1);
		this.parent2.setSolution(s2);
		System.out.println("------ result crossover ------ ");
		System.out.println("Parent 1: "+this.parent1.getScore(this.workers));
		System.out.println("Parent 2: "+this.parent2.getScore(this.workers));
	 }

	 
	 public void doMutation() {  
		System.out.println("------ start mutation ------ ");
		
		int s1[] = this.parent1.solution ; 
		int s2[] = this.parent2.solution ; 
		
		//1st parent
		int m = (new Random()).nextInt(this.parent1.solution.length-1);
		int s1p[] = s1.clone(); 
		int tempS1m = s1[m]; 
		s1p[m] = s1[m+1];
		s1p[m+1] = tempS1m;  
		if(changeRespectsConstrains(s1p)) {
			this.parent1.setSolution(s1p);
		}
		//2nd parent
		m = (new Random()).nextInt(this.parent1.solution.length-1);
		int s2p[] = s2.clone(); 
		int tempS2m = s2[m]; 
		s2p[m] = s2[m+1];
		s2p[m+1] = tempS2m;  
		if(changeRespectsConstrains(s2p)) {
			this.parent2.setSolution(s2p);
		}

		
		System.out.println("------ result mutation ------ ");
		System.out.println("Parent 1: "+this.parent1.getScore(this.workers));
		System.out.println("Parent 2: "+this.parent2.getScore(this.workers));
	 }
	 
	 
	 
	 
	 
	 /**
	  * Verify whether a solution respects problem's constrains 
	  * @param solutions
	  * @return 
	  */
	 protected boolean changeRespectsConstrains(int[] solutions) {
		boolean canProcess = true; 
		ArrayList<Worker> workers = this.workers;
		
		
	
		for (int jobIndex = 0; jobIndex < solutions.length; jobIndex++) {
			Job job = null; 
			for (int i = 0; i < this.jobs.size(); i++) {
				if(this.jobs.get(i).ID==jobIndex+1) {
					job = this.jobs.get(i);
				}
			} 
			Worker worker = null; 
			int workerIndex = -1;
			for (int j = 0; j < workers.size(); j++) { 
				if(workers.get(j).getBase10Name()==solutions[jobIndex]) {  
					worker = workers.get(j);  
					workerIndex = j; 
					break;  
				 }  
			} 
			
			if(worker.getAvailableDiskSize() > job.getRequiredDiskSizeForExecution() && 
				worker.getAvailableMemorySize() > job.getRequiredMemorySizeForExecution()) {
				
				worker.setAvailableMemorySize(worker.getAvailableMemorySize() - job.getRequiredMemorySizeForExecution());
				worker.setAvailableDiskSize(worker.getAvailableMemorySize()-job.getRequiredMemorySizeForExecution());
				
				workers.set(workerIndex, worker);
			}else {
				canProcess = false;
				break; 
			}
			
		} 
		
		return canProcess;
	}
	
	
}
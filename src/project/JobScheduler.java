package project;
 
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random; 

public class JobScheduler {
	ArrayList<Job> jobs = new ArrayList<Job>() ;
	ArrayList<Worker> workers = new ArrayList<Worker>() ;
	double costMatrix [][];
	
	//		j1  j2  j3  j4
	// w1   1   0   1   0   ==> 8*1+4*0+2*1+1*0 = 8+2 = 10 
	// w2	0   1   0   0   ==> 
	// w3   0   0   0   1
	
	public JobScheduler(ArrayList<Job> jobs, ArrayList<Worker> workers)  
	{
		this.jobs = jobs; 
		this.workers = workers; 
		this.costMatrix = new double[workers.size()][jobs.size()];
		initializeCostMatrix(); 
		
		// Long.valueOf(1 * this.getStandardProcessingDurations().get(this.getAssignedWorker().getCpuInfo().getFamilyName() + "-" + this.getAssignedWorker().getCpuInfo().getDenomination() + "-" + this.getAssignedWorker().getCpuInfo().getNumberOfCores())));  /* we create a new thread and makes it sleep for the amount of time required to process this job on its assigned worker PC */
		 
	}
	
	public void initializeCostMatrix() {
		for (int i = 0; i < this.workers.size(); i++) {
			Worker w = this.workers.get(i);
			System.out.println();
			for (int j = 0; j < this.jobs.size(); j++) {
				this.costMatrix[i][j] = this.jobs.get(j).getStandardProcessingDurations().
						get(w.getCpuInfo().getFamilyName() + "-" + w.getCpuInfo().getDenomination() + "-" + w.getCpuInfo().getNumberOfCores());
				System.out.print(this.costMatrix[i][j]);
				System.out.print("-");
				
			}
		}
	}
	
	public Population getPopulationInitial() {  
		/**  **/
		Population population = new Population(this.workers, this.jobs);
		int[] divs = new int[] { 5, 8, 14, 17, 22, 28}; 
		
		int lastLimit = 0; 
		for (int i = 0; i < divs.length; i++) { 
			
			GreedyAlgo greedyAlgo = new GreedyAlgo(this.jobs, this.workers, this.costMatrix);
			greedyAlgo.divideInGroup(lastLimit, divs[i]);
			
			SingleSolution singleSolution = greedyAlgo.greedyIteration(); 
			//singleSolution.showSolution();
			
			if(singleSolution.jobs.size()==this.jobs.size()) {
				population.addSolution(singleSolution);
			} 
			
			lastLimit =  divs[i]-2;   
		}
		
		return population; 
	}
	
	//5553//5596//5633 
	//5764 
	//5958//5952
	public void startGeneticAlg(Population p) {
		System.out.println("Taille de la population initiale: "+p.solutions.size());
		int conv = 0;
		while(conv<8000) {
			//calculate score// 
			System.out.println("Taille de la population: "+p.solutions.size());
			double scores [] = p.getScorePopulation();
			
			//Sort scores from the lowest to the highest
			p.sortScorePopulation();
			Population population = p.clone(); 
			
			//Make selection
			p.makeSelection(); 
			
			// Crossover
			p.doCrossOver(); 

			// Mutation
			//1st parent 
			p.doMutation();
			
			// Check score
			SingleSolution p1p = p.parent1;
			SingleSolution p2p = p.parent2;
			double score1 = p1p.getScore(this.workers);
			double score2 = p2p.getScore(this.workers);
			double scoreP1 = scores[0]; 
			double scoreP2 = scores[1]; 
			boolean oneSolution = false;  
			if((score1<scoreP1 || score1<scoreP2) &&  score1!=scoreP1 && score1 !=scoreP2) {
				System.out.println("add enfant 1 ");
				population.getSolutions().remove(population.getSolutions().size()-1);
				population.addSolution(p1p);
				oneSolution = true;
			}
			if((score2<scoreP1 || score2<scoreP2) && (score2!=scoreP1 && score2 !=scoreP2)) {
				System.out.println("add enfant 2 ");
				// Si le dernier parent était déjà enlevé
				if(oneSolution) {
					population.getSolutions().remove(population.getSolutions().size()-2); 
				}else { 
					population.getSolutions().remove(population.getSolutions().size()-1);
				}
				population.addSolution(p2p);
				oneSolution = true; 
			}
			if(!oneSolution) {
				conv++; 
			}else {
				conv=0;
			}
			
			p = population.clone();
			

			System.out.println("Score parent 1: "+scoreP1);
			System.out.println("Score parent 2: "+scoreP2);
			System.out.println("Score Enfant 1: "+score1);
			System.out.println("Score Enfant 2: "+score2);

			
		}
	}
	 

	
	
	
}

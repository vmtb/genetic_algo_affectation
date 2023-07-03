package project;
 
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random; 


public class JobScheduler {
	ArrayList<Job> jobs = new ArrayList<Job>() ;
	ArrayList<Worker> workers = new ArrayList<Worker>() ;
	protected double costMatrix [][];
	
	//		j1  j2  j3  j4
	// w1   1   0   1   0   ==> 8*1+4*0+2*1+1*0 = 8+2 = 10 
	// w2	0   1   0   0   ==> 
	// w3   0   0   0   1   ==>
	
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
		int[] divs = new int[] {5, 8, 14, 17, 22, 28}; 
		
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
	
	/**
	 * 9- 2169
	 * 18 - 3675
	 * 27 - 5148
	 * @param p
	 */
	public void startGeneticAlg(Population p) {
		System.out.println("Taille de la population initiale: "+p.solutions.size());
		int conv = 0;
		SingleSolution solutionFinal = null; 
		while(conv<1000) {
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
				// Si le dernier parent était déjà enlevé //
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
			
			population.sortScorePopulation();
			p = population.clone();
			solutionFinal = population.getSolutions().get(0);
						

			System.out.println("Score parent 1: "+scoreP1);
			System.out.println("Score parent 2: "+scoreP2);
			System.out.println("Score Enfant 1: "+score1);
			System.out.println("Score Enfant 2: "+score2);
		}
		System.out.println("Solution finale ");
		solutionFinal.showSolution2(this.workers);
		System.out.print(solutionFinal.getScore(this.workers));;
	}
	 

	
	
	
}

//Job 1 =====>  Worker 5  ====> (2169.0) 
//Job 2 =====>  Worker 4  ====> (833.0) 
//Job 3 =====>  Worker 4  ====> (765.0) 
//Job 4 =====>  Worker 4  ====> (337.0) 
//Job 5 =====>  Worker 3  ====> (427.0) 
//Job 6 =====>  Worker 3  ====> (803.0) 
//Job 7 =====>  Worker 1  ====> (1029.0) 
//Job 8 =====>  Worker 3  ====> (324.0) 
//Job 9 =====>  Worker 2  ====> (885.0)
//2169.0 ------- 0h36'09s (02'20s)

//Job 1 =====>  Worker 5  ====> (2169.0) 
//Job 2 =====>  Worker 4  ====> (833.0) 
//Job 3 =====>  Worker 4  ====> (765.0) 
//Job 4 =====>  Worker 1  ====> (1063.0) 
//Job 5 =====>  Worker 2  ====> (1342.0) 
//Job 6 =====>  Worker 5  ====> (414.0) 
//Job 7 =====>  Worker 2  ====> (694.0) 
//Job 8 =====>  Worker 2  ====> (518.0) 
//Job 9 =====>  Worker 1  ====> (1520.0) 
//Job 10 =====>  Worker 3  ====> (3261.0) 
//Job 11 =====>  Worker 4  ====> (833.0) 
//Job 12 =====>  Worker 4  ====> (765.0) 
//Job 13 =====>  Worker 5  ====> (289.0) 
//Job 14 =====>  Worker 5  ====> (287.0) 
//Job 15 =====>  Worker 4  ====> (479.0) 
//Job 16 =====>  Worker 5  ====> (255.0) 
//Job 17 =====>  Worker 5  ====> (199.0) 
//Job 18 =====>  Worker 2  ====> (885.0)
//3675.0 ------- 1h1'15s (07'55s)


//Job 1 =====>  Worker 5  ====> (2169.0)
//Job 2 =====>  Worker 4  ====> (833.0) 
//Job 3 =====>  Worker 5  ====> (675.0) 
//Job 4 =====>  Worker 5  ====> (289.0) 
//Job 5 =====>  Worker 5  ====> (287.0) 
//Job 6 =====>  Worker 5  ====> (414.0) 
//Job 7 =====>  Worker 4  ====> (293.0) 
//Job 8 =====>  Worker 5  ====> (199.0) 
//Job 9 =====>  Worker 3  ====> (668.0) 
//Job 10 =====>  Worker 2  ====> (4004.0) 
//Job 11 =====>  Worker 3  ====> (1227.0) 
//Job 12 =====>  Worker 4  ====> (765.0) 
//Job 13 =====>  Worker 3  ====> (423.0) 
//Job 14 =====>  Worker 5  ====> (287.0) 
//Job 15 =====>  Worker 5  ====> (414.0) 
//Job 16 =====>  Worker 1  ====> (1029.0) 
//Job 17 =====>  Worker 3  ====> (324.0) 
//Job 18 =====>  Worker 1  ====> (1520.0) 
//Job 19 =====>  Worker 4  ====> (2380.0) 
//Job 20 =====>  Worker 3  ====> (1227.0) 
//Job 21 =====>  Worker 4  ====> (765.0) 
//Job 22 =====>  Worker 3  ====> (423.0) 
//Job 23 =====>  Worker 3  ====> (427.0) 
//Job 24 =====>  Worker 5  ====> (414.0) 
//Job 25 =====>  Worker 1  ====> (1029.0) 
//Job 26 =====>  Worker 3  ====> (324.0) 
//Job 27 =====>  Worker 2  ====> (885.0)
//5148.0 =====> 1h25'48s (20'46)


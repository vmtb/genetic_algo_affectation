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
		
		/// Job {
		/// Required Memory Size For Execution
		/// Required Disk Size For Execution
		/// Standard Processing Duration On PC_i
		/// } 
		
		/// Winner {
		/// Available Memory Size  
		/// Available Disk
		/// Standard Processing Duration On PC_i
		/// }
		
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
	
	//9 jobs  vs 5 workers
	//Réitérer
	public Population getPopulationInitial() { 
		// Greedy Algorithm  
		/**  **/
		Population population = new Population(this.workers, this.jobs);
		int[] divs = new int[] { 4, 6, 8}; //, 14, 16 //4
		int M = this.jobs.size(); 
		int N = this.workers.size(); 
		
		int lastLimit = 0; 
		for (int i = 0; i < divs.length; i++) {
			int j = divs[i];
			
			
			ArrayList<ArrayList<Job>> jobClassesJobs = new ArrayList<ArrayList<Job>>();
			ArrayList<ArrayList<Worker>> workersClasses = new ArrayList<ArrayList<Worker>>();
			 
			// Les coeurs exécutent les threads 
			workersClasses.add(new ArrayList<Worker>());
			workersClasses.add(new ArrayList<Worker>());

			jobClassesJobs.add(new ArrayList<Job>());
			jobClassesJobs.add(new ArrayList<Job>());
			for (int k = 0; k < this.workers.size(); k++) {
				Worker  w = this.workers.get(k);
//				if(lastLimit <= w.cpuInfo.numberOfCores && w.cpuInfo.numberOfCores<j) {
//					workersClasses.get(0).add(w);
//				} else { 
//					workersClasses.get(1).add(w);
//				} 
				workersClasses.get(0).add(w); 
			} 

			for (int k = 0; k < this.jobs.size(); k++) {
				Job  jo = this.jobs.get(k);
				if(lastLimit <= jo.threadProcessCount && jo.threadProcessCount<j) {
					jobClassesJobs.get(0).add(jo);
				} else { 
					jobClassesJobs.get(1).add(jo);
				}  
			}
			

			System.out.println();
			System.out.println("Pour j "+j);
			
			lastLimit = 0;  
			
			SingleSolution singleSolution = greedyIteration(jobClassesJobs, workersClasses);
			singleSolution.showSolution();
			if(singleSolution.jobs.size()==this.jobs.size()) {
				population.addSolution(singleSolution);
			} 
		}
		
		return population;
		
		
		
	}
	
	public SingleSolution greedyIteration(ArrayList<ArrayList<Job>> jobClassesJobs, ArrayList<ArrayList<Worker>> workersClasses) {

		SingleSolution singleSolution = new SingleSolution(this.costMatrix);
		 

		System.out.println("workers");
		System.out.println(workersClasses.get(0).size());
		System.out.println(workersClasses.get(1).size());
		System.out.println("jobs");
		System.out.println(jobClassesJobs.get(0).size());
		System.out.println(jobClassesJobs.get(1).size());
		
		ArrayList<Worker> wksArrayList = workersClasses.get(0);
		wksArrayList = sortWorkers(wksArrayList); 
		
		for (int k = 0; k < 2; k++) {

			
			ArrayList<Job> jbsArrayList = jobClassesJobs.get(k); 
			for (int l = 0; l < jbsArrayList.size(); l++) {
				double theta = 0;
				for (int l2 = 0; l2 < wksArrayList.size()-1; l2++) {
					int newIndexL = wksArrayList.get(l2).ID-1;
					int newIndexLplusUN = wksArrayList.get(l2+1).ID-1;
					theta += this.costMatrix[newIndexL][l]/this.costMatrix[newIndexLplusUN][l];
				}
				Job job = jbsArrayList.get(l); 
				job.setTheta(theta);
				jbsArrayList.set(l, job);
			}
			
			jbsArrayList = sortJobs(jbsArrayList); 
			for (int l = 0; l < jbsArrayList.size(); l++) {
				Job job = jbsArrayList.get(l);  
				double miniMum = Double.MAX_VALUE; 
				int currentIndex = -1 ;
				int newIndexL = jbsArrayList.get(l).ID-1;
				for (int l2 = 0; l2 < wksArrayList.size(); l2++) {
					int newIndexL2 = wksArrayList.get(l2).ID-1;
					if(miniMum>=this.costMatrix[newIndexL2][newIndexL] 
							&& 
							wksArrayList.get(l2).getAvailableDiskSize() >= job.getRequiredDiskSizeForExecution() 
							&& 
							wksArrayList.get(l2).getAvailableMemorySize() >= job.getRequiredMemorySizeForExecution() ) {
						miniMum = this.costMatrix[newIndexL2][newIndexL]; 
						currentIndex = l2; 
					}
				}
				
				System.out.println("For JOB "+job.ID);
				if(currentIndex>=0) {
					Worker worker = wksArrayList.get(currentIndex);
					worker.setAvailableDiskSize(worker.getAvailableDiskSize()-job.getRequiredDiskSizeForExecution());
					worker.setAvailableMemorySize(worker.getAvailableMemorySize()-job.getRequiredMemorySizeForExecution());
					job.setAssignedWorker(worker);
					wksArrayList.set(currentIndex, worker);
					
					jbsArrayList.set(l, job);
					singleSolution.addJob(job);
				} else { 
				} 
			}   
		} 
		return singleSolution;  
	}
	
	//5713
	//5736
	//5719
	//5708
	//5553
	//5596
	public void startGeneticAlg(Population p) {
		System.out.println("Taille de la population initiale: "+p.solutions.size());
		int conv = 0;
		while(conv<80000) {
			Population population = p.clone();
			//calculate score// 
			System.out.println("Taille de la population: "+p.solutions.size());
			double scores [] = p.getScorePopulation();
			
			//Sort scores from the lowest to the highest
			p.sortScorePopulation();
			
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
				population.addSolution(p1p);
				oneSolution = true;
			}
			if((score2<scoreP1 || score2<scoreP2) && (score2!=scoreP1 && score2 !=scoreP2)) {
				System.out.println("add enfant 2 ");
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
	 

	public ArrayList<Worker>  sortWorkers(ArrayList<Worker> worker){ 
		for (int i = 0; i < worker.size(); i++) {
			int minTime = worker.get(i).cpuInfo.getNumberOfCores();
			int pos = i; 
			for (int j = i; j < worker.size(); j++) {
				int processTime = worker.get(j).cpuInfo.getNumberOfCores();
				if(minTime>processTime) {
					minTime = processTime;
					pos = j;
				}
				
			}
			Worker tempWorker = worker.get(i);
			worker.set(i, worker.get(pos));
			worker.set(pos, tempWorker);
		}
		
		return worker;
	}

	public ArrayList<Job>  sortJobs(ArrayList<Job> worker){ 
		for (int i = 0; i < worker.size(); i++) {
			double minTime = worker.get(i).getTheta();
			int pos = i; 
			for (int j = i; j < worker.size(); j++) {
				double processTime = worker.get(j).getTheta();
				if(minTime>processTime) {
					minTime = processTime;
					pos = j;
				} 
			}
			Job tempWorker = worker.get(i);
			worker.set(i, worker.get(pos));
			worker.set(pos, tempWorker);
		}
		
		return worker;
	}
	
	
	
}

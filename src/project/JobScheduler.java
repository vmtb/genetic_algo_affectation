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
		Population population = new Population();
		int[] divs = new int[] { 5, 8, 10 }; //, 14, 16 //4
		int M = this.jobs.size(); 
		int N = this.workers.size(); 
		
		int lastLimit = 0; 
		for (int i = 0; i < divs.length; i++) {
			int j = divs[i];
			
			SingleSolution singleSolution = new SingleSolution(this.costMatrix);
			
			ArrayList<ArrayList<Job>> jobClassesJobs = new ArrayList<ArrayList<Job>>();
			ArrayList<ArrayList<Worker>> workersClasses = new ArrayList<ArrayList<Worker>>();
			
			// worker minimumCoreNumber<>mmaximumCoreNumber
			// job minimumThreadCount  <>mmaximumThreadCount
			// Les coeurs exécutent les threads 
			workersClasses.add(new ArrayList<Worker>());
			workersClasses.add(new ArrayList<Worker>());

			jobClassesJobs.add(new ArrayList<Job>());
			jobClassesJobs.add(new ArrayList<Job>());
			for (int k = 0; k < this.workers.size(); k++) {
				Worker  w = this.workers.get(k);
				if(lastLimit <= w.cpuInfo.numberOfCores && w.cpuInfo.numberOfCores<j) {
					workersClasses.get(0).add(w);
				} else { 
					workersClasses.get(1).add(w);
				} 
			}
			if(workersClasses.get(0).size()==1) {
				//workersClasses.set(0, new ArrayList<Worker>());
				//workersClasses.get(1).add(workersClasses.get(0).get(0));
			}

			for (int k = 0; k < this.jobs.size(); k++) {
				Job  jo = this.jobs.get(k);
				if(lastLimit <= jo.threadProcessCount && jo.threadProcessCount<j) {
					jobClassesJobs.get(0).add(jo);
				} else { 
					jobClassesJobs.get(1).add(jo);
				}  
			}
			
			
			lastLimit = 5; 
			System.out.println();
			System.out.println("Pour j "+j);
			System.out.println("workers");
			System.out.println(workersClasses.get(0).size());
			System.out.println(workersClasses.get(1).size());
			System.out.println("jobs");
			System.out.println(jobClassesJobs.get(0).size());
			System.out.println(jobClassesJobs.get(1).size());
			if(workersClasses.get(0).size()==0 || workersClasses.get(1).size()==0) {
				// continue;
			}
			
			for (int k = 0; k < 2; k++) {
				ArrayList<Worker> wksArrayList = workersClasses.get(k);
				wksArrayList = sortWorkers(wksArrayList);
				

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
						if(miniMum>this.costMatrix[newIndexL2][newIndexL] 
								&& 
								wksArrayList.get(l2).getAvailableDiskSize() > job.getRequiredDiskSizeForExecution() 
								&& 
								wksArrayList.get(l2).getAvailableMemorySize() > job.getRequiredMemorySizeForExecution() ) {
							miniMum = this.costMatrix[newIndexL2][newIndexL]; 
							currentIndex = l2; 
						}
					}
					
					System.out.println("For JOB "+job.ID);
					if(wksArrayList.size()>0 && currentIndex>=0) {
						Worker worker = wksArrayList.get(currentIndex);
						worker.setAvailableDiskSize(worker.getAvailableDiskSize()-job.getRequiredDiskSizeForExecution());
						worker.setAvailableMemorySize(worker.getAvailableMemorySize()-job.getRequiredMemorySizeForExecution());
						job.setAssignedWorker(worker);
						wksArrayList.set(currentIndex, worker);
						
						jbsArrayList.set(l, job);
						singleSolution.addJob(job);
					}
				}  
			}
			
			singleSolution.showSolution();
			if(singleSolution.jobs.size()==this.jobs.size()) {
				population.addSolution(singleSolution);
			} 
		}
		
		return population;
		
		
		
	}
	
	//5713
	public void startGeneticAlg(Population p) {
		System.out.println("Taille de la population initiale: "+p.solutions.size());
		int conv = 0;
		while(conv<10) {
			
			//calculate score// 
			System.out.println("Taille de la population: "+p.solutions.size());
			double scores [] = new double[p.solutions.size()];
			for (int i = 0; i < scores.length; i++) {
				scores[i] = p.getSolutions().get(i).getScore(this.workers);
			}
			
			//Sort scores from the lowest to the highest
			ArrayList<SingleSolution> solutions = p.solutions; 
			for (int i = 0; i < scores.length; i++) {
				double min = scores[i]; 
				int pos = i; 
				for (int j = i; j < scores.length; j++) {
					if(scores[j]<min) {
						min= scores[j]; 
						pos = j; 
					}
				}
				scores[pos] = scores[i];
				scores[i] = min; 
				SingleSolution s = solutions.get(i);
				solutions.set(i, solutions.get(pos)); 
				solutions.set(pos, s);
			}
			p.setSolutions(solutions);
			
			Population population = p.clone();
			
			// Crossover
			SingleSolution p1 = p.getSolutions().get(0);
			SingleSolution p2 = p.getSolutions().get(1);
			SingleSolution p1p = p1;
			SingleSolution p2p = p2;
			int c = (new Random()).nextInt(p1.solution.length);
			int s1[] = p1.solution; 
			int s2[] = p2.solution; 
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
			p1p.setSolution(s1);
			p2p.setSolution(s2);
			
			
			// Mutation
			//1st parent
			int m = (new Random()).nextInt(p1.solution.length-1);
			int s1p[] = s1; 
			int tempS1m = s1[m]; 
			s1p[m] = s1[m+1];
			s1p[m+1] = tempS1m;  
			if(changeRespectsConstrains(s1p)) {
				p1p.setSolution(s1p);
			}
			//2nd parent
			m = (new Random()).nextInt(p1.solution.length-1);
			int s2p[] = s2; 
			int tempS2m = s2[m]; 
			s2p[m] = s2[m+1];
			s2p[m+1] = tempS2m;  
			if(changeRespectsConstrains(s2p)) {
				p2p.setSolution(s2p);
			}

			
			System.out.println("after crossover");
			for (int i = 0; i < p.solutions.size(); i++) {
				 p.solutions.get(i).getScore(this.workers);
			}
			System.out.println("end after crossover");
			
			System.out.println("after mutation");
			for (int i = 0; i < population.solutions.size(); i++) {
				population.solutions.get(i).getScore(this.workers);
			}
			System.out.println("end after mutation");
			
			
			
			// Check score
			double score1 = p1p.getScore(this.workers);
			double score2 = p2p.getScore(this.workers);
			double scoreP1 = scores[0]; 
			double scoreP2 = scores[1]; 
			boolean oneSolution = false;  
			if((score1<scoreP1 || score1<scoreP2) &&  score1!=scoreP1 && score1 !=scoreP2) {
				System.out.println("add enfant 1 ");
				p.addSolution(p2p);
				oneSolution = true;
			}
			if((score2<scoreP1 || score2<scoreP2) && (score2!=scoreP1 && score2 !=scoreP2)) {
				System.out.println("add enfant 2 ");
				p.addSolution(p2p);
				oneSolution = true; 
			}
			if(!oneSolution) {
				conv++; 
			}
			

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
	
	public boolean changeRespectsConstrains(int[] solutions) {
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

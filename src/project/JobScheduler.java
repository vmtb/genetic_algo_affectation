package project;
 
import java.util.ArrayList; 

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
	public void getPopulationInitial() { 
		// Greedy Algorithm  
		/**  **/
		int[] divs = new int[] {2, 4, 6, 8, 10, 12}; //, 14, 16
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
				}else {
					workersClasses.get(1).add(w);
				} 
			}
			

			for (int k = 0; k < this.jobs.size(); k++) {
				Job  jo = this.jobs.get(k);
				if(lastLimit <= jo.threadProcessCount && jo.threadProcessCount<j) {
					jobClassesJobs.get(0).add(jo);
				}else {
					jobClassesJobs.get(1).add(jo);
				} 
			}
			
			
			lastLimit = j; 
			
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
					int currentIndex = 0;
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
					if(wksArrayList.size()>0) {
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

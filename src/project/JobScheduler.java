package project;
 
import java.util.ArrayList; 

public class JobScheduler {
	ArrayList<Job> jobs = new ArrayList<Job>() ;
	ArrayList<Worker> workers = new ArrayList<Worker>() ;

	public JobScheduler(ArrayList<Job> jobs, ArrayList<Worker> workers)  
	{
		this.jobs = jobs; 
		this.workers = workers; 
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
		}
		
		
		
	}
	
	
}
